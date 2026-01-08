package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends BaseActivity {

    private Button btnJoinGroups, btnCreateGroup, btnStatistics, btnAddPost, btnActivities;

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

        // 1. Görünüm elemanlarını bağla
        headerLayout = findViewById(R.id.headerLayout);
        actionButtonsLayout = findViewById(R.id.actionButtonsLayout);
        bottomNavBar = findViewById(R.id.bottom_navigation_bar);
        fragmentContainer = findViewById(R.id.fragment_container);

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
        if ("PROFILE".equals(getIntent().getStringExtra("OPEN_FRAGMENT"))) {
            openProfileMode();
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
        openProfileMode();
    }
}