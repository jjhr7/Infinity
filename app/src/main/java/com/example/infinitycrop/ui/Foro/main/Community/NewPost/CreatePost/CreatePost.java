package com.example.infinitycrop.ui.Foro.main.Community.NewPost.CreatePost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.welcome_forum;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class CreatePost extends AppCompatActivity {

    private ImageView img_post;
    private Bitmap thumb_bitmap;
    private TextView btn_changeImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        img_post=findViewById(R.id.img_CreatePost);
        btn_changeImg=findViewById(R.id.txt_changeImgPost);
        CropImage.startPickImageActivity(CreatePost.this);
        btn_changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(CreatePost.this);
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
                Uri resultUri=result.getUri();
                File url=new File(resultUri.getPath());
                Picasso.get().load(url).into(img_post);
                //comprimir imagen
                try{
                    thumb_bitmap=new Compressor(this)
                            .setQuality(90)
                            .compressToBitmap(url);
                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                final byte[] thumb_byte= byteArrayOutputStream.toByteArray();

                /*String urlStorage="foro/user/"+id+"/profile img";
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
                });*/



            }
        }
    }
}