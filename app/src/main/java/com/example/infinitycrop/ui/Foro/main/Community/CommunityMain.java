package com.example.infinitycrop.ui.Foro.main.Community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.Foro.main.Community.InfoCommunity.InfoCommunityFragment;
import com.example.infinitycrop.ui.Foro.main.Community.NewPost.NewPostFragment;
import com.example.infinitycrop.ui.Foro.main.Community.Posts.PostsCommunityFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class CommunityMain extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    private ViewPager viewPager;
    private int index = 0;
    private FloatingActionButton btn_back;
    private String s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_main);

        Bundle extras = getIntent().getExtras();
        s = extras.getString("community");

        btn_back=findViewById(R.id.btn_back_to_foro);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chipNavigationBar=findViewById(R.id.botton_nav_community);
        viewPager = findViewById(R.id.view_pagerCommunity);
        chipNavigationBar.setItemSelected(R.id.community_home,true);

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final PostsCommunityFragment fragment_1 = new PostsCommunityFragment();
        final NewPostFragment fragment_2 = new NewPostFragment();
        final InfoCommunityFragment fragment_3 = new InfoCommunityFragment();
        adapter.addFragment(fragment_1);
        adapter.addFragment(fragment_2);
        adapter.addFragment(fragment_3);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        index = 1;
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch(i){
                    case R.id.community_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.community_addpost:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.community_info:
                        viewPager.setCurrentItem(2);
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

                    chipNavigationBar.setItemSelected(R.id.community_home, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.community_home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.community_addpost:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.community_info:
                                    viewPager.setCurrentItem(2);
                                    break;
                            }
                        }
                    });

                    index = 0;

                }else if (position == 1){

                    chipNavigationBar.setItemSelected(R.id.community_addpost, true);

                    if(index == 0 || index == 2){

                        //fragment_2.downloadPlates_2();
                    }
                    index = 1;
                    //Toast.makeText(Home.this, ""+index, Toast.LENGTH_SHORT).show();
                }else if (position == 2){

                    chipNavigationBar.setItemSelected(R.id.community_info, true);
                    chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.community_home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.community_addpost:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.community_info:
                                    viewPager.setCurrentItem(2);
                                    break;
                            }
                        }
                    });

                    index = 2;

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

    public String getCommunityUid(){
        return s;
    }
}