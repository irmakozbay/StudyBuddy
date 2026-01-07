package msku.ceng.madproject.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupNavbar();

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_notifications);
        ImageButton btnBack = findViewById(R.id.btn_back);

        adapter = new NotificationAdapter(notificationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        fetchNotifications();
    }

    @Override
    protected void onProfileRequest() { }

    private void fetchNotifications() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetching notifications ordered by newest first
        db.collection("users").document(uId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        notificationList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notification notification = doc.toObject(Notification.class);
                            if (notification != null) notificationList.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
        private List<Notification> list;
        public NotificationAdapter(List<Notification> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Notification n = list.get(position);
            holder.tvTitle.setText(n.getTitle());
            holder.tvMessage.setText(n.getMessage());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvMessage;
            public ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_notification_title);
                tvMessage = v.findViewById(R.id.tv_notification_message);
            }
        }
    }
}