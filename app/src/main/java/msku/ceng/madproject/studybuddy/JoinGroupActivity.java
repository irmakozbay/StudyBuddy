package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class JoinGroupActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<Group> groupList = new ArrayList<>();
    private List<Group> fullGroupList = new ArrayList<>();
    private GroupAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        setupNavbar();

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_recommended_groups);
        ImageButton btnBack = findViewById(R.id.btn_back);
        EditText editTextSearch = findViewById(R.id.et_search);

        adapter = new GroupAdapter(groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        fetchGroupsFromFirebase();
    }

    @Override
    protected void onProfileRequest() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("OPEN_FRAGMENT", "PROFILE");
        startActivity(intent);
    }

    private void fetchGroupsFromFirebase() {
        db.collection("groups").addSnapshotListener((value, error) -> {
            if (value != null) {
                fullGroupList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Group group = doc.toObject(Group.class);
                    if (group != null) {
                        // KRİTİK: Firebase'den gelen döküman ID'sini modele set ediyoruz
                        group.setId(doc.getId());
                        fullGroupList.add(group);
                    }
                }
                groupList.clear();
                groupList.addAll(fullGroupList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void filter(String text) {
        List<Group> filteredList = new ArrayList<>();
        for (Group item : fullGroupList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) { filteredList.add(item); }
        }
        adapter.updateList(filteredList);
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
        private List<Group> list;
        public GroupAdapter(List<Group> list) { this.list = list; }
        public void updateList(List<Group> newList) { this.list = newList; notifyDataSetChanged(); }

        @NonNull
        @Override
        public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new GroupViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
            Group group = list.get(position);
            holder.tvName.setText(group.getName());
            holder.tvDesc.setText(group.getDescription());
            // İkon varsa set et
            holder.itemView.setOnClickListener(v -> {
                new AlertDialog.Builder(JoinGroupActivity.this)
                        .setTitle("Join Group")
                        .setMessage("Join " + group.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> joinGroup(group))
                        .setNegativeButton("No", null).show();
            });
        }

        private void joinGroup(Group group) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(JoinGroupActivity.this, "Please login first!", Toast.LENGTH_SHORT).show();
                return;
            }

            String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Eğer group.getId() hala null ise döküman oluşturulamaz
            String groupId = group.getId();
            if (groupId == null) {
                Toast.makeText(JoinGroupActivity.this, "Error: Group ID is null", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users").document(uId)
                    .collection("my_groups")
                    .document(groupId)
                    .set(group)
                    .addOnSuccessListener(aVoid -> Toast.makeText(JoinGroupActivity.this, "Joined successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(JoinGroupActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        @Override public int getItemCount() { return list.size(); }
        class GroupViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDesc; ImageView imgIcon;
            public GroupViewHolder(@NonNull View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_group_name);
                tvDesc = v.findViewById(R.id.tv_group_description);
                imgIcon = v.findViewById(R.id.img_group_icon);
            }
        }
    }
}