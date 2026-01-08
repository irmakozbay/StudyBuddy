package msku.ceng.madproject.studybuddy;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        holder.mTitleView.setText(material.getTitle());
        holder.mSubheadView.setText(material.getContent());

        // 1. Karta tıklayınca -> DETAY AÇILSIN (Silinmesin)
// Karta Tıklama (Detay Açma)
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PostDetailActivity.class);

            // Verileri paketleyip gönderiyoruz
            intent.putExtra("title", material.getTitle());
            intent.putExtra("content", material.getContent()); // Getter ismin farklıysa düzelt (getDescription vb.)

            context.startActivity(intent);
        });

        // 2. Çöp Kutusuna tıklayınca -> SİLİNSİN
        holder.deleteButton.setOnClickListener(v -> {
            String id = material.getMaterialId();
            if (id != null && !id.isEmpty()) {
                // Firebase silme işlemi
                FirebaseFirestore.getInstance().collection("posts").document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(v.getContext(), "Başarıyla silindi", Toast.LENGTH_SHORT).show();

                            // Listeden görsel olarak da anında kaldırmak için:
                            mValues.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mValues.size());
                        });
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTitleView;
        public final TextView mSubheadView;
        public final ImageView deleteButton; // Yeni butonu buraya ekliyoruz

        public ViewHolder(ItemMaterialBinding binding) {
            super(binding.getRoot());
            mTitleView = binding.materialTitle;
            mSubheadView = binding.materialSubhead;

            // XML'deki ID ile bağlıyoruz
            deleteButton = binding.btnDelete;
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}