package msku.ceng.madproject.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Geri butonu için
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Opsiyonel: Toast mesajları için
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import msku.ceng.madproject.studybuddy.R;

public class JoinGroupActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Group> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_join_group.xml layout dosyasını bağlıyoruz
        setContentView(R.layout.activity_join_group);

        recyclerView = findViewById(R.id.recycler_recommended_groups);
        groupList = new ArrayList<>();

        // 1. Veri listesini doldur
        prepareGroupData();

        // 2. Adapter'ı başlat ve RecyclerView'a bağla
        GroupAdapter adapter = new GroupAdapter(this, groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ÖNEMLİ ÇÖZÜM: ScrollView içindeki RecyclerView'ın sorunsuz kayması için
        recyclerView.setNestedScrollingEnabled(false);

        // Geri Butonu işlevselliği
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish()); // Activity'i kapatıp bir önceki ekrana döner
        }
    }

    // ----------------------------------------------------
    // *** 1. GROUP MODELİ (VERİ YAPISI) ***
    // ----------------------------------------------------

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

    // ----------------------------------------------------
    // *** 2. GROUP ADAPTER'I (BAĞLAYICI) ***
    // ----------------------------------------------------

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
            // item_group.xml layout dosyasını kullandığınız varsayılmıştır.
            View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
            return new GroupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
            Group group = groupList.get(position);
            holder.tvGroupName.setText(group.getName());
            holder.tvGroupDescription.setText(group.getDescription());
            holder.imgGroupIcon.setImageResource(group.getIconResId());

            // Tıklama işlevi eklenebilir
            holder.itemView.setOnClickListener(v ->
                    Toast.makeText(context, group.getName() + " grubuna katılınıyor...", Toast.LENGTH_SHORT).show()
            );
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
                // item_group.xml içindeki ID'lerinizin bu şekilde olduğunu varsayıyoruz
                tvGroupName = itemView.findViewById(R.id.tv_group_name);
                tvGroupDescription = itemView.findViewById(R.id.tv_group_description);
                imgGroupIcon = itemView.findViewById(R.id.img_group_icon);
            }
        }
    }

    // ----------------------------------------------------
    // *** VERİ OLUŞTURMA METODU ***
    // ----------------------------------------------------

    private void prepareGroupData() {
        // R.drawable.profile yerine kendi ikon dosyanızı kullanın.
        // Bu ikonların projenizde (res/drawable altında) var olması GEREKİR.
        int defaultIcon = R.drawable.profile; // Veya kullandığınız ikonun adı

        groupList.add(new Group("Engineering Buddies", "Focused on first-year engineering students who mostly study calculus.", defaultIcon));
        groupList.add(new Group("Study & Chill", "Collaborative study sessions and stay productive with friendly accountability.", defaultIcon));
        groupList.add(new Group("Exam Prep Squad", "Focused on sharing notes, summaries, and tips before upcoming exams.", defaultIcon));
        groupList.add(new Group("Code & Coffee", "For beginner programmers who love learning Java and Python with others.", defaultIcon));
        groupList.add(new Group("Math Wizards", "Collaborate on solving calculus, algebra, and geometry problems.", defaultIcon));
        groupList.add(new Group("Design Thinkers", "A creative community for students exploring UI/UX design basics.", defaultIcon));
        groupList.add(new Group("Chemistry Pros", "Advanced topics and problem-solving sessions for chemistry majors.", defaultIcon));
        groupList.add(new Group("History Buffs", "Discussing world history and preparing for essay exams.", defaultIcon));
    }
}