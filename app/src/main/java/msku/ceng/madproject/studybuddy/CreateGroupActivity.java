package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText etName, etDesc;
    private Button btnCreate;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        etName = findViewById(R.id.et_group_name);
        etDesc = findViewById(R.id.et_group_description);
        btnCreate = findViewById(R.id.btn_create_group);
        btnBack = findViewById(R.id.btn_back);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (!name.isEmpty() && !desc.isEmpty()) {
                // Yeni grubu merkezi listeye ekle
                JoinGroupActivity.Group newGroup = new JoinGroupActivity.Group(name, desc, R.drawable.profile);
                GroupManager.addGroup(newGroup);

                Toast.makeText(this, "Grup eklendi!", Toast.LENGTH_SHORT).show();
                finish(); // Join ekranına geri dön
            } else {
                Toast.makeText(this, "Alanları doldurun!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}