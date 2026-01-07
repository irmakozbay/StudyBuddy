package msku.ceng.madproject.studybuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityStatistics extends AppCompatActivity {
    private BarChart barChart;
    private TextView tvTotalHours, tvWeeklyGoalText, tvActiveGroups, tvStreak;
    private SeekBar seekBarWeeklyGoal;
    private FirebaseFirestore db;
    private double currentTotalHours = 0.0;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        db = FirebaseFirestore.getInstance();

        barChart = findViewById(R.id.barChart);
        tvTotalHours = findViewById(R.id.tvTotalHours);
        tvWeeklyGoalText = findViewById(R.id.tvWeeklyGoalText);
        tvActiveGroups = findViewById(R.id.tvActiveGroups);
        tvStreak = findViewById(R.id.tvStreak);
        seekBarWeeklyGoal = findViewById(R.id.seekBarWeeklyGoal);
        ImageButton btnBack = findViewById(R.id.btn_back);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        seekBarWeeklyGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { updateGoalDisplay(progress); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        fetchStudyData();
        fetchActiveGroupsCount();
    }

    private void updateGoalDisplay(int goalValue) {
        tvWeeklyGoalText.setText(String.format("%.1f / %d hours", currentTotalHours, goalValue));
    }

    private void fetchActiveGroupsCount() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uId).collection("my_groups")
                .addSnapshotListener(this, (value, error) -> {
                    if (error != null) {
                        Log.e("Stats", "Error: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        int count = value.size();
                        mainHandler.post(() -> {
                            if (tvActiveGroups != null) tvActiveGroups.setText(String.valueOf(count));
                        });
                    }
                });
    }

    private void fetchStudyData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uId).collection("study_logs")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value != null) {
                        executorService.execute(() -> {
                            float[] weeklyHours = new float[7];
                            currentTotalHours = 0;
                            TreeSet<Long> studyDates = new TreeSet<>();
                            long now = System.currentTimeMillis();

                            for (QueryDocumentSnapshot doc : value) {
                                Double hours = doc.getDouble("hours");
                                Long timestamp = doc.getLong("timestamp");
                                if (hours != null && timestamp != null && timestamp <= now) {
                                    currentTotalHours += hours;
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(timestamp);
                                    int index = mapDayToIndex(cal.get(Calendar.DAY_OF_WEEK));
                                    weeklyHours[index] += hours.floatValue();
                                    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0);
                                    cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
                                    studyDates.add(cal.getTimeInMillis());
                                }
                            }
                            int streak = calculateStreak(studyDates);
                            mainHandler.post(() -> {
                                if (tvTotalHours != null) tvTotalHours.setText(String.format("%.1f h", currentTotalHours));
                                if (tvStreak != null) tvStreak.setText(streak + (streak == 1 ? " day" : " days"));

                                if (streak >= 7) {
                                    sendStreakNotification(streak);
                                }

                                updateGoalDisplay(seekBarWeeklyGoal.getProgress());
                                updateChart(weeklyHours);
                            });
                        });
                    }
                });
    }

    private void sendStreakNotification(int streak) {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Study Streak Milestone! 🔥");
        notification.put("message", "Amazing! You have maintained a " + streak + "-day streak. Keep pushing!");
        notification.put("timestamp", System.currentTimeMillis());

        db.collection("users").document(uId).collection("notifications").add(notification);
    }

    private int calculateStreak(TreeSet<Long> dates) {
        if (dates.isEmpty()) return 0;
        int streakCount = 0;
        long oneDayMillis = 24 * 60 * 60 * 1000L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
        long checkDate = cal.getTimeInMillis();
        if (!dates.contains(checkDate)) checkDate -= oneDayMillis;
        while (dates.contains(checkDate)) { streakCount++; checkDate -= oneDayMillis; }
        return streakCount;
    }

    private int mapDayToIndex(int calDay) {
        switch (calDay) {
            case Calendar.MONDAY: return 0;
            case Calendar.TUESDAY: return 1;
            case Calendar.WEDNESDAY: return 2;
            case Calendar.THURSDAY: return 3;
            case Calendar.FRIDAY: return 4;
            case Calendar.SATURDAY: return 5;
            case Calendar.SUNDAY: return 6;
            default: return 0;
        }
    }

    private void updateChart(float[] dataPoints) {
        if (barChart == null) return;
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) entries.add(new BarEntry(i, dataPoints[i]));
        BarDataSet dataSet = new BarDataSet(entries, "Weekly Stats");
        dataSet.setColor(Color.parseColor("#A2AD7E"));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        barChart.setData(data);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}