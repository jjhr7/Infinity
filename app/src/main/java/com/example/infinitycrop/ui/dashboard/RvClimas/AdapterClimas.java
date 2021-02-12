package com.example.infinitycrop.ui.dashboard.RvClimas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comun.Mqtt;
import com.example.infinitycrop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;

import static com.example.comun.Mqtt.topicRoot;

public class AdapterClimas extends RecyclerView.Adapter<AdapterClimas.ClimasHolder>  implements  View.OnClickListener,MqttCallback {

    //Acceso a la maquina
    public static MqttClient client = null;
    private List<ClimaModel> climaModelList;
    private Context context;
    private FirebaseFirestore db;
    private View.OnClickListener listener;
    private FirebaseAuth firebaseAuth;
    private String machineId;

    public AdapterClimas(List<ClimaModel> climaModelList, Context context,String machineId) {
        this.climaModelList = climaModelList;
        this.context = context;
        this.machineId=machineId;
    }

    @NonNull
    @Override
    public ClimasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.static_rv_item,parent,false);
        v.setOnClickListener(this);
        return new ClimasHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClimasHolder holder, int position) {
        db=FirebaseFirestore.getInstance();
        final ClimaModel climaModel=climaModelList.get(position);

        holder.textView.setText(climaModel.getName());
        //String idMachine=climaModel.getMachineId();
        String climaID=climaModel.getUid();
        final String[] idRealMachine = {""};

        //recupera el id del documento en la coleccion Machine

        db.collection("Machine")
                .whereEqualTo("description",machineId)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idRealMachine[0] =document.getId();
                            }
                            db.collection("Machine").document(idRealMachine[0]).collection("Clima")
                                    .document("Activado").get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    String climaActivated= document.getString("climaID");
                                                    if(climaModel.getUid().equals(climaActivated)){
                                                        holder.linearLayout.setBackgroundResource(R.drawable.button_action_machine);
                                                        setClimaTemperatura(machineId,climaModel.getTemperatura(),climaModel.getHumedad(),climaModel.getLuminosidad());
                                                    }
                                                } else {

                                                }
                                            } else {

                                            }
                                        }
                                    });

                        } else {

                        }
                    }
                });


        /*db.collection("Machine")
                .whereEqualTo("description",machineId)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String idRealMachine="";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idRealMachine=document.getId();
                            }

                            db.collection("Machine").document(idRealMachine).collection("Clima")
                                    .document("Activado").get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            String idClima=document.getString("climaID");
                                            if(climaModel.getUid().equals(idClima)){
                                                holder.linearLayout.setBackgroundResource(R.drawable.button_action_machine);
                                            }


                                        } else {

                                        }
                                    } else {

                                    }
                                }
                            });
                        } else {

                        }
                    }
                });*/
    }

    private void getClimaIdActivated(String UidMachine){

    }

    @Override
    public int getItemCount() {
        return climaModelList.size();
    }

    //onclick

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    //MQTT -> INICIO

    public void setClimaTemperatura(String machine,String temperatura,String humedad,String luminosidad){
        String mensaje="5-"+temperatura;
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
        //nos subscribimos a topic
        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "operaciones-"+machine);//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "operaciones-"+machine, Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage(mensaje.getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"operaciones-"+machine, message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
        setClimaHumedad(machineId,humedad,luminosidad);
    }
    public void setClimaHumedad(String machine,String humedad,String luminosidad){
        String mensaje="4-"+humedad;
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
        //nos subscribimos a topic
        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "operaciones-"+machine);//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "operaciones-"+machine, Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage(mensaje.getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"operaciones-"+machine, message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
        setClimaLuminosidad(machine,humedad,luminosidad);
    }
    public void setClimaLuminosidad(String machine,String humedad,String luminosidad){
        String mensaje="6-"+luminosidad;
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
        //nos subscribimos a topic
        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "operaciones-"+machine);//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "operaciones-"+machine, Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage(mensaje.getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"operaciones-"+machine, message);
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



    //MQTT-> FIN

    public static class ClimasHolder extends RecyclerView.ViewHolder{
        protected TextView textView;
        protected ImageView imageView;
        protected LinearLayout linearLayout;
        public ClimasHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            textView=itemView.findViewById(R.id.text);
            linearLayout=itemView.findViewById(R.id.linearLayoutItem);
        }
    }
}
