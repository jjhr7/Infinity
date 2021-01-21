package com.example.infinitycrop.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarClima extends AppCompatActivity {


    private String modo;
    private TextInputEditText name_climaCreateClima;
    private EditText temperaturaCreateClima;
    private EditText humedadCreateClima;
    private Button btn_createClima_act;
    private Button btn_cancelar_crearClima;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String id;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_clima);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        db=FirebaseFirestore.getInstance();

        name_climaCreateClima=findViewById(R.id.name_climaEditClima);
        temperaturaCreateClima=findViewById(R.id.temperaturaEditClima);
        humedadCreateClima=findViewById(R.id.humedadEditClima);
        btn_createClima_act=findViewById(R.id.btn_EditClima_act);
        btn_cancelar_crearClima=findViewById(R.id.btn_cancelar_EditClima);
        firebaseAuth= FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();


        db.collection("Climas").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                name_climaCreateClima.setText(document.getString("name"));
                                temperaturaCreateClima.setText(document.getString("temperatura"));
                                humedadCreateClima.setText(document.getString("humedad"));
                            } else {

                            }
                        } else {

                        }
                    }
                });

        btn_cancelar_crearClima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_createClima_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name_climaCreateClima.getText().equals("") || temperaturaCreateClima.getText().toString().equals("") || humedadCreateClima.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Introduce todos los datos necesarios", Toast.LENGTH_LONG).show();
                }else{


                    final Map<String, Object> a = new HashMap<>();
                    a.put("name", name_climaCreateClima.getText().toString());
                    a.put("humedad", humedadCreateClima.getText().toString());
                    a.put("temperatura", temperaturaCreateClima.getText().toString() );
                    a.put("creator", uid );
                    a.put("machineId", "" );
                    a.put("defecto", false );

                    db.collection("Climas").document(id).update(a);

                    Toast.makeText(getApplicationContext(),"Clima editado con éxito", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }
}