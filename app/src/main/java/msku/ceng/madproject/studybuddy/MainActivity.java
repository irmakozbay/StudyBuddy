package msku.ceng.madproject.studybuddy;

/*Bahriye Gavaz, Irmak Özbay*/

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private Button btnJoinGroups, btnCreateGroup, btnStatistics, btnAddPost, btnActivities;
    private View notificationBadge;
    private FirebaseFirestore db;

    private LinearLayout headerLayout, actionButtonsLayout, bottomNavBar;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        try {
            setupNavbar();
        } catch (Exception e) { e.printStackTrace(); }

        headerLayout = findViewById(R.id.headerLayout);
        actionButtonsLayout = findViewById(R.id.actionButtonsLayout);
        bottomNavBar = findViewById(R.id.bottom_navigation_bar);
        fragmentContainer = findViewById(R.id.fragment_container);
        notificationBadge = findViewById(R.id.notificationBadge);

        btnJoinGroups = findViewById(R.id.btnJoinGroups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnActivities = findViewById(R.id.btnActivities);

        if(btnJoinGroups != null) btnJoinGroups.setOnClickListener(v -> startActivity(new Intent(this, JoinGroupActivity.class)));
        if(btnCreateGroup != null) btnCreateGroup.setOnClickListener(v -> startActivity(new Intent(this, CreateGroupActivity.class)));
        if(btnStatistics != null) btnStatistics.setOnClickListener(v -> startActivity(new Intent(this, ActivityStatistics.class)));
        if(btnActivities != null) btnActivities.setOnClickListener(v -> startActivity(new Intent(this, UpdateStatsActivity.class)));
        if(btnAddPost != null) {
            btnAddPost.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddPostActivity.class)));
        }

        if ("PROFILE".equals(getIntent().getStringExtra("OPEN_FRAGMENT"))) {
            openProfileMode();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentContainer != null && fragmentContainer.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        listenForNotifications();
        checkAndSendWeeklySummary();
    }

    @Override
    protected void onProfileRequest() {
        openProfileMode();
    }

    private void openProfileMode() {
        if(headerLayout != null) headerLayout.setVisibility(View.GONE);
        if(actionButtonsLayout != null) actionButtonsLayout.setVisibility(View.GONE);
        if(bottomNavBar != null) bottomNavBar.setVisibility(View.GONE);

        if(fragmentContainer != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
        }
    }

    private void listenForNotifications() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uId).collection("notifications")
                .addSnapshotListener((value, error) -> {
                    if (value != null && !value.isEmpty()) {
                        if (notificationBadge != null) notificationBadge.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void checkAndSendWeeklySummary() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> notif = new HashMap<>();
        notif.put("title", "Weekly Summary 📊");
        notif.put("message", String.format("Great job! You have studied for %.1f hours in the last 7 days.", totalHours));
        notif.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(uId).collection("notifications").add(notif);
    }
}