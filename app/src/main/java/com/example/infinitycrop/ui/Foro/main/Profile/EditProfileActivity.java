package com.example.infinitycrop.ui.Foro.main.Profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.CreatePost.CreatePost;
import com.example.infinitycrop.ui.MachineControl.planta1;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    //layout variables
    private EditText input_name;
    private TextView change_img;
    private EditText input_desc;
    private ImageView img_editCommunity;
    private ImageView btn_back;
    private Button btn_create;
    private String name;

    //firebase variables
    private FirebaseUser usuario;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private FirebaseFirestore db;
    private  Uri resultUri;
    private String image_url;
    private DocumentReference documentReference;
    private StorageReference mStorageRef;
    private String id;
    private String names;
    private String uidDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //ALL VARIABLES DECLARADAS
        input_name=findViewById(R.id.nombreperf);
        input_desc=findViewById(R.id.input_desc_EditProfile);
        change_img=findViewById(R.id.changeImg_EditProfile);
        img_editCommunity=findViewById(R.id.img_editProfile);
        btn_back=findViewById(R.id.backEditprofile);
        btn_create=findViewById(R.id.btn_EditProfile);
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        db= FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        img_editCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(EditProfileActivity.this);
            }
        });


        db.collection("Foro user").document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String nombre = documentSnapshot.getString("name");
                            String descipcion = documentSnapshot.getString("description");
                            input_name.setText(nombre);
                            /*imgP.setText(link);*/
                            input_desc.setText(descipcion);
                            /* //set values
                               Picasso.get().load(community.getImg()).into(img_editCommunity);
                               image_url=community.getImg();*/
                        }else{
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        //Onclick
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirPost(v);


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
                    .start(EditProfileActivity.this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                resultUri=result.getUri();
                File url=new File(resultUri.getPath());
                Picasso.get().load(url).into(img_editCommunity);
            }
        }
    }
    private void subirPost(View v){
        //upload image into storage firebase
        String urlStorage="foro/user/"+uid+"/profile img/"+ UUID.randomUUID().toString();
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
                String desc = String.valueOf(input_desc.getText());
                String nom = String.valueOf(input_name.getText());
                Uri dowloadUri=task.getResult();
                image_url=dowloadUri.toString();
                //subir a coleccion posts
                Map<String, Object> post = new HashMap<>();
                post.put("name", nom);
                post.put("description", desc);
                post.put("url", image_url);
                db.collection("Foro user").document(uid).update(post);
                finish();

            }
        });
    }


}
