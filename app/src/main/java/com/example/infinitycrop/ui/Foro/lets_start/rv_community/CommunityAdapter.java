package com.example.infinitycrop.ui.Foro.lets_start.rv_community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.list_machine.Adapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CommunityAdapter extends FirestoreRecyclerAdapter<CommunityModel, CommunityAdapter.CommunityViewHolder> {
    private OnItemClickListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    public CommunityAdapter(@NonNull FirestoreRecyclerOptions<CommunityModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CommunityViewHolder holder, int position, @NonNull final CommunityModel model) {
        holder.name_community.setText(model.getName());
        Picasso.get().load(model.getImg()).into(holder.img_community);
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String uid=snapshot.getId();
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        String id=firebaseAuth.getUid();
        db.collection("Foro user").document(id).collection("Following").document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.cardView_community.setBackgroundResource(R.color.colorPrimaryDashboard);
                    }else{
                        holder.cardView_community.setBackgroundResource(R.color.white);
                    }
                } else {

                }
            }
        });


    }

    public void unFollow(int posicion,String uiDocument,int followers){
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        String id=firebaseAuth.getUid();
        db.collection("Foro user").document(id)
                .collection("Following").document(uiDocument).delete();
        db.collection("Community").document(uiDocument)
                .collection("Following").document(id).delete();
        followers--;
        db.collection("Community").document(uiDocument).update("followers",followers);
        //actualizar RV
        notifyItemChanged(posicion);
    }
    public void follow(int posicion,String uiDocument,int followers){
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        String id=firebaseAuth.getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("user id", id);
        db.collection("Foro user").document(id)
                .collection("Following").document(uiDocument).set(data);
        db.collection("Community").document(uiDocument)
                .collection("Following").document(id).set(data);
        followers++;
        db.collection("Community").document(uiDocument).update("followers",followers);
        //actualizar RV
        notifyItemChanged(posicion);
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item,parent,false);
        return new CommunityViewHolder(view);
    }

    class CommunityViewHolder extends RecyclerView.ViewHolder{
        protected TextView name_community;
        protected TextView creator_community;
        protected ImageView img_community;
        protected CardView cardView_community;
        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            name_community=itemView.findViewById(R.id.name_community);
            img_community=itemView.findViewById(R.id.img_community);
            cardView_community=itemView.findViewById(R.id.cardView_community);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
