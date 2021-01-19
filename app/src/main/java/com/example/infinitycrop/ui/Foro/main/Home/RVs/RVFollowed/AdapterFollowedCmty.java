package com.example.infinitycrop.ui.Foro.main.Home.RVs.RVFollowed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFollowedCmty extends RecyclerView.Adapter<AdapterFollowedCmty.FollowedHolder> implements View.OnClickListener {

    private List<CommunityModel> communityModels;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private View.OnClickListener listener;
    private FirebaseFirestore db;

    public AdapterFollowedCmty(List<CommunityModel> communityModels, Context context) {
        this.communityModels = communityModels;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fixed_community_item,parent,false);
        v.setOnClickListener(this);
        return new FollowedHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowedHolder holder, int position) {
        final CommunityModel communityModel=communityModels.get(position);
        //img
        String url=communityModel.getImg();
        Picasso.get().load(url).into(holder.img_community);
        //name
        String name=communityModel.getName();
        holder.name_community.setText(name);
        //your community or not
        firebaseAuth= FirebaseAuth.getInstance();
        String id=firebaseAuth.getUid();
        if (communityModel.getCreator().equals(id)) {
            holder.cardView_community.setBackgroundResource(R.color.white);
        }else{
            holder.cardView_community.setBackgroundResource(R.color.colorPrimaryDashboard);
        }

    }

    @Override
    public int getItemCount() {
        return communityModels.size();
    }


    //onclick

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public static class FollowedHolder extends RecyclerView.ViewHolder{
        protected TextView name_community;
        protected TextView creator_community;
        protected ImageView img_community;
        protected CardView cardView_community;
        public FollowedHolder(@NonNull View itemView) {
            super(itemView);
            name_community=itemView.findViewById(R.id.name_community);
            img_community=itemView.findViewById(R.id.img_community);
            cardView_community=itemView.findViewById(R.id.cardView_community);
        }
    }

    //unfollow community
    public void unFollow(int posicion,String uiDocument,int followers){
        db= FirebaseFirestore.getInstance();
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


}

