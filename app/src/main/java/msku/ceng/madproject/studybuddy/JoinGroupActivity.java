package msku.ceng.madproject.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class JoinGroupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Group> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        recyclerView = findViewById(R.id.recycler_recommended_groups);
        ImageButton btnBack = findViewById(R.id.btn_back);

        groupList = new ArrayList<>();
        prepareGroupData();

        GroupAdapter adapter = new GroupAdapter(this, groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.setNestedScrollingEnabled(false);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    public static class Group {
        private String name;
        private String description;
        private int iconResId;

        public Group(String name, String description, int iconResId) {
            this.name = name;
            this.description = description;
            this.iconResId = iconResId;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getIconResId() { return iconResId; }
    }


    public static class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
        private final Context context;
        private final List<Group> groupList;

        public GroupAdapter(Context context, List<Group> groupList) {
            this.context = context;
            this.groupList = groupList;
        }

        @NonNull
        @Override
        public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
            return new GroupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
            Group group = groupList.get(position);
            holder.tvGroupName.setText(group.getName());
            holder.tvGroupDescription.setText(group.getDescription());
            holder.imgGroupIcon.setImageResource(group.getIconResId());

            holder.itemView.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Join Group")
                        .setMessage("Do ypu want to the join this group")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Toast.makeText(context, "You have joined the group! ✅", Toast.LENGTH_SHORT).show();
                            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9"));
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return groupList.size();
        }

        public static class GroupViewHolder extends RecyclerView.ViewHolder {
            TextView tvGroupName;
            TextView tvGroupDescription;
            ImageView imgGroupIcon;

            public GroupViewHolder(@NonNull View itemView) {
                super(itemView);
                tvGroupName = itemView.findViewById(R.id.tv_group_name);
                tvGroupDescription = itemView.findViewById(R.id.tv_group_description);
                imgGroupIcon = itemView.findViewById(R.id.img_group_icon);
            }
        }
    }


    private void prepareGroupData() {
        int icon = R.drawable.profile;

        groupList.add(new Group("Engineering Buddies", "Focused on calculus and physics.", icon));
        groupList.add(new Group("Study & Chill", "Collaborative study sessions for everyone.", icon));
        groupList.add(new Group("Exam Prep Squad", "Sharing notes and tips before exams.", icon));
        groupList.add(new Group("Code & Coffee", "Java and Python learning group.", icon));
        groupList.add(new Group("Math Wizards", "Solving complex algebra problems.", icon));
        groupList.add(new Group("Design Thinkers", "UI/UX design basics and feedback.", icon));
        groupList.add(new Group("History Buffs", "Discussing world history and essays.", icon));
    }
}