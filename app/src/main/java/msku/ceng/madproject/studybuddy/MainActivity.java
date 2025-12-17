package msku.ceng.madproject.studybuddy;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnJoinGroups;
    private Button btnCreateGroup;
    private Button btnStatistics;
    private Button btnAddPost;

    private ImageButton navProfile;
    // Diğer navigasyon butonları: navNotifications, navGroups

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Elemanlarını Tanımlama
        btnJoinGroups = findViewById(R.id.btnJoinGroups);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnAddPost = findViewById(R.id.btnAddPost);

        navProfile = findViewById(R.id.nav_profile);
        // navGroups ve navNotifications'ı da tanımlayabilirsiniz.

        // Tıklama Olayları (Click Listeners)
        // MainActivity.java veya butonun bulunduğu Activity dosyası
        btnJoinGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Gruplara Katıl sayfasına gidiliyor...");

                // --- İNTENT KODU BURAYA EKLENİYOR ---

                // 1. Intent nesnesi oluşturulur.
                // Intent, mevcut Activity'den (this) hedef Activity'ye (JoinGroupActivity.class) gitme niyetini belirtir.
                Intent intent = new Intent(v.getContext(), JoinGroupActivity.class);

                // 2. Intent ile hedef Activity başlatılır.
                startActivity(intent);

                // --- İNTENT KODU SONU ---
            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Raporlara göre, bu buton "Create Group" sayfasına gitmelidir (Page 4)
                showToast("Yeni Grup Oluştur sayfasına gidiliyor...");
                // Intent ile CreateGroupActivity başlatılacak (ileride)
            }
        });

        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kullanıcıya bilgi veriyoruz
                Toast.makeText(v.getContext(), "İstatistikler sayfasına gidiliyor...", Toast.LENGTH_SHORT).show();

                // INTENT: Mevcut sayfadan StatisticsActivity sayfasına geçiş başlatılır
                Intent intent = new Intent(v.getContext(), ActivityStatistics.class);
                startActivity(intent);
            }
        });

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Gönderi Ekleme formu açılıyor...");
                // Burada yeni bir Post oluşturma formu açılabilir
            }
        });

        // Alt Navigasyon Çubuğu Olayı
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Raporlara göre, bu buton "Profile" sayfasına gitmelidir (Page 1)
                showToast("Profil sayfasına gidiliyor...");
                // Intent ile ProfileActivity başlatılacak (ileride)
            }
        });

        // navGroups için bir olay eklemeye gerek yok, çünkü zaten ana sayfadayız.
    }

    /**
     * Kısa bir Toast mesajı gösteren yardımcı fonksiyon
     */
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}