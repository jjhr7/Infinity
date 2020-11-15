package com.example.infinitycrop;
import android.net.Uri;
import android.os.Bundle;

import com.example.infinitycrop.ui.dashboard.DashboardFragment;
import com.example.infinitycrop.ui.graphic.GraphicFragment;
import com.example.infinitycrop.ui.notifications.NotificationFragment;
import com.example.infinitycrop.ui.profile.ProfileFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeHome);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        chipNavigationBar=findViewById(R.id.chipNavigation);
        chipNavigationBar=findViewById(R.id.chipNavigation);

        chipNavigationBar.setItemSelected(R.id.home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new DashboardFragment()).commit();

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch(i){
                    case R.id.home:
                        fragment=new DashboardFragment();
                        break;
                    case R.id.graphics:
                        fragment=new GraphicFragment();
                        break;
                    case R.id.profile:
                        fragment=new ProfileFragment();
                        break;
                    case R.id.notifications:
                        fragment=new NotificationFragment();
                        break;
                }

                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                }
            }
        });

        //Firestore:

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> datos = new HashMap<>();
        datos.put("Nombre:", "pruebas");
        datos.put("numero", 1);
        datos.put("fecha", new Date());
        db.collection("coleccion").document("documento").set(datos);

    }

    



}