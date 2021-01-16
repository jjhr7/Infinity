package com.example.infinitycrop.ui.Foro.lets_start;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
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

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

import id.zelory.compressor.Compressor;

public class EditCommunity extends AppCompatActivity {

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
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private StorageReference mStorageRef;
    private String id;
    private String names;
    private String uidDocument;
    //change image variables
    private String image_url;
    private Bitmap thumb_bitmap;
    private byte[] thumb_byte;
    //community class
    private CommunityModel community;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_community);

        //get the id of the docuemnt in the extra of the intent
        Bundle extras = getIntent().getExtras();
        uidDocument = extras.getString("community");

        //layout variables
        input_name=findViewById(R.id.input_name_EditCommunity);
        input_desc=findViewById(R.id.input_desc_EditCommunity);
        change_img=findViewById(R.id.changeImg_EditCommunity);
        img_editCommunity=findViewById(R.id.img_bS_Editcommunity);
        btn_back=findViewById(R.id.backEditCommunity);
        btn_create=findViewById(R.id.btn_EditCommunity);
        //change img variables
        thumb_bitmap=null;
        image_url="";
        //firebase variables
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        id=usuario.getUid();

        //get all the data of the document
        db.collection("Community").document(uidDocument).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                community=new CommunityModel();
                community.setName(value.get("name").toString());
                community.setDescription(value.get("description").toString());
                community.setCreator(value.get("creator").toString());
                community.setImg(value.get("img").toString());
                //set values
                input_name.setText(community.getName());
                input_desc.setText(community.getDescription());
                Picasso.get().load(community.getImg()).into(img_editCommunity);
                image_url=community.getImg();
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
                names=input_name.getText().toString();
                if(names.equals("")){
                    Toast.makeText(getApplicationContext(),"Indique un nombre valido",Toast.LENGTH_LONG).show();
                }else{
                    NameIsUsed();
                }
            }
        });
        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_name.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Indique un nombre valido",Toast.LENGTH_LONG).show();
                }else{
                    CropImage.startPickImageActivity(EditCommunity.this);
                }
            }
        });
    }
    public void NameIsUsed(){
        if(names.equals(community.getName())){
            saveCommunity();
        }else{
            db.collection("Community")
                    .whereEqualTo("name", names)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot document = task.getResult();
                                if (document.size()>=1) {
                                    Toast.makeText(getApplicationContext(),"El nombre ya existe",Toast.LENGTH_LONG).show();
                                } else {
                                    saveCommunity();
                                }
                            } else {

                            }
                        }
                    });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageuri=CropImage.getPickImageResultUri(this,data);
            //recortar imagen
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(EditCommunity.this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri=result.getUri();
                File url=new File(resultUri.getPath());
                Picasso.get().load(url).into(img_editCommunity);
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
                thumb_byte= byteArrayOutputStream.toByteArray();
                String urlStorage="foro/user/"+id+"/community";
                //save img in storage
                name=input_name.getText().toString();
                final StorageReference ref = mStorageRef.child(urlStorage).child(name);
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
    public void saveCommunity(){
        //create the community document
        if(!image_url.equals(community.getImg())){
            db.collection("Community").document(uidDocument).update("img",image_url);
        }
        if(!input_name.getText().toString().equals(community.getName())){
            db.collection("Community").document(uidDocument).update("name",input_name.getText().toString());
        }
        if(!input_desc.getText().toString().equals(community.getDescription())){
            db.collection("Community").document(uidDocument).update("description",input_desc.getText().toString());
        }
        finish();
    }
}