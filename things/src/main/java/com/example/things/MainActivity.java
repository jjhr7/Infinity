package com.example.things;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.comun.Mqtt;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Datos del sensor de humedad
        Map<String, Object> datosS1 = new HashMap<>();
        datosS1.put("Porcentaje", "%");
        datosS1.put("Estado", "Activado");

        //Datos del sensor de iluminación
        Map<String, Object> datosS2 = new HashMap<>();
        datosS2.put("Porcentaje", "%");
        datosS2.put("Estado", "Desactivado");
        /* datosS2.put("fecha", System.currentTimeMillis());*/

        //Datos del sensor de la ventilación
        Map<String, Object> datosS3 = new HashMap<>();
        datosS3.put("Estado", "Desactivado");

        //Datos del sensor de temperatura
        Map<String, Object> datosS4 = new HashMap<>();
        datosS4.put("Medición", "ºC");
        datosS4.put("Estado:", "Activado");


        db.collection("SensoresA-T").document("Humedad").set(datosS1);//colocamos la coleccion y luego el document para enviarse
        db.collection("SensoresA-T").document("Iluminación").set(datosS2);//colocamos la coleccion y luego el document para enviarse
        db.collection("SensoresA-T").document("Ventilación").set(datosS3);//colocamos la coleccion y luego el document para enviarse
        db.collection("SensoresA-T").document("Temperatura").set(datosS4);//colocamos la coleccion y luego el document para enviarse
        try {
            Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
            client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "Desconexión", "App desconectada".getBytes(), Mqtt.qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al conectar.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "hola");
            MqttMessage message = new MqttMessage("Sensores sincronizandose".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot + "InfinityCrop", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot + "infinity/prueba/");
            client.subscribe(topicRoot + "POWER", Mqtt.qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }


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
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");

    }
}