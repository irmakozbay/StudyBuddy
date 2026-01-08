package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvContent;
    private ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvContent = findViewById(R.id.tvDetailContent);
        btnClose = findViewById(R.id.btnCloseDetail);

        // Kapatma butonu
        btnClose.setOnClickListener(v -> finish());

        // Adapter'dan gelen verileri alıyoruz
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        // Verileri ekrana basıyoruz
        tvTitle.setText(title);
        tvContent.setText(content);
    }
}