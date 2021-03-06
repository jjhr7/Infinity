package com.example.infinitycrop.ui.list_machine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMachine extends AppCompatActivity implements View.OnClickListener{
    Button scanBtn;
    private EditText TextnameMachine;
    private EditText Textmodel;
    private CheckBox fav;
    private long priorityMachine;
    private LinearLayout BtnregistMachine;
    private ProgressDialog progressDialogo;
    private ImageView backToProfile;
    private String codigo_qr;
    private CollectionReference machineRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        //Boton escanear
        scanBtn = findViewById(R.id.btn_scan);
        scanBtn.setOnClickListener(this);
        //textos y boton de registrar maquina
        TextnameMachine = (EditText) findViewById(R.id.editTextTextMchineName);
        Textmodel = (EditText) findViewById(R.id.editTextTextMachineModel);
        fav = (CheckBox) findViewById(R.id.btn_fav);
        machineRef = FirebaseFirestore.getInstance().collection("Machine");
        db=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        BtnregistMachine = (LinearLayout) findViewById(R.id.btn_registMachine);
        progressDialogo =new ProgressDialog(this);

        BtnregistMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(v);
            }
        });
        backToProfile =findViewById(R.id.backTomachinelist);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void registMachine(){
        final String name = TextnameMachine.getText().toString().trim();
        final String model = Textmodel.getText().toString().trim();


        //comprobamos si estan vacias o no
        //verificamos si las cajas estan vacias o no
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Debes introducir un nombre",Toast.LENGTH_LONG).show();
            return;
        }
        if(model.equals("Primero escanea el QR")){
            Toast.makeText(this,"Escanea el QR de tu maquina",Toast.LENGTH_LONG).show();
            return;
        }
        if (fav.isSelected()) {

            return;
        }

        progressDialogo.setMessage("Registrando obteniendo contenido en línea...");
        progressDialogo.show();
    }


    @Override
    public void onClick(View v) {
        final String name = TextnameMachine.getText().toString().trim();
        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(),"Debes introducir un nombre",Toast.LENGTH_LONG).show();
        }else {
            scanCode();
        }
    }

    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Escaneando código de tu máquina");
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
        final int[] res = new int[1];
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result!= null){
            if (result.getContents() !=null){
                codigo_qr=result.getContents();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                final CollectionReference docIdRef = rootRef.collection("Machine");
                docIdRef.whereEqualTo("description", codigo_qr)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot document = task.getResult();
                                    if (document.size()>=1) {
                                        dialogYaExiste(result);
                                    } else {
                                        dialogAñadirmaquina(result);
                                    }
                                }else {

                                }
                            }
                        });
                /*docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                dialogYaExiste(result);
                            } else {
                                dialogYaExiste(result);
                            }
                        } else {

                        }
                    }
                });*/

            }
            else {
                Toast.makeText(this, "Sin resultados", Toast.LENGTH_LONG).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    public void dialogAñadirmaquina(IntentResult result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(result.getContents());
        builder.setTitle("El código de tu máquina es");
        builder.setPositiveButton("Escanear de nuevo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                scanCode();
            }
        }).setNegativeButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addMachine();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void dialogYaExiste(IntentResult result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(result.getContents());
        builder.setTitle("Error! El QR ya ha sido utilizado por otro usuario");
        builder.setPositiveButton("Escanear de nuevo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                scanCode();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void onclick(View v){

        //invocamos al metodo
        switch (v.getId()){
            case R.id.btn_registMachine:
                registMachine();
        }
    }
    public void addMachine(){
        Textmodel.setText(codigo_qr);
        if (fav.isChecked()) {
            priorityMachine=1;
        }else{
            priorityMachine=2;
        }
        String uid=firebaseAuth.getUid();
        final String id=Textmodel.getText().toString().trim();
        /*final Map<String, Object> data = new HashMap<>();
        data.put("userUID", uid);*/
        //collecion mediciones general
        final Map<String, Object> a = new HashMap<>();
        a.put("Humedad", "0");
        a.put("Luminosidad", "0");
        a.put("Temperatura", "0");
        a.put("Fecha", new Timestamp(new Date()));
        a.put("machineId", id);
        //collecion mediciones planta 1
        final Map<String, Object> a1 = new HashMap<>();
        a1.put("Humedad", "0");
        a1.put("Humedad Ambiente", "0");
        a1.put("Temperatura", "0");
        a1.put("Fecha", new Timestamp(new Date()));
        a1.put("machineId", id);
        //collecion mediciones planta 2
        final Map<String, Object> a2 = new HashMap<>();
        a2.put("Humedad Ambiente", "0");
        a2.put("Temperatura", "0");
        a2.put("Fecha", new Timestamp(new Date()));
        a2.put("machineId", id);
        machineRef.add(new MachineModel(TextnameMachine.getText().toString().trim(), priorityMachine, id, uid)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    DocumentReference doc=task.getResult();
                    final Map<String, Object> newMach = new HashMap<>();
                    newMach.put("climaID", "Default");
                    String newMachine=doc.getId();
                    db.collection("Machine").document(newMachine).collection("Clima").document("Activado").set(newMach);
                }
            }
        });
        db.collection("Mediciones general").document(id).set(a);
        db.collection("Mediciones planta 1").document(id).set(a1);
        db.collection("Mediciones planta 2").document(id).set(a2);
        //añadir subCollecion datos piso
        final Map<String, Object> planta1 = new HashMap<>();
        planta1.put("fecha", "00/00/0000");
        planta1.put("hora", "00:00 a. m.");
        planta1.put("name", "Planta 1");
        planta1.put("piso", 1);
        planta1.put("estacion","Esta planta se encuentra en la estación número 1");
        planta1.put("estado",0);
        planta1.put("intensidad",50);
        final Map<String, Object> planta2 = new HashMap<>();
        planta2.put("fecha", "00/00/0000");
        planta2.put("hora", "00:00 a. m.");
        planta2.put("name", "Planta 2");
        planta2.put("piso", 1);
        planta2.put("estado",0);
        planta2.put("intensidad",50);
        planta2.put("estacion","Esta planta se encuentra en la estación número 2");
        final Map<String, Object> planta3 = new HashMap<>();
        planta3.put("fecha", "00/00/0000");
        planta3.put("hora", "00:00 a. m.");
        planta3.put("name", "Planta 3");
        planta3.put("piso", 1);
        planta3.put("estado",0);
        planta3.put("estacion","Esta planta se encuentra en la estación número 3");
        planta3.put("intensidad",50);
        final Map<String, Object> planta4 = new HashMap<>();
        planta4.put("fecha", "00/00/0000");
        planta4.put("hora", "00:00 a. m.");
        planta4.put("name", "Planta 4");
        planta4.put("piso", 1);
        planta4.put("estado",0);
        planta4.put("estacion","Esta planta se encuentra en la estación número 4");
        planta4.put("intensidad",50);
        final Map<String, Object> planta5 = new HashMap<>();
        planta5.put("fecha", "00/00/0000");
        planta5.put("hora", "00:00 a. m.");
        planta5.put("name", "Planta 5");
        planta5.put("piso", 1);
        planta5.put("estado",0);
        planta5.put("estacion","Esta planta se encuentra en la estación número 5");
        planta5.put("intensidad",50);
        final Map<String, Object> planta6 = new HashMap<>();
        planta6.put("fecha", "00/00/0000");
        planta6.put("hora", "00:00 a. m.");
        planta6.put("name", "Planta 6");
        planta6.put("piso", 1);
        planta6.put("estado",0);
        planta6.put("estacion","Esta planta se encuentra en la estación número 6");
        planta6.put("intensidad",50);
        final Map<String, Object> planta7 = new HashMap<>();
        planta7.put("fecha", "00/00/0000");
        planta7.put("hora", "00:00 a. m.");
        planta7.put("name", "Planta 7");
        planta7.put("piso", 1);
        planta7.put("estado",0);
        planta7.put("estacion","Esta planta se encuentra en la estación número 7");
        planta7.put("intensidad",50);
        final Map<String, Object> planta8 = new HashMap<>();
        planta8.put("fecha", "00/00/0000");
        planta8.put("hora", "00:00 a. m.");
        planta8.put("name", "Planta 8");
        planta8.put("piso", 1);
        planta8.put("estado",0);
        planta8.put("estacion","Esta planta se encuentra en la estación número 8");
        planta8.put("intensidad",50);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta1").set(planta1);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta2").set(planta2);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta3").set(planta3);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta4").set(planta4);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta5").set(planta5);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta6").set(planta6);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta7").set(planta7);
        db.collection("Mediciones planta 1").document(id).collection("DatosPiso")
                .document("planta8").set(planta8);


        //añadir subCollecion datos piso 2
        final Map<String, Object> planta9 = new HashMap<>();
        planta9.put("fecha", "00/00/0000");
        planta9.put("hora", "00:00 a. m.");
        planta9.put("name", "Planta 1");
        planta9.put("piso", 2);
        planta9.put("estacion","Esta planta se encuentra en la estación número 9");
        planta9.put("estado",0);
        planta9.put("intensidad",50);
        final Map<String, Object> planta10 = new HashMap<>();
        planta10.put("fecha", "00/00/0000");
        planta10.put("hora", "00:00 a. m.");
        planta10.put("name", "Planta 2");
        planta10.put("piso", 2);
        planta10.put("estado",0);
        planta10.put("estacion","Esta planta se encuentra en la estación número 10");
        planta10.put("intensidad",50);
        final Map<String, Object> planta11 = new HashMap<>();
        planta11.put("fecha", "00/00/0000");
        planta11.put("hora", "00:00 a. m.");
        planta11.put("name", "Planta 3");
        planta11.put("piso", 2);
        planta11.put("estado",0);
        planta11.put("estacion","Esta planta se encuentra en la estación número 11");
        planta11.put("intensidad",50);
        final Map<String, Object> planta12 = new HashMap<>();
        planta12.put("fecha", "00/00/0000");
        planta12.put("hora", "00:00 a. m.");
        planta12.put("name", "Planta 4");
        planta12.put("piso", 2);
        planta12.put("estado",0);
        planta12.put("estacion","Esta planta se encuentra en la estación número 12");
        planta12.put("intensidad",50);
        final Map<String, Object> planta13 = new HashMap<>();
        planta13.put("fecha", "00/00/0000");
        planta13.put("hora", "00:00 a. m.");
        planta13.put("name", "Planta 5");
        planta13.put("piso", 2);
        planta13.put("estado",0);
        planta13.put("estacion","Esta planta se encuentra en la estación número 13");
        planta13.put("intensidad",50);
        final Map<String, Object> planta14 = new HashMap<>();
        planta14.put("fecha", "00/00/0000");
        planta14.put("hora", "00:00 a. m.");
        planta14.put("name", "Planta 6");
        planta14.put("piso", 2);
        planta14.put("estado",0);
        planta14.put("estacion","Esta planta se encuentra en la estación número 14");
        planta14.put("intensidad",0);
        final Map<String, Object> planta15 = new HashMap<>();
        planta15.put("fecha", "00/00/0000");
        planta15.put("hora", "00:00 a. m.");
        planta15.put("name", "Planta 7");
        planta15.put("piso", 2);
        planta15.put("estado",0);
        planta15.put("estacion","Esta planta se encuentra en la estación número 15");
        planta15.put("intensidad",0);
        final Map<String, Object> planta16 = new HashMap<>();
        planta16.put("fecha", "00/00/0000");
        planta16.put("hora", "00:00 a. m.");
        planta16.put("name", "Planta 8");
        planta16.put("piso", 2);
        planta16.put("estado",0);
        planta16.put("estacion","Esta planta se encuentra en la estación número 16");
        planta16.put("intensidad",50);

        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta1").set(planta9);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta2").set(planta10);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta3").set(planta11);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta4").set(planta12);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta5").set(planta13);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta6").set(planta14);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta7").set(planta15);
        db.collection("Mediciones planta 2").document(id).collection("DatosPiso")
                .document("planta8").set(planta16);


        //Toast.makeText(this, "Maquina añadida", Toast.LENGTH_SHORT).show();
        finish();
    }
}
