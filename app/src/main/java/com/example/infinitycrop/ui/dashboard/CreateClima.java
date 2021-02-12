package com.example.infinitycrop.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
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

public class CreateClima extends AppCompatActivity {


    private String modo;
    private TextInputEditText name_climaCreateClima;
    private EditText temperaturaCreateClima;
    private EditText humedadCreateClima;
    private NumberPicker luminosidadPicker;
    private Button btn_createClima_act;
    private Button btn_cancelar_crearClima;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_clima);

        Bundle extras = getIntent().getExtras();
        modo = extras.getString("modo");

        db=FirebaseFirestore.getInstance();

        name_climaCreateClima=findViewById(R.id.name_climaCreateClima);
        temperaturaCreateClima=findViewById(R.id.temperaturaCreateClima);
        luminosidadPicker=findViewById(R.id.luminosidadPicker);
        luminosidadPicker.setMaxValue(100);
        luminosidadPicker.setMinValue(0);
        humedadCreateClima=findViewById(R.id.humedadCreateClima);
        btn_createClima_act=findViewById(R.id.btn_createClima_act);
        btn_cancelar_crearClima=findViewById(R.id.btn_cancelar_crearClima);
        firebaseAuth= FirebaseAuth.getInstance();
        uid=firebaseAuth.getUid();

        btn_cancelar_crearClima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if(modo.equals("default")){
            db.collection("Climas").document("Default").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                   name_climaCreateClima.setText(document.getString("name"));
                                    temperaturaCreateClima.setText(document.getString("temperatura"));
                                    humedadCreateClima.setText(document.getString("humedad"));
                                    luminosidadPicker.setValue(Integer.parseInt(document.getString("luminosidad")));
                                } else {

                                }
                            } else {

                            }
                        }
                    });
        }


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
                    a.put("luminosidad", String.valueOf(luminosidadPicker.getValue()) );
                    a.put("machineId", "" );
                    a.put("defecto", false );

                    db.collection("Climas").add(a);

                    Toast.makeText(getApplicationContext(),"Clima creado con éxito", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

    }
}