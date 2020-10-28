package com.example.infinitycrop;
import android.os.Bundle;

import com.example.infinitycrop.ui.dashboard.DashboardFragment;
import com.example.infinitycrop.ui.graphic.GraphicFragment;
import com.example.infinitycrop.ui.notifications.NotificationFragment;
import com.example.infinitycrop.ui.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
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



    }

    



}