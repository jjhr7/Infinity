package com.example.infinitycrop;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.SplashTheme);
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
                    case R.id.cart:
                        fragment=new CartFragment();
                        break;
                    case R.id.profile:
                        fragment=new ProfileFragment();
                        break;
                }

                if(fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                }
            }
        });


    }


}