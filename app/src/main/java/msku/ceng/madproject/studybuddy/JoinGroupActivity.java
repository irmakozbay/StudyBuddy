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
        groupList = new ArrayList<>();

        prepareGroupData();

        GroupAdapter adapter = new GroupAdapter(this, groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.setNestedScrollingEnabled(false);

        ImageButton btnBack = findViewById(R.id.btn_back);
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
                        .setMessage(group.getName() + " Do you want to join this group?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Toast.makeText(context, group.getName() + " You have joined the group! ✅", Toast.LENGTH_SHORT).show();

                            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9"));
                        })
                        .setNegativeButton("No", null)
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

    // ----------------------------------------------------
    // *** ÖRNEK VERİLER ***
    // ----------------------------------------------------
    private void prepareGroupData() {
        // İKON NOTU: R.drawable.profile yerine projendeki gerçek ikon adını yazmalısın.
        int icon = R.drawable.profile;

        groupList.add(new Group("Engineering Buddies", "Focused on first-year engineering students who mostly study calculus.", icon));
        groupList.add(new Group("Study & Chill", "Collaborative study sessions and stay productive.", icon));
        groupList.add(new Group("Exam Prep Squad", "Sharing notes and summaries before exams.", icon));
        groupList.add(new Group("Code & Coffee", "For beginner programmers learning Java and Python.", icon));
        groupList.add(new Group("Math Wizards", "Solving calculus and algebra problems together.", icon));
        groupList.add(new Group("Design Thinkers", "Exploring UI/UX design basics.", icon));
        groupList.add(new Group("Chemistry Pros", "Problem-solving for chemistry majors.", icon));
    }
}