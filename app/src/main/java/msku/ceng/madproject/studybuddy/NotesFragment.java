package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyNotesRecyclerViewAdapter adapter;
    private List<Note> noteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public NotesFragment() { }

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
        if (mAuth.getCurrentUser() == null) return;

        String currentUserId = mAuth.getCurrentUser().getUid();

        // ARTIK 'posts' KOLEKSİYONUNDAN ÇEKİYORUZ
        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("postType", "NOTE") // Sadece NOT olanları getir
                .addSnapshotListener((value, error) -> { // get() yerine addSnapshotListener ile anlık güncelleme
                    if (error != null) return;

                    if (value != null) {
                        noteList.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Note note = snapshot.toObject(Note.class);

                            // KRİTİK: Silme işlemi için ID'yi Firebase belgesinden alıp objeye koymalıyız
                            note.setNoteId(snapshot.getId());

                            noteList.add(note);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}