package msku.ceng.madproject.studybuddy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyNotesRecyclerViewAdapter adapter;
    private List<Note> noteList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FloatingActionButton fabAddNote; // FAB Tanımladık

    public NotesFragment() {
        // Boş constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        // View tanımlamaları
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


    // Firebase'e Kaydetme Fonksiyonu
    private void saveNoteToFirebase(String title, String content) {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        // Önce kullanıcının adını 'users' koleksiyonundan çekelim
        // Çünkü notu kaydederken ismini de ekleyeceğiz.
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = "Anonim Öğrenci";
                    if (documentSnapshot.exists() && documentSnapshot.getString("fullName") != null) {
                        userName = documentSnapshot.getString("fullName");
                    }

                    // Not nesnesini oluştur
                    String noteId = UUID.randomUUID().toString(); // Rastgele ID
                    // Not modelinde userName alanını eklediğini varsayıyorum!
                    Note newNote = new Note(noteId, title, content, userId);
                    newNote.setUserName(userName); // Notu kimin yazdığını ekle

                    // Veritabanına yaz
                    db.collection("notes").document(noteId).set(newNote)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Not paylaşıldı!", Toast.LENGTH_SHORT).show();
                                loadNotes(); // Listeyi yenile
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void loadNotes() {

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tüm notları çek (Filtre yok, herkes görebilir)
        db.collection("notes")
                .whereEqualTo("userId", currentUserId) // "userId" veritabanınızda kullanıcı id'sini tuttuğunuz alan olmalı
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        noteList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Note note = snapshot.toObject(Note.class);
                            noteList.add(note);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}