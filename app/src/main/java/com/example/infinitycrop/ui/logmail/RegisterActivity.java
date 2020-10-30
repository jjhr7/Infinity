package com.example.infinitycrop.ui.logmail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    //objetos visibles
    private EditText Textmail;
    private EditText Textname;
    private EditText Textpassword;
    private LinearLayout Btnregistrar;
    private ProgressDialog progressDialog;
    //objeto firebase
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        //referenciamos los views
        Textmail = (EditText) findViewById(R.id.editTextTextPersonName);
        Textname = (EditText) findViewById(R.id.editTextTextPersonName2);
        Textpassword = (EditText) findViewById(R.id.editTextTextPassword);

        Btnregistrar = (LinearLayout) findViewById(R.id.btn_login);
        progressDialog =new ProgressDialog(this);

        //atacamos el listener al boton
        Btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(v);
            }
        });



    }
    private void registrarUsuario(){
        //obtendremos el email y la contraseña desde la caja de texto
        String email = Textmail.getText().toString().trim();
        String nombre = Textname.getText().toString().trim();
        String password = Textpassword.getText().toString().trim();

        //verificamos si las cajas estan vacias o no
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Debes introducir un correo",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Debes introducir una contraseña",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(nombre)){
            Toast.makeText(this,"Debes introducir una nombre",Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Registrando obteniendo contenido en línea...");
        progressDialog.show();
        //creando un nuevo usuario
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //comprovar si el resultado ha sido resuelto correctamente
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Iniciando sesión", Toast.LENGTH_LONG).show();
                        Intent intencion = new Intent (getApplication(), MainActivity.class);
                        startActivity(intencion);

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                            Toast.makeText(RegisterActivity.this, "Este Usuario ya existe", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "No se puede registrar este usuario", Toast.LENGTH_SHORT).show();
                        }

                    }
                    progressDialog.dismiss();
                }

        });
    }
    public void onclick(View view){
        //invocamos al metodo
        registrarUsuario();
    }

}
