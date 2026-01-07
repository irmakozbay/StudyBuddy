package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGroupsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<Group> myGroups = new ArrayList<>();
    private MyGroupAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        setupNavbar();

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_my_groups);
        ImageButton btnBack = findViewById(R.id.btn_back);

        adapter = new MyGroupAdapter(myGroups);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        fetchMyGroups();
    }

    @Override
    protected void onProfileRequest() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("OPEN_FRAGMENT", "PROFILE");
        startActivity(intent);
    }

    private void fetchMyGroups() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uId).collection("my_groups")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        myGroups.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Group group = doc.toObject(Group.class);
                            if (group != null) {
                                group.setId(doc.getId());
                                myGroups.add(group);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void leaveGroup(Group group) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uId)
                .collection("my_groups")
                .document(group.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "You have left " + group.getName(), Toast.LENGTH_SHORT).show();
                    sendLeaveNotification(group.getName());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void sendLeaveNotification(String groupName) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Left Group");
        notification.put("message", "You successfully left the group: " + groupName);
        notification.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(uId).collection("notifications").add(notification);
    }

    private class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.ViewHolder> {
        private List<Group> list;
        public MyGroupAdapter(List<Group> list) { this.list = list; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Group g = list.get(position);
            holder.name.setText(g.getName());
            holder.desc.setText(g.getDescription());

            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(MyGroupsActivity.this)
                        .setTitle("Leave Group")
                        .setMessage("Are you sure you want to leave " + g.getName() + "?")
                        .setPositiveButton("Leave", (dialog, which) -> leaveGroup(g))
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, desc;
            public ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tv_group_name);
                desc = v.findViewById(R.id.tv_group_description);
            }
        }
    }
}