package com.example.things;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.comun.Mqtt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.comun.Mqtt.qos;
import static com.example.comun.Mqtt.topicRoot;


/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 * <p>
 * PeripheralManager manager = PeripheralManager.getInstance();
 * try {
 * Gpio gpio = manager.openGpio("BCM6");
 * gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * gpio.setValue(true);
 * } catch (IOException e) {
 * Log.e(TAG, "Unable to access GPIO");
 * }
 * <p>
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
public class MainActivity extends AppCompatActivity implements MqttCallback {
    public static MqttClient client = null;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

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
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "lecturaDatos");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "lecturaDatos", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //Root2 = Canal de senyal de luces

        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "luces");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "luces", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        //--------------------------------------------------------------------------------------------------------------------------------------------
        //Root 3= Canal de senyal de  ventiladores

        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "ventiladores");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "ventiladores", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        //--------------------------------------------------------------------------------------------------------------------------------------
        //Root 4= Canal de senyal riego

        try {
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "riego");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "riego", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }




        //instancia con el Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //esto de bajo una vez acabemos con las comunicaciones de los mensajes se podrá borrar

        //Datos del sensor de humedad
        Map<String, Object> datosS1 = new HashMap<>();
        datosS1.put("Porcentaje", "%");
        datosS1.put("Estado", "Activado");

        //Datos del sensor de iluminación
        Map<String, Object> datosS2 = new HashMap<>();
        datosS2.put("Porcentaje", "%");
        datosS2.put("Estado", "Desactivado");
        /* datosS2.put("fecha", System.currentTimeMillis());*/

        //Datos del sensor de temperatura en ambiente
        Map<String, Object> datosS3 = new HashMap<>();
        datosS3.put("Medición", "ºC");
        datosS3.put("Estado", "Desactivado");

        //Datos del sensor de temperatura
        Map<String, Object> datosS4 = new HashMap<>();
        datosS4.put("Medición", "ºC");
        datosS4.put("Estado:", "Activado");


        db.collection("SensoresA-T").document("Humedad").set(datosS1);//colocamos la coleccion y luego el document para enviarse con los datos
        db.collection("SensoresA-T").document("Iluminación").set(datosS2);//colocamos la coleccion y luego el document para enviarse con los datos
        db.collection("SensoresA-T").document("Temperatura ambiente").set(datosS3);//colocamos la coleccion y luego el document para enviarse con los datos
        db.collection("SensoresA-T").document("Temperatura").set(datosS4);//colocamos la coleccion y luego el document para enviarse con los datos

    }
    // Se ejecuta cuando se pierde la conexión
    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");

    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);

        if(topic.equals(topicRoot+"lecturaDatos")){
            topicLectura(payload);
        }

        if(topic.equals(topicRoot+"luces")){
            topicLuces(payload);
        }
        if(topic.equals(topicRoot+"riego")){
            topicRiego(payload);
        }
        if(topic.equals(topicRoot+"ventiladores")){
            topicVentiladores(payload);
        }

    }
    private void topicLectura(final String payload) {
        // parts0=humedad, parts0=humedad,parts1=iluminosidad,parts2=humedadHambiente,parts3=temperatura
        String[] parts = payload.split("-");//comprueba si los datos introducidos van referentes a cada sensor: nombre,medición, estado
        db.collection("SensoresA-T").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){// si se han introducido bien los datos exigidos

                    db.collection("SensoresA-T")
                            .document("Humedad")
                            .update("Porcentaje", parts[0]);//verificar los estados y actualizarlos

                    db.collection("SensoresA-T")
                            .document("Iluminación")
                            .update("Porcentaje", parts[1]);

                    db.collection("SensoresA-T")
                            .document("Temperatura ambiente")
                            .update("Medición", parts[2]);

                    db.collection("SensoresA-T")
                            .document("Temperatura")
                            .update("Medición", parts[3]);


                }

            }

        });
    }

    private void topicLuces(final String payload) {
    }

    private void topicRiego(final String payload) {
    }

    private void topicVentiladores(final String payload) {
    }







    @Override
    public void onDestroy() {
        try {
            Log.i(Mqtt.TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al desconectar.", e);
        }
        super.onDestroy();
    }





    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");

    }
}