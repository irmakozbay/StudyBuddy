package msku.ceng.madproject.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    private Button btnJoinGroups, btnCreateGroup, btnStatistics, btnAddPost, btnActivities;
    private ImageButton navProfile, navNotifications, navGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Element Initialization
        btnJoinGroups = findViewById(R.id.btnJoinGroups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnActivities = findViewById(R.id.btnActivities);
        navProfile = findViewById(R.id.nav_profile);
        navGroups = findViewById(R.id.nav_groups);
        navNotifications = findViewById(R.id.nav_notifications);


        // Join Groups Click Listener
        btnJoinGroups.setOnClickListener(v -> {
            showToast("Navigating to Join Groups...");
            Intent intent = new Intent(MainActivity.this, JoinGroupActivity.class);
            startActivity(intent);
        });

        // Activities (Update Stats) Click Listener
        btnActivities.setOnClickListener(v -> {
            showToast("Opening Activity Entry...");
            Intent intent = new Intent(MainActivity.this, UpdateStatsActivity.class);
            startActivity(intent);
        });

        // Create Group Click Listener
        btnCreateGroup.setOnClickListener(v -> {
            showToast("Navigating to Create Group...");
            Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        });

        // Statistics Click Listener
        btnStatistics.setOnClickListener(v -> {
            showToast("Navigating to Statistics...");
            Intent intent = new Intent(MainActivity.this, ActivityStatistics.class);
            startActivity(intent);
        });

        // Add Post Click Listener
        btnAddPost.setOnClickListener(v -> {
            showToast("Opening Add Post form...");
            // Intent for AddPostActivity can be added here
        });
        navGroups.setOnClickListener((v -> {
            showToast("Navigating to My Groups...");
            Intent intent = new Intent(MainActivity.this, MyGroupsActivity.class);
            startActivity(intent);
        }));



        // Bottom Navigation Profile Click Listener
        navProfile.setOnClickListener(v -> {
            showToast("Navigating to Profile...");
            Intent intent = new Intent(MainActivity.this, ProfileFragment.class);
            startActivity(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}