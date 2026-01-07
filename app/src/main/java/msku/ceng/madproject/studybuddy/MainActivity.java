package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class MainActivity extends BaseActivity {

    private Button btnJoinGroups, btnCreateGroup, btnStatistics, btnAddPost, btnActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navbar'ı BaseActivity üzerinden kur
        setupNavbar();

        // UI Element Initialization
        btnJoinGroups = findViewById(R.id.btnJoinGroups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnActivities = findViewById(R.id.btnActivities);

        // Click Listeners
        btnJoinGroups.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, JoinGroupActivity.class));
        });

        btnActivities.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UpdateStatsActivity.class));
        });

        btnCreateGroup.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
        });

        btnStatistics.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ActivityStatistics.class));
        });

        // Diğer sayfalardan profil butonuna basılarak gelindiyse kontrol et
        if ("PROFILE".equals(getIntent().getStringExtra("OPEN_FRAGMENT"))) {
            onProfileRequest();
        }
    }

    @Override
    protected void onProfileRequest() {
        // HATA ÇÖZÜMÜ: Fragment startActivity ile DEĞİL, Transaction ile yüklenir
        loadFragment(new ProfileFragment());
    }

    private void loadFragment(Fragment fragment) {
        // fragment_container ID'si activity_main.xml'de tanımlı olmalı
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}