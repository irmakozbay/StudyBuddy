package msku.ceng.madproject.studybuddy;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateStatsActivity extends AppCompatActivity {

    private EditText etHours, etNote;
    private Spinner spinnerGroups;
    private Button btnUpdate, btnSelectDate;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private String selectedDate;
    private long selectedTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stats);

        db = FirebaseFirestore.getInstance();

        // View Bağlamaları
        etHours = findViewById(R.id.etHours);
        etNote = findViewById(R.id.etNote);
        spinnerGroups = findViewById(R.id.spinnerGroups);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnBack = findViewById(R.id.btnBack);

        // Başlangıç Ayarları
        Calendar calendar = Calendar.getInstance();
        updateSelectedDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        selectedTimestamp = calendar.getTimeInMillis();

        // Geri Butonu İşlemi
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

        // Spinner Grupları
        String[] groups = {"Math Wizards", "Code & Coffee", "Design Thinkers"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groups);
        spinnerGroups.setAdapter(adapter);

        // Kaydetme İşlemi
        btnUpdate.setOnClickListener(v -> saveToFirebase());
    }

    private void updateSelectedDate(int day, int month, int year) {
        selectedDate = day + "/" + (month + 1) + "/" + year;
    }

    private void saveToFirebase() {
        String hoursStr = etHours.getText().toString().trim();

        if (hoursStr.isEmpty()) {
            Toast.makeText(this, "Please enter study hours", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double hours = Double.parseDouble(hoursStr);
            String group = spinnerGroups.getSelectedItem().toString();
            String note = etNote.getText().toString().trim();

            if (selectedTimestamp > System.currentTimeMillis()) {
                Toast.makeText(this, "Future dates are not allowed!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> log = new HashMap<>();
            log.put("groupName", group);
            log.put("hours", hours);
            log.put("note", note);
            log.put("date", selectedDate);
            log.put("timestamp", selectedTimestamp); // Grafik senkronizasyonu için

            db.collection("users").document("user_1").collection("study_logs")
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