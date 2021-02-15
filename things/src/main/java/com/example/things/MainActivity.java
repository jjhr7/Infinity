package com.example.things;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.comun.Mqtt;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.ByteBuffer;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class MainActivity extends AppCompatActivity implements MqttCallback{
    public static MqttClient client = null;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String idUser;
    private String idMachine;
    //Càmara
    private DoorbellCamera mCamera;
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    private Handler temporizadorHandler = new Handler();
    private Bitmap bitmap;
    
    private ImageView btnStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();

        btnStatus=findViewById(R.id.iotImage);


        //Camara
        //Creamos handlers y les asociamos un hilo para la cámara
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());

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

        // Nos suscribimos al topic rfid
        try {
            Log.i(Mqtt.TAG, "Suscrito a "+topicRoot+"estado");
            client.subscribe(topicRoot+"estado", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }



        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //El Código para programar la cámara ha sido recogido en una clase
                mCamera = DoorbellCamera.getInstance();
                mCamera.initializeCamera(getApplicationContext(), mCameraHandler, mOnImageAvailableListener);
                temporizadorHandler.postDelayed(tomaFoto, 3 * 1000); //llamamos en 3 seg.
            }
        });

    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);
        idMachine=payload;

            //El Código para programar la cámara ha sido recogido en una clase
            mCamera = DoorbellCamera.getInstance();
            mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);
            temporizadorHandler.postDelayed(tomaFoto, 3 * 1000); //llamamos en 3 seg.

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }


    //CODI CAMARA *********************************************************************************


    //Mètodes per a la càmara
    private final Runnable tomaFoto = new Runnable() {
        @Override public void run() {
            mCamera.takePicture();

        }
    };
    private final ImageReader.OnImageAvailableListener
            mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();
                    onPictureTaken(imageBytes);
                }
            };

    private void onPictureTaken(final byte[] imageBytes) {
        if (imageBytes != null) {
            String nombreFichero = idMachine;
            subirBytes(imageBytes, "estado/"+nombreFichero);
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    //ImageView imageView = findViewById(R.id.iotImage);
                    //imageView.setImageBitmap(bitmap);
                }
            });
        }
    }
    private void subirBytes(final byte[] bytes, String referencia) {
        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();

        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putBytes(bytes);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) throw task.getException();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.e("Almacenamiento", "URL: " + downloadUri.toString());
                    registrarImagen(downloadUri.toString(), idMachine);
                } else {
                    Log.e("Almacenamiento", "ERROR: subiendo bytes");
                }
            }
        });
    }

    static void registrarImagen(String url,String idMachine) {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final Map<String, Object> a2 = new HashMap<>();
        a2.put("Url", url);
        a2.put("Fecha", new Timestamp(new Date()));
        db.collection("Machine").document(idMachine).collection("Estado").document("activo").set(a2);

    }


}