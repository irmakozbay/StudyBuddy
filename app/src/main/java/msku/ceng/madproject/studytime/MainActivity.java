package msku.ceng.madproject.studytime;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeTxt);

        String username = getIntent().getStringExtra("username");
        welcomeText.setText("Welcome, " + username + "!");
    }
}
