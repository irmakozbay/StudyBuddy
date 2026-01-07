package msku.ceng.madproject.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; // Eklendi

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText registerNameSurname, registerEmail, registerUsername, registerPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Veritabanı referansı

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Veritabanını başlat

        registerNameSurname = findViewById(R.id.registerNameSurname);
        registerEmail = findViewById(R.id.registerEmail);
        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonCreateAccount.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        final String nameSurname = registerNameSurname.getText().toString().trim();
        final String email = registerEmail.getText().toString().trim();
        final String username = registerUsername.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();

        if (nameSurname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalı.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Authentication ile Kullanıcı Oluştur
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Kayıt başarılı, veritabanına yazılıyor...");
                        FirebaseUser user = mAuth.getCurrentUser();

                        // 2. Veritabanına Kullanıcı Detaylarını Kaydet
                        saveUserToFirestore(user, nameSurname, username, email);

                    } else {
                        Log.w(TAG, "Kayıt hatası", task.getException());
                        Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user, String name, String username, String email) {
        if (user == null) return;

        // Veritabanına gidecek veri paketi
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUid());
        userData.put("fullName", name);
        userData.put("userName", username);
        userData.put("email", email);
        userData.put("bio", "Merhaba! Ben StudyBuddy kullanıyorum."); // Varsayılan bio
        // İstersen followers, following gibi sayıları da 0 olarak başlatabilirsin
        userData.put("followers", 0);
        userData.put("following", 0);

        // 'users' koleksiyonunda, kullanıcının kendi ID'si ile döküman oluştur
        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Hesap oluşturuldu!", Toast.LENGTH_SHORT).show();
                    navigateToLoginActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Veritabanı hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}