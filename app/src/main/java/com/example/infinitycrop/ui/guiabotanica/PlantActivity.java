package org.example.lvilmar1.recycleviewpersonalizado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlantActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    ImageView imageView;
    TextView nombre1;
    TextView descripcion;
    TextView humedad;
    TextView temperatura;
    TextView luminosidad;
    private String uid;
    String nombre, info, foto;

    private List<Plants> plants = new ArrayList<>();
    //Set para que no se repitan las id de las comunidades y que no se dupliquen
    private Set<String> plantsId = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        getIncomingIntent();

    }
    private void getIncomingIntent() {
        if(getIntent().hasExtra("nombre") && getIntent().hasExtra("info")){

            String foto = getIntent().getStringExtra("foto");
            String nombre = getIntent().getStringExtra("nombre");
            String descripcion=getIntent().getStringExtra("info");
            String humedad=getIntent().getStringExtra("humedad");
            String temperatura=getIntent().getStringExtra("temperatura");
            String luminosidad=getIntent().getStringExtra("luminosidad");

            TextView name = findViewById(R.id.nombreID);
            name.setText(nombre);

            TextView description = findViewById(R.id.descripcionID);
            description.setText(descripcion);

            TextView humidity = findViewById(R.id.humedadID);
            humidity.setText(humedad);


            TextView temperature = findViewById(R.id.temperaturaID);
            temperature.setText(temperatura);

            TextView luminosity = findViewById(R.id.luminosidadID);
            luminosity.setText(luminosidad);

            ImageView image = findViewById(R.id.imageView7);
            Glide.with(this)
                    .asBitmap()
                    .load(foto)
                    .into(image);


        }

    }

}
