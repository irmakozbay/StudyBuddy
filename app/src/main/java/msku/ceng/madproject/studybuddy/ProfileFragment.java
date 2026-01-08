package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView; // EKLENDİ
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide; // Glide kütüphanesi (build.gradle'da ekli olmalı)
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener; // EKLENDİ
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException; // EKLENDİ

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // View Elemanları
    private TextView profileName, profileHandle, profileBio;
    private ImageView profileImage; // Profil resmi değişkeni
    private TextView btnLogout;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Firebase Başlatma
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 2. View Elemanları Bağlama
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        profileName = view.findViewById(R.id.profileName);
        profileHandle = view.findViewById(R.id.profileHandle);
        profileBio = view.findViewById(R.id.profileBio);

        // XML dosyasındaki resim ID'sine dikkat et (Genelde profileImage veya img_profile olur)
        // Eğer hata alırsan XML'deki ID'yi kontrol et.
        profileImage = view.findViewById(R.id.profileImage);

        btnBack = view.findViewById(R.id.btnBack);
        btnLogout = view.findViewById(R.id.btnLogout);
        Button editProfileBtn = view.findViewById(R.id.btnEditProfile);

        // Z-Index ayarı
        btnBack.bringToFront();
        btnLogout.bringToFront();

        // 3. Buton İşlevleri

        // Geri Tuşu
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Çıkış Tuşu
        btnLogout.setOnClickListener(v -> {
            if (mAuth != null) {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Çıkış yapıldı", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Profili Düzenle Tuşu
        editProfileBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);

                // İsim ve Bio'yu al
                String name = (profileName != null) ? profileName.getText().toString() : "";
                String bio = (profileBio != null) ? profileBio.getText().toString() : "";

                // Kullanıcı adını al ve başındaki '@' işaretini temizle
                String username = (profileHandle != null) ? profileHandle.getText().toString() : "";
                if (username.startsWith("@")) {
                    username = username.substring(1);
                }

                // Verileri Edit sayfasına gönder
                intent.putExtra("currentName", name);
                intent.putExtra("currentBio", bio);
                intent.putExtra("currentUsername", username); // ARTIK KULLANICI ADI DA GİDİYOR

                startActivity(intent);
            } catch (Exception e) {
                Log.e("ProfileFragment", "Edit sayfası açılırken hata: " + e.getMessage());
            }
        });

        // Verileri Dinlemeye Başla
        startListeningUserProfile();
        setupViewPager();

        return view;
    }

    // LoadUserProfile yerine bu metod kullanılıyor (Canlı Dinleme)
    private void startListeningUserProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        // addSnapshotListener: Veritabanında değişiklik olunca anında çalışır
        db.collection("users").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("ProfileError", "Veri dinleme hatası", error);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            // EditProfileActivity'de kaydettiğimiz anahtarları (key) kullanıyoruz
                            String name = documentSnapshot.getString("name");
                            // Eğer name boşsa fullName'e bak (eski kayıtlar için önlem)
                            if (name == null) name = documentSnapshot.getString("fullName");

                            String userName = documentSnapshot.getString("userName"); // EditProfileActivity'de userName yaptık
                            String bio = documentSnapshot.getString("bio");
                            String imgUrl = documentSnapshot.getString("profileImage");

                            // UI Güncelleme
                            profileName.setText(name != null ? name : "İsimsiz");
                            profileHandle.setText(userName != null ? "@" + userName : "@kullanici");
                            profileBio.setText(bio != null ? bio : "Merhaba! Ben StudyBuddy kullanıyorum.");

                            // Resim Yükleme (Glide)
                            if (imgUrl != null && !imgUrl.isEmpty() && getActivity() != null) {
                                try {
                                    Glide.with(ProfileFragment.this)
                                            .load(imgUrl)
                                            .placeholder(R.drawable.default_profile) // Yüklenirken gösterilecek resim (varsa)
                                            .into(profileImage);
                                } catch (Exception e) {
                                    Log.e("GlideError", "Resim yüklenemedi: " + e.getMessage());
                                }
                            }
                        }
                    }
                });
    }

    private void setupViewPager() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) return new NotesFragment();
                return new MaterialsFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Notes");
            else tab.setText("Study Materials");
        }).attach();
    }
}