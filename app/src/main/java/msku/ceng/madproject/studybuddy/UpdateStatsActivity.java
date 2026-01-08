package msku.ceng.madproject.studybuddy;

/*Bahriye Gavaz*/

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateStatsActivity extends AppCompatActivity {

    private EditText etHours, etNote;
    private Spinner spinnerGroups;
    private Button btnUpdate, btnSelectDate;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private String selectedDate;
    private long selectedTimestamp;

    private List<String> joinedGroupNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stats);

        db = FirebaseFirestore.getInstance();

        etHours = findViewById(R.id.etHours);
        etNote = findViewById(R.id.etNote);
        spinnerGroups = findViewById(R.id.spinnerGroups);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnBack = findViewById(R.id.btnBack);

        Calendar calendar = Calendar.getInstance();
        updateSelectedDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        selectedTimestamp = calendar.getTimeInMillis();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Tarih Seçici
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar chosenDate = Calendar.getInstance();
                chosenDate.set(year, month, dayOfMonth);

                if (chosenDate.getTimeInMillis() > System.currentTimeMillis()) {
                    Toast.makeText(this, "You cannot select a future date!", Toast.LENGTH_SHORT).show();
                } else {
                    updateSelectedDate(dayOfMonth, month, year);
                    selectedTimestamp = chosenDate.getTimeInMillis();
                    btnSelectDate.setText("Selected Date: " + selectedDate);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, joinedGroupNames);
        spinnerGroups.setAdapter(spinnerAdapter);

        fetchJoinedGroups();

        btnUpdate.setOnClickListener(v -> saveToFirebase());
    }

    private void fetchJoinedGroups() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uId).collection("my_groups")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    joinedGroupNames.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String name = doc.getString("name");
                        if (name != null) joinedGroupNames.add(name);
                    }

                    if (joinedGroupNames.isEmpty()) {
                        joinedGroupNames.add("Please first join a group.");
                        btnUpdate.setEnabled(false);
                    } else {
                        btnUpdate.setEnabled(true);
                    }
                    spinnerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("UpdateStats", "Groups couldn't load.", e));
    }

    private void updateSelectedDate(int day, int month, int year) {
        selectedDate = day + "/" + (month + 1) + "/" + year;
    }

    private void saveToFirebase() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String hoursStr = etHours.getText().toString().trim();
        if (hoursStr.isEmpty()) {
            Toast.makeText(this, "Please enter study hours", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double hours = Double.parseDouble(hoursStr);
            String group = spinnerGroups.getSelectedItem().toString();
            String note = etNote.getText().toString().trim();

            Map<String, Object> log = new HashMap<>();
            log.put("groupName", group);
            log.put("hours", hours);
            log.put("note", note);
            log.put("date", selectedDate);
            log.put("timestamp", selectedTimestamp);

            // Dinamik uId yoluyla kaydet
            db.collection("users").document(uId).collection("study_logs")
                    .add(log)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Data synced!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid hours format!", Toast.LENGTH_SHORT).show();
        }
    }
}