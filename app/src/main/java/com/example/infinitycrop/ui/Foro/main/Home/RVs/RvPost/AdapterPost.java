package com.example.infinitycrop.ui.Foro.main.Home.RVs.RvPost;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.EditCommunity;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.PostHolder>{

    private List<PostModel> postModelList;
    private Context context;
    private FirebaseFirestore db;

    public AdapterPost(List<PostModel> postModelList, Context context) {
        this.postModelList = postModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_item,parent,false);
        return new PostHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, int position) {
        db=FirebaseFirestore.getInstance();
        final PostModel postModel= postModelList.get(position);
        //img
        final String url=postModel.getImg();
        Picasso.get().load(url).into(holder.img_post);
        //tittle post
        String tittle=postModel.getTittle();
        holder.name_post.setText(tittle);
        //desc
        String desc=postModel.getDesc();
        holder.desc_post.setText(desc);
        //time
        CharSequence prettyTime = DateUtils.getRelativeDateTimeString(
                context, postModel.getDate().toDate().getTime(), DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, 0);
        holder.time_post.setText(prettyTime);
        //num likes
        int likes=postModel.getLikes();
        String resLikes=likes+" Likes";
        holder.numLikes_post.setText(resLikes);
        //get names,img of community, user etc
        db.collection("Community").document(postModel.getCommunity())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nameCom=document.getString("name");
                        String urlCom=document.getString("img");
                        holder.name_community_post.setText(nameCom);
                        Picasso.get().load(urlCom).into(holder.img_community_post);
                    } else {

                    }
                } else {

                }
            }
        });
        //onclick on community
        holder.name_community_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.name_community_post.getContext(), CommunityMain.class);
                intent.putExtra("community", postModel.getCommunity());
                holder.name_community_post.getContext().startActivity(intent);
            }
        });
        db.collection("Foro user").document(postModel.getCreator())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nameCreator=document.getString("name");
                        holder.creator_community_post.setText(nameCreator);
                    } else {

                    }
                } else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }


    public static class PostHolder extends RecyclerView.ViewHolder{
        protected TextView name_community_post;
        protected TextView creator_community_post;
        protected TextView name_post;
        protected TextView desc_post;
        protected ImageView img_post;
        protected TextView time_post;
        protected ImageView like_post;
        protected ImageView img_community_post;
        protected TextView numLikes_post;
        public PostHolder(@NonNull View itemView) {
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
