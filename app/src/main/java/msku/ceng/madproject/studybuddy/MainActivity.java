package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

    // Layoutları tanımlıyoruz (Gizle/Göster yapmak için)
    private LinearLayout headerLayout, actionButtonsLayout, bottomNavBar;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navbar kurulumu (Hata olursa uygulama çökmesin diye try-catch içinde)
        try {
            setupNavbar();
        } catch (Exception e) { e.printStackTrace(); }
        db = FirebaseFirestore.getInstance();
        setupNavbar();

        // 1. Görünüm elemanlarını bağla
        headerLayout = findViewById(R.id.headerLayout);
        actionButtonsLayout = findViewById(R.id.actionButtonsLayout);
        bottomNavBar = findViewById(R.id.bottom_navigation_bar);
        fragmentContainer = findViewById(R.id.fragment_container);

        // UI Elements
        btnJoinGroups = findViewById(R.id.btnJoinGroups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnActivities = findViewById(R.id.btnActivities);

        // 2. Buton Tıklamaları
        if(btnJoinGroups != null) btnJoinGroups.setOnClickListener(v -> startActivity(new Intent(this, JoinGroupActivity.class)));
        if(btnCreateGroup != null) btnCreateGroup.setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));
        if(btnStatistics != null) btnStatistics.setOnClickListener(v -> startActivity(new Intent(this, ActivityStatistics.class)));
        if(btnActivities != null) btnActivities.setOnClickListener(v -> startActivity(new Intent(this, UpdateStatsActivity.class)));
        if(btnAddPost != null) {
            btnAddPost.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intent);
            });
        }

        // 3. Profil isteği geldiyse (Navbardan veya başka yerden)
        notificationBadge = findViewById(R.id.notificationBadge); // Kırmızı nokta ID'si

        // Listeners
        btnJoinGroups.setOnClickListener(v -> startActivity(new Intent(this, JoinGroupActivity.class)));
        btnActivities.setOnClickListener(v -> startActivity(new Intent(this, UpdateStatsActivity.class)));
        btnCreateGroup.setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));
        btnStatistics.setOnClickListener(v -> startActivity(new Intent(this, ActivityStatistics.class)));

        if ("PROFILE".equals(getIntent().getStringExtra("OPEN_FRAGMENT"))) {
            onProfileRequest();
        }

        // ============================================================
        // SENİN İSTEDİĞİN BASİT INTENT ÇÖZÜMÜ
        // ============================================================
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Eğer Profil sayfası açıksa (yani fragment container görünürse)
                if (fragmentContainer != null && fragmentContainer.getVisibility() == View.VISIBLE) {

                    // Karmaşık işlemler yerine uygulamayı Ana Sayfa olarak YENİDEN BAŞLAT
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    // Bu bayraklar (flags) eski sayfaları temizler, böylece üst üste binmez
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Şu anki bozuk/karışık sayfayı öldür

                } else {
                    // Ana sayfadaysan normal çıkış yap
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

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

    // Profil modunu açan basit fonksiyon
    private void openProfileMode() {
        if(headerLayout != null) headerLayout.setVisibility(View.GONE);
        if(actionButtonsLayout != null) actionButtonsLayout.setVisibility(View.GONE);
        if(bottomNavBar != null) bottomNavBar.setVisibility(View.GONE);

        if(fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit(); // Back stack'e eklemiyoruz çünkü Intent ile döneceğiz
        }
    }

    // BaseActivity'den çağrılan metod
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