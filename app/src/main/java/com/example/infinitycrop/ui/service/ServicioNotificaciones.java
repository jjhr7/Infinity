package com.example.infinitycrop.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.infinitycrop.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServicioNotificaciones extends Service {

    private FirebaseFirestore db;
    private String getId;


    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        getId =intenc.getStringExtra("machine");
        return START_STICKY;
    }

    @Override public void onCreate() {
        db = FirebaseFirestore.getInstance();

        db.collection("Mediciones general").document(getId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {

                            //Temperatura

                            Long medidaT=snapshot.getLong("Temperatura");
                            if(medidaT < 13){

                                final Map<String, Object> notificacionesTempAlerta = new HashMap<>();
                                notificacionesTempAlerta.put("Tipo", 1);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones temperatura").set(notificacionesTempAlerta);
                            }


                            if(medidaT >= 13 && medidaT <= 16) {
                                //collecion notificaciones temperatura
                                final Map<String, Object> notificacionesTempAviso = new HashMap<>();
                                notificacionesTempAviso.put("Tipo", 2);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones temperatura").set(notificacionesTempAviso);
                            }

                            //Humedad

                            Long medidaH=snapshot.getLong("Humedad");
                            if(medidaH < 20){
                                final Map<String, Object> notificacionesHumedadAlerta = new HashMap<>();
                                notificacionesHumedadAlerta.put("Tipo", 1);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones humedad").set(notificacionesHumedadAlerta);
                            }
                            if(medidaH >= 20 && medidaT <= 30) {
                                //collecion notificaciones temperatura
                                final Map<String, Object> notificacionesHumedadAviso = new HashMap<>();
                                notificacionesHumedadAviso.put("Tipo", 2);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones humedad").set(notificacionesHumedadAviso);
                            }

                            //Humedad Ambiente

                            Long medidaHA=snapshot.getLong("Humedad Ambiente");
                            if(medidaHA < 20){
                                final Map<String, Object> notificacionesHumedadAmbAlerta = new HashMap<>();
                                notificacionesHumedadAmbAlerta.put("Tipo", 1);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones humedad ambiente").set(notificacionesHumedadAmbAlerta);
                            }
                            if(medidaHA >= 20 && medidaHA <= 30) {
                                //collecion notificaciones temperatura
                                final Map<String, Object> notificacionesHumedadAmbAviso = new HashMap<>();
                                notificacionesHumedadAmbAviso.put("Tipo", 2);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones humedad amnbiente").set(notificacionesHumedadAmbAviso);
                            }

                            //Luminosidad

                            Long medidaL=snapshot.getLong("Luminosidad");
                            if(medidaL <= 30){
                                final Map<String, Object> notificacionesLuminosidadAlerta = new HashMap<>();
                                notificacionesLuminosidadAlerta.put("Tipo", 1);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones luminosidad").set(notificacionesLuminosidadAlerta);
                            }
                            if(medidaL <= 50) {
                                //collecion notificaciones temperatura
                                final Map<String, Object> notificacionesLuminosidadAviso = new HashMap<>();
                                notificacionesLuminosidadAviso.put("Tipo", 2);
                                db.collection("Notificaciones").document(getId).collection("Notificaciones general").document("Notificaciones luminosidad").set(notificacionesLuminosidadAviso);
                            }

                        }
                    }
                });



    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override public void onDestroy() {

    }


}
