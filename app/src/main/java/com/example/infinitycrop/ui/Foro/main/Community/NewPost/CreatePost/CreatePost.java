package com.example.infinitycrop.ui.Foro.main.Community.NewPost.CreatePost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.lets_start.welcome_forum;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.PostModel;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RvPost.AdapterPost;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class CreatePost extends AppCompatActivity {
//andrey
    private ImageView img_post;
    private Bitmap thumb_bitmap;
    private TextView btn_changeImg;
    private StorageReference mStorageRef;
    private Button btn_back;
    private TextInputEditText input_name;
    private TextInputEditText desc_name;
    private Button btn_createPost;
    private String image_url;
    private  Uri resultUri;
    private FirebaseUser usuario;
    private FirebaseFirestore db;
    private String id;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Bundle extras = getIntent().getExtras();
        uid = extras.getString("community");

        btn_back=findViewById(R.id.btnBackCreatePost);
        img_post=findViewById(R.id.img_CreatePost);
        btn_changeImg=findViewById(R.id.txt_changeImgPost);
        input_name=findViewById(R.id.input_editText_Createpost);
        desc_name=findViewById(R.id.input_desc_editText_Createpost);
        btn_createPost=findViewById(R.id.btn_createButtonPost);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseFirestore.getInstance();
        id=usuario.getUid();
        //init fotopicker
        CropImage.startPickImageActivity(CreatePost.this);
        //onclicks
        btn_changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(CreatePost.this);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarActivity();
            }
        });
        btn_createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarInputs();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageuri=CropImage.getPickImageResultUri(this,data);
            //recortar imagen
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(CreatePost.this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                resultUri=result.getUri();
                File url=new File(resultUri.getPath());
                Picasso.get().load(url).into(img_post);
            }
        }
    }
    private void comprobarInputs(){
        if(input_name.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Indique el título del post", Toast.LENGTH_LONG).show();
        }else if(img_post.getDrawable()==null){
            Toast.makeText(getApplicationContext(),"Seleccione una imagen para el post", Toast.LENGTH_LONG).show();
        }else{
            //all gucci
            subirPost();
        }
    }

    private void subirPost(){
        //upload image into storage firebase
        String urlStorage="foro/communities/"+uid+"/posts/"+UUID.randomUUID().toString();
        final StorageReference ref = mStorageRef.child(urlStorage);
        UploadTask uploadTask=ref.putFile(resultUri);
        //subit imagen storage
        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri dowloadUri=task.getResult();
                image_url=dowloadUri.toString();
                //subir a coleccion posts
                Map<String, Object> post = new HashMap<>();
                post.put("creator", id);post.put("community", uid);
                post.put("likes", 0);post.put("tittle", input_name.getText().toString());
                post.put("desc", desc_name.getText().toString());post.put("comments", "");
                post.put("img", image_url);post.put("date", new Timestamp(new Date()));
                db.collection("Posts").add(post);
                sumNumOfPost();
                cerrarActivity();
            }
        });
    }

    private void sumNumOfPost(){
        db.collection("Community").document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            CommunityModel posts=document.toObject(CommunityModel.class);
                            int numPosts=posts.getPosts();
                            numPosts++;
                            db.collection("Community").document(uid).update("posts",numPosts);
                        } else {

                        }
                    } else {

                    }
                }
            });
    }

    private void cerrarActivity(){
        finish();
    }
}