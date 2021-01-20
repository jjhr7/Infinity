package com.example.infinitycrop.ui.recycler_control;

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

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

import static com.example.comun.Mqtt.topicRoot;

public class StaticRvAdapter extends RecyclerView.Adapter<StaticRvAdapter.StaticTVViewHolder> implements MqttCallback {
    public static MqttClient client = null;
    private ArrayList<StaticRvModel> items;
    int row_index=-1;
    TextView textMedidasTemp;
    TextView textMedidasH;
    TextView textMedidasS;
    TextView textMedidasL;

    public StaticRvAdapter(ArrayList<StaticRvModel> items) {
        this.items = items;

    }

    @NonNull
    @Override
    public StaticTVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.static_rv_item,parent,false);
        StaticTVViewHolder staticTVViewHolder=new StaticTVViewHolder(view);
        return  staticTVViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StaticTVViewHolder holder, final int position) {
        final StaticRvModel currentItem=items.get(position);
        holder.imageView.setImageResource(currentItem.getImage());
        holder.textView.setText(currentItem.getText());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;

                if(currentItem.getText().equals("Soleado")){
                    /*textMedidasTemp.setText("23°C");
                    textMedidasH.setText("60%");
                    textMedidasS.setText("70%");
                    textMedidasL.setText("99%");*/
                    enviarMensajeNoVentiladores();
                }else if(currentItem.getText().equals("Nocturno")){
                   /* textMedidasTemp.setText("8°C");
                    textMedidasH.setText("32%");
                    textMedidasS.setText("19%");
                    textMedidasL.setText("70%");*/
                    enviarMensajeNocturno();
                } else if(currentItem.getText().equals("Ahorro")){
                    /*textMedidasTemp.setText("26°C");
                    textMedidasH.setText("99%");
                    textMedidasS.setText("34%");
                    textMedidasL.setText("13%");*/
                    enviarMensajeNoMotor();
                } else if(currentItem.getText().equals("Apagado")){
                    /*textMedidasTemp.setText("0FF");
                    textMedidasH.setText("OFF");
                    textMedidasS.setText("OFF");
                    textMedidasL.setText("OFF");*/
                } else if(currentItem.getText().equals("Custom")){
                    /*textMedidasTemp.setText("19°C");
                    textMedidasH.setText("23%");
                    textMedidasS.setText("87%");
                    textMedidasL.setText("78%");*/
                }
                notifyDataSetChanged();
            }
        });

        if(row_index == position){
            holder.linearLayout.setBackgroundResource(R.drawable.static_rv_selected);
        }else{
            holder.linearLayout.setBackgroundResource(R.drawable.static_rv_bg);

        }
    }

    public void enviarMensajeNocturno(){
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
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "luces");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "luces", Mqtt.qos);
            client.setCallback(StaticRvAdapter.this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage("luces OFF".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"luces", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }
    public void enviarMensajeNoMotor(){
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
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "riego");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "riego", Mqtt.qos);
            client.setCallback(StaticRvAdapter.this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage("riego OFF".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"riego", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }
    public void enviarMensajeNoVentiladores(){
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
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "ventiladores");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "ventiladores", Mqtt.qos);
            client.setCallback(StaticRvAdapter.this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage("ventiladores OFF".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"ventiladores", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");
    }

    public static class  StaticTVViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;
        public StaticTVViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            textView=itemView.findViewById(R.id.text);
            linearLayout=itemView.findViewById(R.id.linearLayoutItem);
        }
    }


}
