package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

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

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private TextView profileName, profileHandle, profileBio;
    private ImageView profileImage;
    private TextView btnLogout;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        profileName = view.findViewById(R.id.profileName);
        profileHandle = view.findViewById(R.id.profileHandle);
        profileBio = view.findViewById(R.id.profileBio);

        profileImage = view.findViewById(R.id.profileImage);

        btnBack = view.findViewById(R.id.btnBack);
        btnLogout = view.findViewById(R.id.btnLogout);
        Button editProfileBtn = view.findViewById(R.id.btnEditProfile);

        btnBack.bringToFront();
        btnLogout.bringToFront();

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btnLogout.setOnClickListener(v -> {
            if (mAuth != null) {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Logged out.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        editProfileBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);

                String name = (profileName != null) ? profileName.getText().toString() : "";
                String bio = (profileBio != null) ? profileBio.getText().toString() : "";

                String username = (profileHandle != null) ? profileHandle.getText().toString() : "";
                if (username.startsWith("@")) {
                    username = username.substring(1);
                }

                intent.putExtra("currentName", name);
                intent.putExtra("currentBio", bio);
                intent.putExtra("currentUsername", username);

                startActivity(intent);
            } catch (Exception e) {
                Log.e("ProfileFragment", "Failed while opening the edit page: " + e.getMessage());
            }
        });

        startListeningUserProfile();
        setupViewPager();

        return view;
    }

    private void startListeningUserProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("ProfileError", "Error: ", error);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name == null) name = documentSnapshot.getString("fullName");

                            String userName = documentSnapshot.getString("userName"); // EditProfileActivity'de userName yaptık
                            String bio = documentSnapshot.getString("bio");
                            String imgUrl = documentSnapshot.getString("profileImage");

                            profileName.setText(name != null ? name : "İsimsiz");
                            profileHandle.setText(userName != null ? "@" + userName : "@kullanici");
                            profileBio.setText(bio != null ? bio : "Merhaba! Ben StudyBuddy kullanıyorum.");

                            if (imgUrl != null && !imgUrl.isEmpty() && getActivity() != null) {
                                try {
                                    Glide.with(ProfileFragment.this)
                                            .load(imgUrl)
                                            .placeholder(R.drawable.default_profile)
                                            .into(profileImage);
                                } catch (Exception e) {
                                    Log.e("GlideError", "Image could not load: " + e.getMessage());
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