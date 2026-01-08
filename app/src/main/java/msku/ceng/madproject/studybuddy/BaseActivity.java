package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupNavbar() {
        ImageButton navGroups = findViewById(R.id.nav_groups);
        ImageButton navProfile = findViewById(R.id.nav_profile);
        ImageButton navNotifications = findViewById(R.id.nav_notifications);

        if (navGroups != null) {
            navGroups.setOnClickListener(v -> {
                if (!(this instanceof MyGroupsActivity)) {
                    startActivity(new Intent(this, MyGroupsActivity.class));
                }
            });
        }

        if (navNotifications != null) {
            navNotifications.setOnClickListener(v -> {
                if (!(this instanceof NotificationsActivity)) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                }
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                // Fragment olduğu için direkt Main'e yönlendiriyoruz
                if (!(this instanceof MainActivity)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("OPEN_FRAGMENT", "PROFILE");
                    startActivity(intent);
                } else {
                    // Zaten Main'deysen bu metodu tetikle
                    onProfileRequest();
                }
            });
        }
    }

    // Bu metodu her Activity kendi içinde dolduracak (Abstract Metod)
    protected abstract void onProfileRequest();
}