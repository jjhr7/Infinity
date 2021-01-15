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

    private CheckBox favo;
    private long priorityMachine;
    private EditText namemachine;
    private FirebaseFirestore db;
    private static final String TAG = "";
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_machine);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("maq");
        id = extras.getString("identificator");

        db = FirebaseFirestore.getInstance();

        namemachine = findViewById(R.id.nombreMaquina);
        namemachine.setText(name);

        //volver a la anterior actividad
        ImageView backToList =findViewById(R.id.backToListMachine);
        backToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });

        //firebase
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();

        DocumentReference documentReference=fStore.collection("Machine").document(getMachineUID());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(namemachine.getText().toString() == ""){
                    namemachine.setText(snapshot.getString("name"));
                }
            }
        });


        favo = findViewById(R.id.btn_fav_editar);

        DocumentReference docRef=fStore.collection("Machine").document(getMachineUID());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(snapshot.getLong("priority") == 1){
                    favo.setChecked(true);
                }
            }
        });


       /* DocumentReference docRef=fStore.collection("Machine").document(getMachineUID());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(priorityCheck == 1){
                    favo.isChecked(snapshot.getString("priority"));
                    favo.isSelected();
                }
            }
        });

        */



        Button btn_modifyy =findViewById(R.id.btn_modify_machine);
        btn_modifyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMachineNameChanged();
                isCheckBoxChanged();
            }
        });

    }


    private void isMachineNameChanged() {

        Map<String, Object> nombreMaq = new HashMap<>();
        nombreMaq.put("name",namemachine.getText().toString());

        db.collection("Machine").document(getMachineUID()).update(nombreMaq);

        Toast.makeText(this, "Los datos han sido modificados correctamente", Toast.LENGTH_SHORT).show();
    }

    private void isCheckBoxChanged() {

        if (favo.isChecked()){
            priorityMachine=1;
        }else{
            priorityMachine=2;
        }

        Map<String, Object> priorityMaq = new HashMap<>();

        priorityMaq.put("priority",priorityMachine);

        db.collection("Machine").document(getMachineUID()).update(priorityMaq);

        Toast.makeText(this, "Los datos han sido modificados correctamente", Toast.LENGTH_SHORT).show();
    }

    public String getMachineUID(){
        return id;
    }
}