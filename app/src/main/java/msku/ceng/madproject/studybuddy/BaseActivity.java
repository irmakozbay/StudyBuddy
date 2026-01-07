package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BaseActivity extends AppCompatActivity {

    protected void setupNavbar() {
        ImageButton navGroups = findViewById(R.id.nav_groups);
        ImageButton navProfile = findViewById(R.id.nav_profile);
        ImageButton navNotifications = findViewById(R.id.nav_notifications);

        if (navGroups != null) {
            navGroups.setOnClickListener(v -> {
                if (!(this instanceof JoinGroupActivity)) {
                    startActivity(new Intent(this, JoinGroupActivity.class));
                    // Fragment'tan Activity'ye geçerken finish() diyebilirsin
                }
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                // EĞER ŞU AN MAINACTIVITY'DEYSEK FRAGMENT'I YÜKLE
                if (this instanceof MainActivity) {
                    loadFragment(new ProfileFragment());
                } else {
                    // EĞER BAŞKA SAYFADAYSAK ÖNCE MAIN'E GİT, SONRA FRAGMENT'I AÇ
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("openFragment", "profile");
                    startActivity(intent);
                }
            });
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 'fragment_container' MainActivity XML'indeki id olmalı
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}