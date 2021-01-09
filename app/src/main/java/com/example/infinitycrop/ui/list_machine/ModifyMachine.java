package com.example.infinitycrop.ui.list_machine;

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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        Bundle extras = getIntent().getExtras();
        String machineName =  extras.getString("maq");

        namemachine = findViewById(R.id.nombreMaquina);
        namemachine.setText(machineName);




        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToProfile2);
        backToProfile.setOnClickListener(new View.OnClickListener() {
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

        Button btn_modifyy =findViewById(R.id.btn_modify_machine);
        btn_modifyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMachineNameChanged();
            }
        });

    }

    private void isMachineNameChanged() {

        Map<String, Object> nombreMaquina = new HashMap<>();
        nombreMaquina.put("name",namemachine.getText().toString());
        db.collection("Machine").document(getMachineUID())
                .set(nombreMaquina);

        Toast.makeText(this, "Los datos han sido modificados correctamente", Toast.LENGTH_SHORT).show();
    }


    public String getMachineUID(){
        return s;
    }


}
