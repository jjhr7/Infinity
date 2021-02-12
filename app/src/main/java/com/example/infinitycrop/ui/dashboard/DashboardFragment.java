package com.example.infinitycrop.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comun.Mqtt;
import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.lets_start.EditCommunity;
import com.example.infinitycrop.ui.Foro.lets_start.rv_community.CommunityModel;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.CreatePost.CreatePost;
import com.example.infinitycrop.ui.Foro.main.Home.RVs.RVFollowed.AdapterFollowedCmty;
import com.example.infinitycrop.ui.MachineControl.planta1;
import com.example.infinitycrop.ui.dashboard.RvClimas.AdapterClimas;
import com.example.infinitycrop.ui.dashboard.RvClimas.ClimaModel;
import com.example.infinitycrop.ui.recycler_control.StaticRvAdapter;
import com.example.infinitycrop.ui.recycler_control.StaticRvModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

import static com.example.comun.Mqtt.topicRoot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements MqttCallback {

    private RecyclerView recyclerView;
    //private StaticRvAdapter staticRvAdapter;
    private AdapterClimas adapterClimas;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //Weather

    final long MIN_TIME = 200;
    final float MIN_DISTANCE = 200;
    final int REQUEST_CODE = 101;
    final String APP_ID = "2599fe29f309cebdb21dd7dcf235662a";
    String Location_Provider = LocationManager.GPS_PROVIDER;
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    TextView NameofCity, weatherState, Temperature;
    ImageView mweatherIcon;
    private String idUser;
    LocationManager mLocationManager;
    LocationListener mLocationListner;
    private List<ClimaModel> climaModelList=new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    //Acceso a la maquina
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
    private String uidActualMachine;
    private TextView btn_createCima;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        //FIREBASE

        MainActivity myActivity = (MainActivity) getActivity();
        uidActualMachine=myActivity.getMachineUID();
        //Recojo los datos del usuario
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser usuario = mAuth.getCurrentUser();
        final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        idUser = mAuth.getUid();
        db=FirebaseFirestore.getInstance();
        btn_createCima=v.findViewById(R.id.btn_anyadir_clima);
        //rv
        recyclerView = v.findViewById(R.id.rv_1);
        initRvClimas();
        getMyClimas();

        btn_createCima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_create_clima);

                Button create_new=bottomSheetDialog.findViewById(R.id.createNewClima);
                Button create_plantilla=bottomSheetDialog.findViewById(R.id.createDefaultClima);
                Button backClimna=bottomSheetDialog.findViewById(R.id.btn_cancelarCreateClima);


                create_new.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CreateClima.class);
                        intent.putExtra("modo", "new");
                        startActivity(intent);
                        bottomSheetDialog.dismiss();
                    }
                });

                create_plantilla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CreateClima.class);
                        intent.putExtra("modo", "default");
                        startActivity(intent);
                        bottomSheetDialog.dismiss();
                    }
                });


                backClimna.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });



                bottomSheetDialog.show();
            }
        });


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

        //switch methods
 /*     Switch switchLuz = (Switch) v.findViewById(R.id.switchLuminosidadGeneral);
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
        });*/


        //


        //RECYCLER VIEW
        /*ArrayList<StaticRvModel> item = new ArrayList<>();
        item.add(new StaticRvModel(R.drawable.icons_sun, "Soleado"));
        item.add(new StaticRvModel(R.drawable.icons_night_mode, "Nocturno"));
        item.add(new StaticRvModel(R.drawable.icons_energy_saving, "Ahorro"));
        item.add(new StaticRvModel(R.drawable.icons_power_off, "Apagado"));
        item.add(new StaticRvModel(R.drawable.icons_custom, "Custom"));*/

        final TextView medidasT = v.findViewById(R.id.medidaTemperaturaGeneral);
        final TextView medidasH = v.findViewById(R.id.medidaHumedadGeneral);
        /*final TextView medidasS = v.findViewById(R.id.medidaSalinidadGeneral);*/
        /*final TextView medidasL = v.findViewById(R.id.medidasLuminosidadGeneral);*/




        //TABS
        tabLayout = v.findViewById(R.id.tab_layout);
        viewPager = v.findViewById(R.id.view_pager);
        //Array
        ArrayList<String> arrayList = new ArrayList<>();

        //set up with viewpager
        tabLayout.setupWithViewPager(viewPager);

        TabAdapter adapter = new TabAdapter(getActivity().getSupportFragmentManager());
        //Pestañas
        adapter.addFrag(new GeneralFragment(), "General");
        adapter.addFrag(new Planta1Fragment(), "Planta 1");
        adapter.addFrag(new Planta2Fragment(), "Planta 2");

        viewPager.setAdapter(adapter);

        /*((MainActivity)getActivity()).ola();*/
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
        //Weather App
        weatherState =(TextView) v.findViewById(R.id.weatherCondition);
        Temperature = (TextView)v.findViewById(R.id.temperature);
        mweatherIcon =(ImageView) v.findViewById(R.id.weatherIcon);
        NameofCity = (TextView)v.findViewById(R.id.cityName);

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());
                RequestParams params = new RequestParams();
                params.put("lat",Latitude);
                params.put("lon",Longitude);
                params.put("appid",APP_ID);
                letsDoSomeWorking(params);

            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Localizacion obtenida", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(getContext(), "Localizaacion no obtenida", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void letsDoSomeWorking(RequestParams params){
        AsyncHttpClient client =new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                /*Toast.makeText(getContext(), "Data obteninda", Toast.LENGTH_SHORT).show();*/
                weatherData weatherD= weatherData.fromJson(response);
                updateUI(weatherD);
                //super.onSuccess(statusCode, headers, response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "Data no obteninda", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(weatherData weather){
        Temperature.setText(weather.getmTemperature());
        NameofCity.setText(weather.getMcity());
        weatherState.setText(weather.getmWeatherType());
        int resourceID =getResources().getIdentifier(weather.getMicon(),"drawable",getActivity().getPackageName());
        mweatherIcon.setImageResource(resourceID);


    }
    @Override
    public void onPause() {
        super.onPause();
        if(mLocationManager!=null){
            mLocationManager.removeUpdates(mLocationListner);
        }
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




    private void setText(final TextView text, final String value) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }



    //App location
   /* private void getLocation() {

        //Check Permissions again

        /*if (ActivityCompat.checkSelfPermission(
                getContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
             if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
               /* loc_func(LocationNetwork);*/
              /*  api_key(String.valueOf(view_city.getText()));
                showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
            }

        }

    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(getContext());

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                getLocation();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void loc_func(Location location){
        try {
            Geocoder geocoder=new Geocoder(getContext());
            List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
            /*String address = addresses.get(0).getSubLocality();*/
           /* String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            //txt_paddress.setText(address);
            view_city.setText(cityName);
            view_country.setText(country);
            //txt_state.setText(stateName);*/

       /* } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error:"+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void inicioGPS(){


        //Check gps is enable or not

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write Function To enable gps

            OnGPS();
        }
        else
        {
            //GPS is already On then

            /*getLocation();*/
 /*       }
    }*/

    private void initRvClimas(){
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        //recyclerView.setAdapter(staticRvAdapter);
    }
    private void getMyClimas(){

        db.collection("Climas").document("Default").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name=document.getString("name");
                                String creator=document.getString("creator");
                                String temperatura=document.getString("temperatura");
                                String luz=document.getString("luminosidad");
                                String humedad=document.getString("humedad");
                                String machine=document.getString("machineId");
                                boolean defecto=document.getBoolean("defecto");
                                String uid=document.getId();
                                final ClimaModel DefaultClima=new ClimaModel(temperatura,humedad,luz,name,creator,uid,uidActualMachine,defecto);

                                db.collection("Climas")
                                        .whereEqualTo("creator",idUser)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value,
                                                                @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    return;
                                                }
                                                if(value!=null){
                                                    climaModelList.clear();
                                                    climaModelList.add(DefaultClima);
                                                    for (QueryDocumentSnapshot doc : value) {
                                                        String name=doc.getString("name");
                                                        String creator=doc.getString("creator");
                                                        String temperatura=doc.getString("temperatura");
                                                        String humedad=doc.getString("humedad");
                                                        String machine=doc.getString("machineId");
                                                        String luminosidad=doc.getString("luminosidad");
                                                        boolean defecto=doc.getBoolean("defecto");
                                                        String uid=doc.getId();

                                                        ClimaModel climaModel=new ClimaModel(temperatura,humedad,luminosidad,name,creator,uid,uidActualMachine,defecto);
                                                        climaModelList.add(climaModel);
                                                    }
                                                    adapterClimas= new AdapterClimas(climaModelList,getContext(),uidActualMachine);
                                                    onclickClima();
                                                    recyclerView.setAdapter(adapterClimas);
                                                }
                                            }
                                        });

                            }
                        } else {

                        }
                    }
                });
    }

    private void onclickClima(){
        adapterClimas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=recyclerView.getChildLayoutPosition(v);
                ClimaModel climaModel=climaModelList.get(position);
                bottomSheetClima(climaModel,position);
            }
        });
    }

    private String climaActivated;

    private void bottomSheetClima(final ClimaModel climaModel, final int position){
        final String uiDocument=climaModel.getUid();
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_climas);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        TextView name=bottomSheetDialog.findViewById(R.id.name_clima);
        TextView temp=bottomSheetDialog.findViewById(R.id.umbral_temperatura_clima);
        TextView luz=bottomSheetDialog.findViewById(R.id.umbral_luminosidad_clima);
        TextView hum=bottomSheetDialog.findViewById(R.id.umbral_humedad_clima);
        final Button activar=bottomSheetDialog.findViewById(R.id.btn_activatClima);
        Button editar=bottomSheetDialog.findViewById(R.id.btn_editar_clima);
        Button eliminar=bottomSheetDialog.findViewById(R.id.btn_eliminar_clima);
        Button cancelar=bottomSheetDialog.findViewById(R.id.btn_cancelar_clima);

        name.setText(climaModel.getName());
        temp.setText(climaModel.getTemperatura());
        hum.setText(climaModel.getHumedad());
        luz.setText(climaModel.getLuminosidad());
        //onClick


        final String[] idRealMachine = {""};
        db.collection("Machine")
                .whereEqualTo("description",uidActualMachine)
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
                                                    climaActivated= document.getString("climaID");
                                                    if(climaModel.getUid().equals(climaActivated)){
                                                        activar.setText("Desactivar clima");
                                                        activar.setBackgroundResource(R.drawable.button_unfollow);
                                                        activar.setTextColor(getResources().getColor(R.color.black));
                                                    }else{
                                                        activar.setText("Activar clima");
                                                        activar.setBackgroundResource(R.drawable.button_enter_machine);
                                                        activar.setTextColor(getResources().getColor(R.color.white));
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

        activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activar.getText().equals("Desactivar clima")){
                    if(climaModel.isDefecto()){
                        Toast.makeText(getContext(),"Tienes que tener activado al menos un clima", Toast.LENGTH_LONG).show();
                    }else{
                        db.collection("Machine").document(idRealMachine[0]).collection("Clima").document("Activado").update("climaID","Default");
                    }
                }else if(activar.getText().equals("Activar clima")){
                    db.collection("Machine").document(idRealMachine[0]).collection("Clima").document("Activado").update("climaID",climaModel.getUid());
                }
                getMyClimas();
                bottomSheetDialog.dismiss();

            }
        });

        if(climaModel.isDefecto()){
            editar.setVisibility(View.GONE);
            eliminar.setVisibility(View.GONE);
            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
        }else{
            editar.setVisibility(View.VISIBLE);
            eliminar.setVisibility(View.VISIBLE);
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent tiwh extra to edit clima
                    Intent intent = new Intent(getContext(), EditarClima.class);
                    intent.putExtra("id", climaModel.getUid());
                    startActivity(intent);
                    bottomSheetDialog.dismiss();
                }
            });
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setMessage("¿Seguro que quieres eliminar este clima?");
                    builder.setTitle("Eliminar clima");
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(climaModel.getUid().equals(climaActivated)){
                                db.collection("Machine").document(idRealMachine[0]).collection("Clima").document("Activado").update("climaID","Default");
                                db.collection("Climas").document(climaModel.getUid()).delete();
                            }else{
                                db.collection("Climas").document(climaModel.getUid()).delete();
                            }

                            bottomSheetDialog.dismiss();
                            dialog.cancel();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
        }
        bottomSheetDialog.show();
    }
}