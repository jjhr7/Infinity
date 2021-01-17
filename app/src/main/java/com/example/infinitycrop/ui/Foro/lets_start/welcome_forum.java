package com.example.infinitycrop.ui.Foro.lets_start;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class welcome_forum extends AppCompatActivity {

    private FirebaseUser usuario;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private String id;
    private TextView btn_changeImg;
    private EditText name_user;
    private ImageView image_user;
    private StorageReference mStorageRef;
    private Button btn_continue;
    private  EditText desc_user;
    private Bitmap thumb_bitmap;
    private ImageView btn_back;
    private String image_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_forum);

        name_user=findViewById(R.id.name_user);
        image_user=findViewById(R.id.image_user);
        btn_changeImg=findViewById(R.id.text_changeImg);
        thumb_bitmap=null;
        btn_continue=findViewById(R.id.btn_continue_1);
        desc_user=findViewById(R.id.user_description);
        btn_back=findViewById(R.id.btn_back_welcome);

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseFirestore.getInstance();
        image_url="";
        id=usuario.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        documentReference=db.collection("Usuarios").document(id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    name_user.setText(snapshot.getString("username"));


            }
        });

        Glide.with(this).load(R.drawable.icons_user).into(image_user);

        //btn cambiar imagen
        btn_changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(welcome_forum.this);
            }
        });

        //btn continue
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name_user.getText().toString().equals("")){
                    Toast.makeText(welcome_forum.this,"Indique un nombre valido",Toast.LENGTH_SHORT).show();
                }else{

                    addForumUser();
                }
            }
        });

        //btn back
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    .start(welcome_forum.this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri=result.getUri();
                File url=new File(resultUri.getPath());
                Picasso.get().load(url).into(image_user);
                //comprimir imagen
                try{
                    thumb_bitmap=new Compressor(this)
                            .setQuality(90)
                            .compressToBitmap(url);
                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
                final byte[] thumb_byte= byteArrayOutputStream.toByteArray();

                String urlStorage="foro/user/"+id+"/profile img";
                final StorageReference ref = mStorageRef.child(urlStorage).child("userImg");
                UploadTask uploadTask=ref.putBytes(thumb_byte);
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

                    }
                });



            }
        }
    }

    private void  addForumUser(){
        final Map<String, Object> user = new HashMap<>();
            user.put("name", name_user.getText().toString());
            user.put("description", desc_user.getText().toString());
            user.put("url", image_url);
            db.collection("Foro user").document(id).set(user);

        Intent intent = new Intent(this, choose_community.class);
        startActivity(intent);


    }
}