package com.example.infinitycrop.ui.Foro.main.Community.Posts.Rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPostsCommunity extends RecyclerView.Adapter<AdapterPostsCommunity.PostsHolder> {

    private List<PostModel> postModels;
    private Context context;
    private FirebaseFirestore db;

    public AdapterPostsCommunity(List<PostModel> postModels, Context context) {
        this.postModels = postModels;
        this.context = context;
    }

    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_item,parent,false);
        return new PostsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, int position) {
        db= FirebaseFirestore.getInstance();
        final PostModel postModel=postModels.get(position);
        //img
        final String url=postModel.getImg();
        Picasso.get().load(url).into(holder.img_post);
        //tittle post
        String tittle=postModel.getTittle();
        holder.name_post.setText(tittle);
        //desc
        String desc=postModel.getDesc();
        holder.desc_post.setText(desc);
        //num likes
        int likes=postModel.getLikes();
        String resLikes=likes+" Likes";
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public static class PostsHolder extends RecyclerView.ViewHolder{
        protected TextView name_community_post;
        protected TextView creator_community_post;
        protected TextView name_post;
        protected TextView desc_post;
        protected ImageView img_post;
        protected TextView time_post;
        protected ImageView like_post;
        protected ImageView img_community_post;
        protected TextView numLikes_post;
        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            name_community_post=itemView.findViewById(R.id.name_community_post);
            creator_community_post=itemView.findViewById(R.id.creator_community_post);
            img_community_post=itemView.findViewById(R.id.img_community_post);
            name_post=itemView.findViewById(R.id.name_post);
            desc_post=itemView.findViewById(R.id.desc_post);
            img_post=itemView.findViewById(R.id.img_post);
            time_post=itemView.findViewById(R.id.time_post);
            like_post=itemView.findViewById(R.id.like_post);
            numLikes_post=itemView.findViewById(R.id.numLikes_post);
        }
    }
}
