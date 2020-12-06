package com.example.infinitycrop.ui.dashboard;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comun.Mqtt;
import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.recycler_control.StaticRvAdapter;
import com.example.infinitycrop.ui.recycler_control.StaticRvModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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

import java.util.ArrayList;

import static com.example.comun.Mqtt.topicRoot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements MqttCallback{

    private RecyclerView recyclerView;
    private StaticRvAdapter staticRvAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ColorStateList def;


    public static MqttClient client = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_dashboard, container, false);
        //FIREBASE
        //Recojo los datos del usuario
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser usuario = mAuth.getCurrentUser();
        final FirebaseFirestore fStore=FirebaseFirestore.getInstance();
        String idUser=usuario.getUid();
        //guardo el nombre en un textView
        /*final TextView nombre = (TextView) v.findViewById(R.id.hello_text);
        String res="Hola"+" "+usuario.getDisplayName();
        nombre.setText(res);
        DocumentReference documentReference=fStore.collection("Usuarios").document(idUser);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(usuario.getDisplayName() == null){
                    String res1="Hola"+" "+snapshot.getString("username");
                    nombre.setText(res1);
                }
            }
        });*/
/*
        //switch methods
        Switch switchLuz = (Switch) v.findViewById(R.id.switchLuminosidad);
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


 */

        //RECYCLER VIEW
        ArrayList<StaticRvModel> item=new ArrayList<>();
        item.add(new StaticRvModel(R.drawable.icons_sun,"Soleado"));
        item.add(new StaticRvModel(R.drawable.icons_night_mode,"Nocturno"));
        item.add(new StaticRvModel(R.drawable.icons_energy_saving,"Ahorro"));
        item.add(new StaticRvModel(R.drawable.icons_power_off,"Apagado"));
        item.add(new StaticRvModel(R.drawable.icons_custom,"Custom"));

        final TextView medidasT=v.findViewById(R.id.medidaTemperatura);
        final TextView medidasH=v.findViewById(R.id.medidaHumedad);
        final TextView medidasS=v.findViewById(R.id.medidaSalinidad);
        final TextView medidasL=v.findViewById(R.id.medidasLuminosidad);

        recyclerView=v.findViewById(R.id.rv_1);
        staticRvAdapter=new StaticRvAdapter(item,medidasT,medidasH,medidasS,medidasL);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(staticRvAdapter);


        //TABS
        tabLayout = v.findViewById(R.id.tab_layout);
        viewPager = v.findViewById(R.id.view_pager);
         //Array
        ArrayList<String> arrayList = new ArrayList<>();

        //set up with viewpager
        tabLayout.setupWithViewPager(viewPager);

        TabAdapter adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        //Pestañas
        adapter.addFrag(new GeneralFragment(),"General");
        adapter.addFrag(new Planta1Fragment(),"Planta 1");
        adapter.addFrag(new Planta2Fragment(),"Planta 2");

        viewPager.setAdapter(adapter );

     /*   //LEER DATOS FIREBASE
        //Temperatura
       DocumentReference documentTemperatura=fStore.collection("SensoresA-T").document("Temperatura");
        documentTemperatura.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                String resMediTemp=snapshot.getString("Medición")+"°C";
                medidasT.setText(resMediTemp);
            }
        });
        //Humedad
        DocumentReference documentHumedad=fStore.collection("SensoresA-T").document("Humedad");
        documentHumedad.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                String resMediHum=snapshot.getString("Porcentaje")+"%";
                medidasH.setText(resMediHum);
            }
        });
        //Iluminación
        DocumentReference documentIluminación=fStore.collection("SensoresA-T").document("Iluminación");
        documentIluminación.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                String resMediIlum=snapshot.getString("Porcentaje")+"%";
                medidasL.setText(resMediIlum);
            }
        });
        //Temperatura ambiente
        DocumentReference documentambiente=fStore.collection("SensoresA-T").document("Temperatura ambiente");
        documentambiente.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                String resMediIAmb=snapshot.getString("Medición")+"°C";
                medidasS.setText(resMediIAmb);
            }
        });

      */

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
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "luces");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "luces", Mqtt.qos);
            client.setCallback((MqttCallback) this);
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
            Log.i(Mqtt.TAG, "Subscrito a " + topicRoot + "luces");//aqui está el root al que nos subscribimos si se quiere modificar se tiene que modificar este
            client.subscribe(topicRoot + "luces", Mqtt.qos);
            client.setCallback((MqttCallback) this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        try {
            Log.i(Mqtt.TAG, "Publicando mensaje: " + "mensaje");
            MqttMessage message = new MqttMessage("luces ON".getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"luces", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
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
}