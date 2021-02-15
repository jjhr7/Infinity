package com.example.infinitycrop.ui.control_panel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.comun.Mqtt;
import com.example.infinitycrop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

import static com.example.comun.Mqtt.topicRoot;

public class StatusMachine extends AppCompatActivity implements MqttCallback {

    private String s;
    private FirebaseFirestore db;
    public static MqttClient client = null;
    private FirebaseAuth mAuth;
    private String idUser;
    private FloatingActionButton back;
    private RoundedImageView imageStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_machine);

        Bundle extras = getIntent().getExtras();
        s = extras.getString("idDocumentMachine");

        db=FirebaseFirestore.getInstance();

        back=findViewById(R.id.btn_backStatus);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        db=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
        //Toast.makeText(this,s.toString(), Toast.LENGTH_LONG).show();

        enviarLucesOff();
        imageStatus=findViewById(R.id.imageStatus);
        db.collection("Machine").document(s).collection("Estado").document("activo")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Picasso.get().load(snapshot.getString("Url")).into(imageStatus);
                        } else {

                        }
                    }
                });



    }




    public void enviarLucesOff(){
        try {
            client = new MqttClient(Mqtt.broker, Mqtt.clientId+UUID.randomUUID().toString(), new
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
            Log.i(Mqtt.TAG, "Subscrito a " );//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot+"estado", Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage(s.getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"estado", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
        //Toast.makeText(this,topicRoot+"estado", Toast.LENGTH_LONG).show();
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