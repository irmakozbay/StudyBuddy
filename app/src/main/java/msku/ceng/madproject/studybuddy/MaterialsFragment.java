package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_materials_list.xml oluşturduğunu varsayıyorum
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
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // "materials" koleksiyonundan verileri çek
        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("postType", "MATERIAL")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        materialList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Material material = snapshot.toObject(Material.class);
                            materialList.add(material);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}