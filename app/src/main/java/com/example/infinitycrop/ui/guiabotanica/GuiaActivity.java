package com.example.infinitycrop.ui.guiabotanica;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GuiaActivity extends AppCompatActivity {

    ArrayList<Guia> listaNotificaciones=new ArrayList<>();
    RecyclerView recyclerNotificaciones;
    AdaptadorGuia adaptadorNotificaciones;
    ArrayList<Plants> listaplantas;
    Context context;
    FirebaseFirestore db;
    private String uid;

    String nombre;
    String descripcion;
    String foto;
    String temperatura;
    String humedad;
    String luminosidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia);

        GuiaActivity myActivity = new GuiaActivity();
        //uid=myActivity.getMachineUID();

        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToProfile6);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });
        //

        recyclerNotificaciones = (RecyclerView) findViewById(R.id.RecyclerId);
        recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(this));

        //llenarNotificaciones();
        getListaNotificaciones();

        AdaptadorGuia adapter =new AdaptadorGuia(context, listaNotificaciones);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PlantActivity.class);
                context.startActivity(i);
            }
        });
        recyclerNotificaciones.setAdapter(adapter);

    }

    public void getListaNotificaciones(){

        db = FirebaseFirestore.getInstance();

        db.collection("Plantas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Plants Planta = document.toObject(Plants.class);



                                humedad = Planta.getHumedadPlanta();
                                temperatura = Planta.getTemperaturaPlanta();
                                luminosidad = Planta.getLuminosidadPlanta();
                                foto = Planta.getFotoPlanta();
                                descripcion = Planta.getDescripcionPlanta();
                                nombre = Planta.getNombrePlanta();

                                Guia notificacion = new Guia(nombre,foto, descripcion,humedad,temperatura,luminosidad);
                                listaNotificaciones.add(notificacion);


                            }
                            adaptadorNotificaciones = new AdaptadorGuia(getBaseContext(), listaNotificaciones);
                            recyclerNotificaciones.setAdapter(adaptadorNotificaciones);
                        }
                    }
                });

    }

}