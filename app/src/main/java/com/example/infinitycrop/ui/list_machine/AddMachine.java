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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
        if(TextUtils.isEmpty(model)){
            Toast.makeText(this,"El modelo de registro está vacío",Toast.LENGTH_LONG).show();
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
        scanCode();

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
                DocumentReference docIdRef = rootRef.collection("Machine").document(codigo_qr);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                dialogYaExiste(result);
                            } else {
                                dialogAñadirmaquina(result);
                            }
                        } else {

                        }
                    }
                });

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
        final String id=Textmodel.getText().toString().trim();
        machineRef.document(id).set(new MachineModel(TextnameMachine.getText().toString().trim(), priorityMachine, id));
        Toast.makeText(this, "Maquina añadida", Toast.LENGTH_SHORT).show();
        finish();
    }
}
