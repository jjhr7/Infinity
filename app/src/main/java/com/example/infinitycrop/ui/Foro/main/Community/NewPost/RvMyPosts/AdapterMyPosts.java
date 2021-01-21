package com.example.infinitycrop.ui.Foro.main.Community.NewPost.RvMyPosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMyPosts extends RecyclerView.Adapter<AdapterMyPosts.MyPostsHolder> {

    private List<PostModel> postModels;
    private Context context;

    public AdapterMyPosts(List<PostModel> postModels, Context context) {
        this.postModels = postModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.img_post_item,parent,false);
        return new MyPostsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsHolder holder, int position) {
        PostModel postModel=postModels.get(position);
        Picasso.get().load(postModel.getImg()).into(holder.imag_post);
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public static class MyPostsHolder extends RecyclerView.ViewHolder{

        protected RoundedImageView imag_post;

        public MyPostsHolder(@NonNull View itemView) {
            super(itemView);
            imag_post=itemView.findViewById(R.id.img_image_post);
        }
    }

}
