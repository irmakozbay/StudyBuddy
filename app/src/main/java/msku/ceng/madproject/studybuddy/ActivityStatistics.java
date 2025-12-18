package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import msku.ceng.madproject.studybuddy.R;

public class ActivityStatistics extends AppCompatActivity {

    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 1. Geri Butonu Özelliği
        ImageButton btnBack = findViewById(R.id.btn_back_statistics);
        btnBack.setOnClickListener(v -> finish());

        // 2. Firebase Bağlantısı
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            dbRef = FirebaseDatabase.getInstance().getReference("Statistics").child(userId);
            loadFirebaseData();
        }
    }

    private void loadFirebaseData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Firebase'den saatleri al (Örn: mon: 5.5)
                    updateBar(findViewById(R.id.bar_sun), getFloat(snapshot, "weekly/sun"));
                    updateBar(findViewById(R.id.bar_mon), getFloat(snapshot, "weekly/mon"));
                    updateBar(findViewById(R.id.bar_tue), getFloat(snapshot, "weekly/tue"));
                    updateBar(findViewById(R.id.bar_wed), getFloat(snapshot, "weekly/wed"));
                    updateBar(findViewById(R.id.bar_thu), getFloat(snapshot, "weekly/thu"));
                    updateBar(findViewById(R.id.bar_fri), getFloat(snapshot, "weekly/fri"));
                    updateBar(findViewById(R.id.bar_sat), getFloat(snapshot, "weekly/sat"));
                }
            }
            @Override public void onCancelled(DatabaseError error) {}
        });
    }

    private float getFloat(DataSnapshot snap, String path) {
        return snap.child(path).exists() ? snap.child(path).getValue(Float.class) : 0f;
    }

    private void updateBar(View bar, float hours) {
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        // Her 1 saat için 25dp yükseklik veriyoruz
        params.height = (int) (hours * 25 * getResources().getDisplayMetrics().density);
        bar.setLayoutParams(params);
    }
}