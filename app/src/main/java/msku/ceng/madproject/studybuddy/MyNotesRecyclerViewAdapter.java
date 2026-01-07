package msku.ceng.madproject.studybuddy;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

        holder.deleteButton.setOnClickListener(v -> {
            Context context = v.getContext();
            String noteId = note.getNoteId();

            if (noteId == null || noteId.isEmpty()) {
                Toast.makeText(context, "Hata: Not ID bulunamadı", Toast.LENGTH_SHORT).show();
                return;
            }

            // DÜZELTME: "notes" yerine "posts" koleksiyonundan siliyoruz
            FirebaseFirestore.getInstance().collection("posts").document(noteId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Not silindi", Toast.LENGTH_SHORT).show();
                        // Not: addSnapshotListener kullandığımız için remove işlemini
                        // manuel yapmaya gerek kalmayabilir, liste otomatik güncellenir.
                        // Ama görsel akıcılık için durabilir.
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
        public final View deleteButton;

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.noteTitle;
            mContentView = binding.noteContent;

            // XML'de id'si neyse onu kullan (Örn: btnDelete)
            // Eğer binding'de bulamıyorsa binding.btnDeleteNote gibi kontrol et
            deleteButton = binding.getRoot(); // Burayı kendi XML id'ne göre güncelle!
        }
    }
}