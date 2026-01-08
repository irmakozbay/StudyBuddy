package msku.ceng.madproject.studybuddy;

/*Irmak Özbay*/

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MaterialsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyMaterialsRecyclerViewAdapter adapter;
    private List<Material> materialList;
    private FirebaseFirestore db;

    public MaterialsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materials_list, container, false);

        recyclerView = view.findViewById(R.id.materialsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        materialList = new ArrayList<>();
        adapter = new MyMaterialsRecyclerViewAdapter(materialList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadMaterials();

        return view;
    }


    private void loadMaterials() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String currentUserId = user.getUid();

        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("postType", "MATERIAL")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        materialList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Material material = snapshot.toObject(Material.class);

                            if (material != null) {
                                material.setMaterialId(snapshot.getId());
                                materialList.add(material);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }
}