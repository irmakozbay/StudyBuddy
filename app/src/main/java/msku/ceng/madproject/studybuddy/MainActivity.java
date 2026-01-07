package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private Button btnJoinGroups, btnCreateGroup, btnStatistics, btnAddPost, btnActivities;
    private View notificationBadge; // XML'de kırmızı bir nokta (View) olmalı
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        setupNavbar();

        // UI Elements
        btnJoinGroups = findViewById(R.id.btnJoinGroups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnActivities = findViewById(R.id.btnActivities);
        notificationBadge = findViewById(R.id.notificationBadge); // Kırmızı nokta ID'si

        // Listeners
        btnJoinGroups.setOnClickListener(v -> startActivity(new Intent(this, JoinGroupActivity.class)));
        btnActivities.setOnClickListener(v -> startActivity(new Intent(this, UpdateStatsActivity.class)));
        btnCreateGroup.setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));
        btnStatistics.setOnClickListener(v -> startActivity(new Intent(this, ActivityStatistics.class)));

        if ("PROFILE".equals(getIntent().getStringExtra("OPEN_FRAGMENT"))) {
            onProfileRequest();
        }

        // Özellikleri Çalıştır
        listenForNotifications();
        checkAndSendWeeklySummary();
    }

    // YENİ: Bildirim gelince kırmızı noktayı gösterir
    private void listenForNotifications() {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uId).collection("notifications")
                .addSnapshotListener((value, error) -> {
                    if (value != null && !value.isEmpty()) {
                        if (notificationBadge != null) notificationBadge.setVisibility(View.VISIBLE);
                    }
                });
    }

    // YENİ: Haftalık Özet Bildirimi Kontrolü
    private void checkAndSendWeeklySummary() {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Son 7 günün başlangıç tarihini hesapla
        Calendar cal = Calendar.getInstance();
        long now = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long sevenDaysAgo = cal.getTimeInMillis();

        db.collection("users").document(uId).collection("study_logs")
                .whereGreaterThanOrEqualTo("timestamp", sevenDaysAgo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double weeklyTotal = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double hours = doc.getDouble("hours");
                        if (hours != null) weeklyTotal += hours;
                    }

                    if (weeklyTotal > 0) {
                        sendSummaryNotification(weeklyTotal);
                    }
                });
    }

    private void sendSummaryNotification(double totalHours) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> notif = new HashMap<>();
        notif.put("title", "Weekly Summary 📊");
        notif.put("message", String.format("Great job! You have studied for %.1f hours in the last 7 days. Keep it up!", totalHours));
        notif.put("timestamp", System.currentTimeMillis());

        // Aynı özeti tekrar tekrar göndermemesi için (Opsiyonel: tarih kontrolü eklenebilir)
        db.collection("users").document(uId).collection("notifications").add(notif);
    }

    @Override
    protected void onProfileRequest() {
        loadFragment(new ProfileFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}