package com.example.infinitycrop.ui.Foro.main.Community.NewPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.main.Community.CommunityMain;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.example.infinitycrop.ui.profile.settings.ModifyProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PostView extends AppCompatActivity {

    private String idPost;
    private FirebaseFirestore db;
    //funciona
    private FloatingActionButton btn_back;
    private ImageView img_community;
    private TextView name_community;
    private TextView name_creator;
    private TextView name_post;
    private TextView desc_post;
    private TextView time;
    private ImageView img_post;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton btn_delete;
    private String uid;

    Dialog dialog1;
    private Button button_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Bundle extras = getIntent().getExtras();
        idPost = extras.getString("Post");

        dialog1=new Dialog(PostView.this);
        dialog1.setContentView(R.layout.animation_done);
        dialog1.setCancelable(false);
        button_done=dialog1.findViewById(R.id.btn_aceptar);
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                finish();
            }
        });

        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();
        btn_back=findViewById(R.id.btn_back_ViewPost);
        btn_delete=findViewById(R.id.btn_deletePost);
        img_community=findViewById(R.id.img_community_ViewPost);
        name_community=findViewById(R.id.name_community_ViewPost);
        name_creator=findViewById(R.id.creator_community_ViewPost);
        name_post=findViewById(R.id.name_post_ViewPost);
        desc_post=findViewById(R.id.desc_post_ViewPost);
        time=findViewById(R.id.time_post_ViewPost);
        img_post=findViewById(R.id.img_post_ViewPost);
        btn_delete.setVisibility(View.GONE);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrar();
            }
        });

        db.collection("Posts").document(idPost)
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final PostModel postModel=document.toObject(PostModel.class);
                        Picasso.get().load(postModel.getImg()).into(img_post);
                        name_post.setText(postModel.getTittle());
                        desc_post.setText(postModel.getDesc());
                        CharSequence prettyTime = DateUtils.getRelativeDateTimeString(
                                getApplicationContext(), postModel.getDate().toDate().getTime(), DateUtils.SECOND_IN_MILLIS,
                                DateUtils.WEEK_IN_MILLIS, 0);
                        time.setText(prettyTime);
                        if(postModel.getCreator().equals(uid)){
                            btn_delete.setVisibility(View.VISIBLE);
                        }

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
                                        name_community.setText(nameCom);
                                        Picasso.get().load(urlCom).into(img_community);
                                        //onclick on community
                                        name_community.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), CommunityMain.class);
                                                intent.putExtra("community", postModel.getCommunity());
                                                startActivity(intent);
                                            }
                                        });
                                    } else {

                                    }
                                } else {

                                }
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
                                        name_creator.setText(nameCreator);
                                    } else {

                                    }
                                } else {

                                }
                            }
                        });

                    } else {

                    }
                } else {

                }
            }
        });



    }

    private void borrar(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quieres eliminar este post?");
        builder.setTitle("Eliminar post");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("Posts").document(idPost).delete();
                Toast.makeText(getApplicationContext(),"Tu post ha sido eliminado", Toast.LENGTH_LONG).show();
                //finish();
                dialog.cancel();
                ventanaEmergente();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ventanaEmergente(){
        dialog1.show();
    }
}