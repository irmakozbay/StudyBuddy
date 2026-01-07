package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MyGroupsActivity extends BaseActivity{
    private RecyclerView recyclerView;
    private List<Group> myGroups = new ArrayList<>();
    private MyGroupAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_my_groups);
        ImageButton btnBack = findViewById(R.id.btn_back);

        adapter = new MyGroupAdapter(myGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        fetchMyGroups();
    }

    private void fetchMyGroups() {
        db.collection("users").document("user_1")
                .collection("my_groups")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        myGroups.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            myGroups.add(doc.toObject(Group.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onProfileRequest() {

    }

    private class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.ViewHolder> {
        private List<Group> list;
        public MyGroupAdapter(List<Group> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Group g = list.get(position);
            holder.name.setText(g.getName());
            holder.desc.setText(g.getDescription());
            holder.icon.setImageResource(g.getIconResId());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, desc;
            ImageView icon;
            public ViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.tv_group_name);
                desc = v.findViewById(R.id.tv_group_description);
                icon = v.findViewById(R.id.img_group_icon);
            }
        }
    }
}