package com.example.infinitycrop.ui.list_machine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.infinitycrop.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.profile.settings.ModifyProfile;
import com.example.infinitycrop.ui.service.ServicioMqtt;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class ModifyMachine extends AppCompatActivity {

    private CheckBox fav;
    private long priorityMachine;
    private TextView nombre;
    private EditText namemachine;
    private FirebaseUser usuario;
    private FirebaseFirestore db;
    private static final String TAG = "";
    private String s;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_machine);

        Bundle extras = getIntent().getExtras();
        s = extras.getString("maq");

    }
}