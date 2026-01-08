package msku.ceng.madproject.studybuddy;

/*Bahriye Gavaz*/

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateGroupActivity extends BaseActivity {
    private EditText etName, etDesc;
    private Button btnCreate;
    private ImageButton btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setupNavbar();

        db = FirebaseFirestore.getInstance();
        etName = findViewById(R.id.et_group_name);
        etDesc = findViewById(R.id.et_group_description);
        btnCreate = findViewById(R.id.btn_create_group);
        btnBack = findViewById(R.id.btn_back);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (!name.isEmpty() && !desc.isEmpty()) {
                String groupId = db.collection("groups").document().getId();
                Group newGroup = new Group(groupId, name, desc, R.drawable.profile);
                db.collection("groups").document(groupId).set(newGroup)
                        .addOnSuccessListener(aVoid -> { Toast.makeText(this, "Group Created!", Toast.LENGTH_SHORT).show(); finish(); })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onProfileRequest() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("OPEN_FRAGMENT", "PROFILE");
        startActivity(intent);
    }
}