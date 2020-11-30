package com.example.infinitycrop.ui.logmail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    //objetos visibles
    private EditText Textmail;
    private EditText Textpassword;
    private LinearLayout Btnlogin;
    private TextView BtnRegistro;
    private ProgressDialog progressDialog;
    //objeto firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        firebaseAuth = FirebaseAuth.getInstance();
        //referenciamos los views
        Textmail = (EditText) findViewById(R.id.editTextTextPersonName);
        Textpassword = (EditText) findViewById(R.id.editTextTextPassword);

        Btnlogin = (LinearLayout) findViewById(R.id.btn_login);
        BtnRegistro = (TextView) findViewById(R.id.register_buton);
        progressDialog =new ProgressDialog(this);

        //atacamos el listener al boton
        Btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUsuario();
            }
        });
        BtnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(v);
            }
        });



    }
    private void loginUsuario(){
        //obtendremos el email y la contraseña desde la caja de texto
        String email = Textmail.getText().toString().trim();
        String password = Textpassword.getText().toString().trim();

        //verificamos si las cajas estan vacias o no
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Debes introducir tu correo",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Debes introducir tu contraseña",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Obteniendo contenido en línea...");
        progressDialog.show();
        //logueando un nuevo usuario


        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //comprovar si el resultado ha sido resuelto correctamente
                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Iniciando sesión", Toast.LENGTH_LONG).show();
                    //continuar con el dashboard aqui
                    Intent intencion = new Intent (getApplication(), MainActivity.class);
                   startActivity(intencion);
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                        Toast.makeText(LoginActivity.this, "Este Usuario ya existe", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "No se puede registrar este usuario", Toast.LENGTH_SHORT).show();
                    }

                }
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Los datos introducidos no son correctos",
                            Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }
        });
    }

    private void botonRegist() {
        Intent regist = new Intent(this, RegisterActivity.class);
        startActivity(regist);
    }
    public void onclick(View view){

        //invocamos al metodo
        switch (view.getId()){
            case  R.id.btn_registMachine:
                loginUsuario();
            case R.id.register_buton:
                botonRegist();
        }


    }


}