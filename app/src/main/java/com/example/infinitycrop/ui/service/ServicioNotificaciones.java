package com.example.infinitycrop.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.infinitycrop.MainActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ServicioNotificaciones extends Service {

    private FirebaseFirestore db;
    private String getId;
    private String uid;

    @Override public void onCreate() {
        db = FirebaseFirestore.getInstance();
        MainActivity myActivity = (MainActivity) getApplicationContext();
        uid=myActivity.getMachineUID();
        db.collection("Mediciones general")
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            //Temperatura
                            String medidaT=snapshot.getString("Temperatura");
                            //Humedad
                            String medidaH=snapshot.getString("Humedad");
                            //Humedad Ambiente
                            String medidaHA=snapshot.getString("Humedad Ambiente");
                            //Luminosidad
                            String medidaL=snapshot.getString("Luminosidad");



                        } else {

                        }
                    }
                });

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        getId =intenc.getStringExtra("machine");


        return START_STICKY;
    }
    @Override public void onDestroy() {

    }


}
