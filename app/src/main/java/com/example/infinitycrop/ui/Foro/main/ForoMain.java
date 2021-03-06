package com.example.infinitycrop.ui.Foro.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.main.Home.HomeForoFragment;
import com.example.infinitycrop.ui.Foro.main.Profile.ProfileForoFragment;
import com.example.infinitycrop.ui.Foro.main.Search.SearchForoFragment;
import com.example.infinitycrop.ui.Foro.main.Subscription.PanelForoFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class ForoMain extends AppCompatActivity {

    ChipNavigationBar chipNavigationBar;
    private ViewPager viewPager;
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foro_main);

        chipNavigationBar=findViewById(R.id.botton_nav_foro);
        viewPager = findViewById(R.id.view_pagerForo);
        chipNavigationBar.setItemSelected(R.id.foro_home,true);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final HomeForoFragment fragment_1 = new HomeForoFragment();
        final SearchForoFragment fragment_2 = new SearchForoFragment();
        final PanelForoFragment fragment_3 = new PanelForoFragment();
        final ProfileForoFragment fragment_4 = new ProfileForoFragment();
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
                    case R.id.foro_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.foro_search:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.foro_panel:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.foro_profile:
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

                    chipNavigationBar.setItemSelected(R.id.foro_home, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.foro_home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.foro_search:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.foro_panel:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.foro_profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 0;

                }else if (position == 1){

                    chipNavigationBar.setItemSelected(R.id.foro_search, true);

                    if(index == 0 || index == 2){

                        //fragment_2.downloadPlates_2();
                    }
                    index = 1;
                    //Toast.makeText(Home.this, ""+index, Toast.LENGTH_SHORT).show();
                }else if (position == 2){

                    chipNavigationBar.setItemSelected(R.id.foro_panel, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.foro_home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.foro_search:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.foro_panel:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.foro_profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 2;

                }else if (position == 3){

                    chipNavigationBar.setItemSelected(R.id.foro_profile, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.foro_home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.foro_search:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.foro_panel:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.foro_profile:
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