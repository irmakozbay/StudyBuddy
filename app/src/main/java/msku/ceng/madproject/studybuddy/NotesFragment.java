package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyNotesRecyclerViewAdapter adapter;
    private List<Note> noteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public NotesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        recyclerView = view.findViewById(R.id.notesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        noteList = new ArrayList<>();
        adapter = new MyNotesRecyclerViewAdapter(noteList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadNotes();

        return view;
    }

    private void loadNotes() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("posts")
                .whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("postType", "NOTE")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null) {
                        noteList.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Note note = snapshot.toObject(Note.class);

                            if (note != null) {
                                note.setNoteId(snapshot.getId());
                                noteList.add(note);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}