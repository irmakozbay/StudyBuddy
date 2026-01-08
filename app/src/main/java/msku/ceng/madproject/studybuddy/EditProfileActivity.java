package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    // XML'deki elemanların karşılıkları
    TextInputEditText etName, etUsername, etBio;
    CircleImageView imgProfile;
    ImageView imgClose;
    Button btnSave;

    // Veritabanı ve Resim değişkenleri
    Uri imageUri;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Firebase tanımları
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // Bağlamalar
        imgProfile = findViewById(R.id.img_edit_profile);
        imgClose = findViewById(R.id.img_close);
        etName = findViewById(R.id.et_name);
        etUsername = findViewById(R.id.et_username);
        etBio = findViewById(R.id.et_bio);
        btnSave = findViewById(R.id.btn_save);

        // --- DÜZELTME 1: Mevcut Kullanıcı Adını Kutucuğa Doldurma ---
        String geleniIsim = getIntent().getStringExtra("currentName");
        String gelenBio = getIntent().getStringExtra("currentBio");
        // ProfileFragment'tan "currentUsername" gönderdiğinden emin olmalısın (Aşağıda hatırlatma yaptım)
        String gelenUsername = getIntent().getStringExtra("currentUsername");

        if(geleniIsim != null) etName.setText(geleniIsim);
        if(gelenBio != null) etBio.setText(gelenBio);
        if(gelenUsername != null) etUsername.setText(gelenUsername); // Kutucuk dolu gelsin

        // 1. Kapatma tuşu
        if (imgClose != null) {
            imgClose.setOnClickListener(v -> finish());
        }

        // 2. Profil Resmine Tıklayınca Galeri Açma
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 100);
        });

        // 3. Kaydet Butonu Mantığı
        btnSave.setOnClickListener(v -> {
            updateProfile();
        });
    }

    // Galeriden dönen resmi yakalama
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri); // Ekranda göster
        }
    }

    private void updateProfile() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        String name = etName.getText().toString();
        String username = etUsername.getText().toString(); // Burada kullanıcı adını alıyoruz
        String bio = etBio.getText().toString();

        Toast.makeText(this, "Güncelleniyor...", Toast.LENGTH_SHORT).show();

        if (imageUri != null) {
            StorageReference fileRef = storage.getReference().child("profile_images/" + uid + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(uid, name, username, bio, uri.toString());
                });
            });
        } else {
            saveToFirestore(uid, name, username, bio, null);
        }
    }

    private void saveToFirestore(String uid, String name, String username, String bio, String imgUrl) {
        Map<String, Object> userUpdates = new HashMap<>();
        // --- DÜZELTME 2: Veritabanı Anahtar İsmi (Key) ---
        // ProfileFragment "userName" okuduğu için burası da "userName" olmalı.
        // Önceden "username" (küçük n) idi.
        userUpdates.put("fullName", name); // ProfileFragment "fullName" okuyor olabilir, kontrol et.
        userUpdates.put("name", name);     // Garanti olsun diye ikisini de ekledim.

        userUpdates.put("userName", username); // <-- KRİTİK DÜZELTME: 'username' yerine 'userName'

        userUpdates.put("bio", bio);
        if (imgUrl != null) userUpdates.put("profileImage", imgUrl);

        db.collection("users").document(uid).update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}