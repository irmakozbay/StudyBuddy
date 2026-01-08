package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText registerNameSurname, registerEmail, registerUsername, registerPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Register successes, loading to database.");
                        FirebaseUser user = mAuth.getCurrentUser();

                        saveUserToFirestore(user, nameSurname, username, email);

                    } else {
                        Log.w(TAG, "Register failed: ", task.getException());
                        Toast.makeText(RegisterActivity.this, "Register fails: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user, String name, String username, String email) {
        if (user == null) return;

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUid());
        userData.put("fullName", name);
        userData.put("userName", username);
        userData.put("email", email);
        userData.put("bio", "Merhaba! Ben StudyBuddy kullanıyorum.");
        userData.put("followers", 0);
        userData.put("following", 0);

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