package msku.ceng.madproject.studybuddy;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import msku.ceng.madproject.studybuddy.databinding.ItemMaterialBinding;

public class MyMaterialsRecyclerViewAdapter extends RecyclerView.Adapter<MyMaterialsRecyclerViewAdapter.ViewHolder> {

    private final List<Material> mValues;

    public MyMaterialsRecyclerViewAdapter(List<Material> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMaterialBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Material material = mValues.get(position);

        // Material sınıfındaki getter metodlarını kullandığından emin ol
        holder.mTitleView.setText(material.getTitle());
        // Veritabanında "content" olarak kayıtlı, sınıfında da getContent() olmalı
        holder.mSubheadView.setText(material.getDescription());

        // SİLME BUTONU İŞLEVİ
        holder.deleteButton.setOnClickListener(v -> {
            Context context = v.getContext();

            // Material sınıfında ID'yi tutan getter metodu (getMaterialId veya getId)
            String materialId = material.getMaterialId();

            if (materialId == null || materialId.isEmpty()) {
                Toast.makeText(context, "Hata: Materyal ID bulunamadı", Toast.LENGTH_SHORT).show();
                return;
            }

            // DÜZELTME: "notes" yerine "posts" koleksiyonundan siliyoruz
            FirebaseFirestore.getInstance().collection("posts").document(materialId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Materyal silindi", Toast.LENGTH_SHORT).show();
                        // Listeden silip animasyonla güncelleme (Opsiyonel, SnapshotListener varsa otomatik de olur)
                        // mValues.remove(holder.getAdapterPosition());
                        // notifyItemRemoved(holder.getAdapterPosition());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Silinemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitleView;
        public final TextView mSubheadView;
        public final View deleteButton; // Silme butonu tanımı

        public ViewHolder(ItemMaterialBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.materialTitle;
            mSubheadView = binding.materialSubhead; // XML'deki ID'ye göre değişebilir

            // XML'de bir silme butonu eklediysen onun ID'sini buraya yazmalısın.
            // Örnek: deleteButton = binding.btnDeleteMaterial;
            // Şimdilik root atadım hata vermesin diye ama XML'deki butonu bağlamalısın.
            deleteButton = binding.getRoot();
        }
    }
}