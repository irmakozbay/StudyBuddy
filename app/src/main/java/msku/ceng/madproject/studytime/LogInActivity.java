package msku.ceng.madproject.studytime;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {

    EditText usrName, password;
    Button loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usrName = findViewById(R.id.usrName);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);

        loginBtn.setOnClickListener(v -> {
            String username = usrName.getText().toString().trim();
            String passWord = password.getText().toString().trim();

            if (username.isEmpty() || passWord.isEmpty()) {
                Toast.makeText(this, "Username or password is empty.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Successful.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }
}
