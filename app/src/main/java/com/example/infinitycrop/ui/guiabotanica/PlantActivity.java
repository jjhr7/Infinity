package com.example.infinitycrop.ui.guiabotanica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.infinitycrop.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlantActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    ImageView imageView;
    TextView nombre1;
    TextView descripcion;
    TextView humedad;
    TextView temperatura;
    TextView luminosidad;
    private String uid;
    String nombre, info, foto;
    String humedad2;

    private List<Plants> plants = new ArrayList<>();
    //Set para que no se repitan las id de las comunidades y que no se dupliquen
    private Set<String> plantsId = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);


        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToGuide);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });


        getIncomingIntent();
    }
    private void getIncomingIntent() {
        if(getIntent().hasExtra("nombre") && getIntent().hasExtra("info")){

            String foto = getIntent().getStringExtra("foto");
            String nombre = getIntent().getStringExtra("nombre");
            String descripcion=getIntent().getStringExtra("info");
            String humedad=getIntent().getStringExtra("humedad");
            String temperatura=getIntent().getStringExtra("temperatura");
            String luminosidad=getIntent().getStringExtra("luminosidad");


            TextView name = findViewById(R.id.nombreID);
            name.setText(nombre);

            TextView description = findViewById(R.id.descripcionID);
            description.setText(descripcion);

            TextView humidity = findViewById(R.id.humedadID);
            humidity.setText(humedad);

            TextView temperature = findViewById(R.id.temperaturaID);
            temperature.setText(temperatura);

            TextView luminosity = findViewById(R.id.luminosidadID);
            luminosity.setText(luminosidad);

            ImageView image = findViewById(R.id.imageView7);
            Glide.with(this)
                    .asBitmap()
                    .load(foto)
                    .into(image);


        }

    }

}
