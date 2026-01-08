package msku.ceng.madproject.studybuddy;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView; // ImageView eklendi
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import msku.ceng.madproject.studybuddy.databinding.ItemNoteBinding;

public class MyNotesRecyclerViewAdapter extends RecyclerView.Adapter<MyNotesRecyclerViewAdapter.ViewHolder> {

    private final List<Note> mValues;

    public MyNotesRecyclerViewAdapter(List<Note> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Note note = mValues.get(position);

        holder.mTitleView.setText(note.getTitle());
        holder.mContentView.setText(note.getContent());

        // 1. Karta Tıklama (Görüntüleme/Düzenleme)
        // Karta Tıklama (Detay Açma)
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PostDetailActivity.class);

            // Verileri paketleyip gönderiyoruz
            intent.putExtra("title", note.getTitle());
            intent.putExtra("content", note.getContent());

            context.startActivity(intent);
        });

        // 2. Silme Butonuna Tıklama (Silme İşlemi)
        holder.deleteButton.setOnClickListener(v -> {
            Context context = v.getContext();
            String noteId = note.getNoteId();

            if (noteId == null || noteId.isEmpty()) {
                Toast.makeText(context, "Hata: Not ID bulunamadı", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore'dan silme işlemi ("posts" koleksiyonundan)
            FirebaseFirestore.getInstance().collection("posts").document(noteId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Not başarıyla silindi", Toast.LENGTH_SHORT).show();

                        // Listeden anlık silmek için:
                        mValues.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mValues.size());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Silinemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitleView;
        public final TextView mContentView;
        public final ImageView deleteButton; // View yerine ImageView kullanmak daha iyi

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.noteTitle;
            mContentView = binding.noteContent;

            // XML'de verdiğimiz yeni ID'yi buraya bağlıyoruz
            deleteButton = binding.btnDeleteNote;
        }
    }
}