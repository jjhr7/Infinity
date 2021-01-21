package com.example.infinitycrop.ui.profile.settings;

import android.app.Dialog;
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

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.list_machine.MachineModel;
import com.example.infinitycrop.ui.list_machine.ModifyMachine;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ModifyProfile extends AppCompatActivity {

    private EditText nombre;
    private FirebaseUser usuario;
    private FirebaseFirestore db;
    private static final String TAG = "";

    Dialog dialog;
    private Button button_done;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        dialog=new Dialog(ModifyProfile.this);
        dialog.setContentView(R.layout.animation_done);
        dialog.setCancelable(false);
        button_done=dialog.findViewById(R.id.btn_aceptar);
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToProfile2);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });


        //guardo el nombre en un textView
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        nombre = findViewById(R.id.nombreUsario);
        nombre.setText(usuario.getDisplayName());

        DocumentReference documentReference=db.collection("Usuarios").document(usuario.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(nombre.getText().toString() == ""){
                    nombre.setText(snapshot.getString("username"));
                }
            }
        });

        Button btn_modifyy =findViewById(R.id.btn_modify);
        btn_modifyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNameChanged();
            }
        });

    }


    private void isNameChanged() {

        Map<String, Object> nombreUser = new HashMap<>();
        nombreUser.put("username",nombre.getText().toString());

        db.collection("Usuarios").document(usuario.getUid()).update(nombreUser);

        //Toast.makeText(this, "Los datos han sido modificados correctamente", Toast.LENGTH_SHORT).show();
        dialog.show();

    }
}
