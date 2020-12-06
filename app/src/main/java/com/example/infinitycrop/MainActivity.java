package com.example.infinitycrop;
import android.os.Bundle;

import com.example.infinitycrop.ui.dashboard.DashboardFragment;
import com.example.infinitycrop.ui.graphic.GraphicFragment;
import com.example.infinitycrop.ui.map.MapFragment;
import com.example.infinitycrop.ui.profile.ProfileFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private ViewPager viewPager;
    private Fragment fragment=null;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeHome);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        chipNavigationBar=findViewById(R.id.chipNavigation);
        viewPager = findViewById(R.id.view_pager11);
        chipNavigationBar.setItemSelected(R.id.home,true);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final DashboardFragment fragment_1 = new DashboardFragment();
        final GraphicFragment fragment_2 = new GraphicFragment();
        final MapFragment fragment_3 = new MapFragment();
        final ProfileFragment fragment_4 = new ProfileFragment();
        adapter.addFragment(fragment_1);
        adapter.addFragment(fragment_2);
        adapter.addFragment(fragment_3);
        adapter.addFragment(fragment_4);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        index = 1;
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch(i){
                    case R.id.home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.graphics:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.notifications:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.profile:
                        viewPager.setCurrentItem(3);
                        break;
                }


            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0){

                    chipNavigationBar.setItemSelected(R.id.home, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.graphics:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.notifications:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 0;

                }else if (position == 1){

                    chipNavigationBar.setItemSelected(R.id.graphics, true);

                    if(index == 0 || index == 2){

                        //fragment_2.downloadPlates_2();
                    }
                    index = 1;
                    //Toast.makeText(Home.this, ""+index, Toast.LENGTH_SHORT).show();
                }else if (position == 2){

                    chipNavigationBar.setItemSelected(R.id.notifications, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.graphics:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.notifications:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 2;

                }else if (position == 3){

                    chipNavigationBar.setItemSelected(R.id.profile, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.graphics:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.notifications:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 3;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {



        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter (FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);

        }

    }


}