package com.example.infinitycrop.ui.dashboard;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.comun.Mqtt;
import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static com.example.comun.Mqtt.topicRoot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Planta2Fragment extends Fragment implements MqttCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String uid;
    private FirebaseFirestore db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Switch switchLuz;
    public static MqttClient client = null;
    public Planta2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_planta2, container, false);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        switchLuz=v.findViewById(R.id.switchLuminosidadPlanta2);
        db = FirebaseFirestore.getInstance();
        MainActivity myActivity = (MainActivity) getActivity();
        final TextView medidaTemp=v.findViewById(R.id.medidaTemperaturaPlanta2);
        /*final TextView medidaHum=v.findViewById(R.id.medidaHumedadPlanta2);*/
        final TextView medidaHumAm=v.findViewById(R.id.medidaSalinidadPlanta2);
        /*final TextView medidaLuz=v.findViewById(R.id.medidasLuminosidadPlanta2);*/
        uid=myActivity.getMachineUID();
        db.collection("Mediciones planta 2")
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
                            medidaTemp.setText(medidaT+"°C");
                            //Humedad
                            /*String medidaH=snapshot.getString("Humedad");
                            medidaHum.setText(medidaH+"%");*/
                            //Humedad Ambiente
                            String medidaHA=snapshot.getString("Humedad Ambiente");
                            medidaHumAm.setText(medidaHA+"%");
                            //Luminosidad
                            /*String medidaL=snapshot.getString("Luminosidad");
                            medidaLuz.setText(medidaL+"%");*/


                        } else {

                        }
                    }
                });

        //switch luz
        /*databaseReference.child("Mediciones general")
                .child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String LuzPieza1= snapshot.child("LuzPieza1").getValue().toString();
                    String LuzPieza2=snapshot.child("LuzPieza2").getValue().toString();
                    String LuzPieza3=snapshot.child("LuzPieza3").getValue().toString();
                    String LuzPuerta=snapshot.child("LuzPuerta").getValue().toString();
                    String LuzTecho=snapshot.child("LuzTecho").getValue().toString();

                    if(LuzPieza3.equals("0") && LuzPuerta.equals("0")){
                        switchLuz.setChecked(true);
                    }else if(LuzPieza3.equals("1") && LuzPuerta.equals("1")){
                        switchLuz.setChecked(false);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        switchLuz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    enviarLucesOn();
                } else {
                    // The toggle is disabled
                    enviarLucesOff();
                }
            }
        });
        return v;
    }


    public void enviarLucesOff(){
        try {
            client = new MqttClient(Mqtt.broker, Mqtt.clientId, new
                    MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setWill(topicRoot+"WillTopic", "App desconectada".getBytes(),Mqtt.qos, false);

        try {
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //conexión con el broker Root1 = Lector de datos
        //nos subscribimos a topic lectura
        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "operaciones-"+uid);//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "operaciones-"+uid, Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage("3-OFF".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"operaciones-"+uid, message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }
    public void enviarLucesOn(){
        try {
            client = new MqttClient(Mqtt.broker, Mqtt.clientId, new
                    MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setWill(topicRoot+"WillTopic", "App desconectada".getBytes(),Mqtt.qos, false);

        try {
            client.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //conexión con el broker Root1 = Lector de datos
        //nos subscribimos a topic lectura
        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "operaciones-"+uid);//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "operaciones-"+uid, Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage("3-ON".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"operaciones-"+uid, message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}