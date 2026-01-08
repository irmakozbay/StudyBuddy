package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
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

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("title", note.getTitle());
            intent.putExtra("content", note.getContent());

            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            Context context = v.getContext();
            String noteId = note.getNoteId();

            if (noteId == null || noteId.isEmpty()) {
                Toast.makeText(context, "Error: Note id cannot found.", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore.getInstance().collection("posts").document(noteId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Note deleted successfully.", Toast.LENGTH_SHORT).show();

                        mValues.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mValues.size());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Deletion failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        public final ImageView deleteButton;

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.noteTitle;
            mContentView = binding.noteContent;

            deleteButton = binding.btnDeleteNote;
        }
    }
}