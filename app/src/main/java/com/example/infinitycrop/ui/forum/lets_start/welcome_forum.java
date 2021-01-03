package com.example.infinitycrop.ui.forum.lets_start;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class welcome_forum extends AppCompatActivity {

    private FirebaseUser usuario;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private String id;

    private EditText name_user;
    private ImageView image_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_forum);

        name_user=findViewById(R.id.name_user);
        image_user=findViewById(R.id.image_user);

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseFirestore.getInstance();

        id=usuario.getUid();

        documentReference=db.collection("Usuarios").document(id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    name_user.setText(snapshot.getString("username"));


            }
        });

        Glide.with(this).load(R.drawable.icons_user).into(image_user);

    }
}