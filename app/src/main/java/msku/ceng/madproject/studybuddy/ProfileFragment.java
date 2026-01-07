package msku.ceng.madproject.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView profileName, profileHandle, profileBio;
    private TextView btnLogout;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. ÖNCE FIREBASE'İ BAŞLAT (En Önemli Kısım: Sıralamayı değiştirdik)
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 2. View Elemanlarını Tanımla
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        profileName = view.findViewById(R.id.profileName);
        profileHandle = view.findViewById(R.id.profileHandle);
        profileBio = view.findViewById(R.id.profileBio);

        btnBack = view.findViewById(R.id.btnBack);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnBack.bringToFront();
        btnLogout.bringToFront();

        // 3. Buton İşlevlerini Tanımla

        // GERİ TUŞU:
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                // onBackPressed yerine finish() kullanmak sayfayı direkt kapatır ve alttakine döner.
                getActivity().finish();
            }
        });

        // ÇIKIŞ (LOGOUT) TUŞU:
        btnLogout.setOnClickListener(v -> {
            if (mAuth != null) {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Çıkış yapıldı", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        loadUserProfile();
        setupViewPager();

        return view;
    }

    private void loadUserProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String userName = documentSnapshot.getString("userName");
                        String bio = documentSnapshot.getString("bio");
                        profileName.setText(name != null ? name : "İsimsiz");
                        profileHandle.setText(userName != null ? "@" + userName : "@kullanici");
                        profileBio.setText(bio != null ? bio : "Biyografi yok.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Hata olursa kullanıcıya sessizce varsayılanı göster veya logla
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