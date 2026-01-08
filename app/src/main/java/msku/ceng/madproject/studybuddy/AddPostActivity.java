package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private EditText etPostTitle, etPostContent;
    private RadioGroup radioGroupType;
    private RadioButton rbNote, rbMaterial;
    private Button btnSharePost;
    private ImageButton btnClose;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        // Firebase Başlatma
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Elemanları Tanımlama
        etPostTitle = findViewById(R.id.etPostTitle);
        etPostContent = findViewById(R.id.etPostContent);
        radioGroupType = findViewById(R.id.radioGroupType);
        rbNote = findViewById(R.id.rbNote);
        rbMaterial = findViewById(R.id.rbMaterial);
        btnSharePost = findViewById(R.id.btnSharePost);
        btnClose = findViewById(R.id.btnClose);
        // Tıklama Olayı: Bu kodu eklemezsen butona basınca hiçbir şey olmaz
        btnClose.setOnClickListener(v -> {
            finish(); // Bu komut "Sayfayı kapat ve önceki sayfaya dön" demektir
        });

        // Paylaş Butonu
        btnSharePost.setOnClickListener(v -> savePost());
    }

    private void savePost() {
        String title = etPostTitle.getText().toString().trim();
        String content = etPostContent.getText().toString().trim();

        // 1. Boş alan kontrolü
        if (TextUtils.isEmpty(title)) {
            etPostTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(content)) {
            etPostContent.setError("Content cannot be empty");
            return;
        }

        // 2. Kullanıcı Giriş Kontrolü
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You need to log in first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Kategori Belirleme (Note mu Material mi?)
        String postType = "NOTE"; // Varsayılan
        if (rbMaterial.isChecked()) {
            postType = "MATERIAL";
        }

        // 4. Veritabanına Kaydedilecek Veri Haritası
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("userId", currentUser.getUid()); // Profilde göstermek için KRİTİK
        postMap.put("userName", currentUser.getDisplayName()); // Gönderen ismi (Opsiyonel)
        postMap.put("title", title);
        postMap.put("content", content);
        postMap.put("postType", postType); // Filtreleme için KRİTİK
        postMap.put("timestamp", FieldValue.serverTimestamp()); // Sıralama için zaman

        // 5. Firestore'a "posts" koleksiyonuna ekle
        db.collection("posts")
                .add(postMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddPostActivity.this, "Post shared successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Sayfayı kapat ve ana sayfaya dön
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPostActivity.this, "Error sharing post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}