package msku.ceng.madproject.studybuddy;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import msku.ceng.madproject.studybuddy.databinding.ItemNoteBinding; //

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
        
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getContent());

        holder.deleteButton.setOnClickListener(v -> {
            // 1. Notun ID'sini al
            ArrayList<Object> noteList = new ArrayList<>();
            String noteId = noteList.get(position).getClass(); // Model sınıfınızda noteId tuttuğunuzu varsayıyorum

            // 2. Firebase'den sil
            Context context;
            FirebaseFirestore.getInstance().collection("Notes").document(noteId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // 3. Başarılı olursa listeyi güncelle
                        noteList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, noteList.size());
                        Toast.makeText(context, "Not silindi", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Hata oluştu", Toast.LENGTH_SHORT).show();
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
        public Note mItem;
        public View deleteButton;

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.noteTitle;
            mContentView = binding.noteContent;
        }
    }
}