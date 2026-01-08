package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

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

    TextInputEditText etName, etUsername, etBio;
    CircleImageView imgProfile;
    ImageView imgClose;
    Button btnSave;

    Uri imageUri;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        imgProfile = findViewById(R.id.img_edit_profile);
        imgClose = findViewById(R.id.img_close);
        etName = findViewById(R.id.et_name);
        etUsername = findViewById(R.id.et_username);
        etBio = findViewById(R.id.et_bio);
        btnSave = findViewById(R.id.btn_save);

        String currentName = getIntent().getStringExtra("currentName");
        String currentBio = getIntent().getStringExtra("currentBio");
        String currentUsername = getIntent().getStringExtra("currentUsername");

        if(currentName != null) etName.setText(currentName);
        if(currentBio != null) etBio.setText(currentBio);
        if(currentUsername != null) etUsername.setText(currentUsername);

        if (imgClose != null) {
            imgClose.setOnClickListener(v -> finish());
        }

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 100);
        });

        btnSave.setOnClickListener(v -> {
            updateProfile();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);
        }
    }

    private void updateProfile() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();
        String name = etName.getText().toString();
        String username = etUsername.getText().toString();
        String bio = etBio.getText().toString();

        Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show();

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
        userUpdates.put("fullName", name);
        userUpdates.put("name", name);

        userUpdates.put("userName", username);
        userUpdates.put("bio", bio);
        if (imgUrl != null) userUpdates.put("profileImage", imgUrl);

        db.collection("users").document(uid).update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}