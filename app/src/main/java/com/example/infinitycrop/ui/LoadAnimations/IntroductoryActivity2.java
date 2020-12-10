package com.example.infinitycrop.ui.LoadAnimations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment1;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment2;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment3;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment4;
import com.example.infinitycrop.ui.list_machine.MainActivityMachineList;

public class IntroductoryActivity2 extends AppCompatActivity {
    ImageView splashImg;
    TextView txth;
    LottieAnimationView lottieAnimationView1,lottieAnimationView2;
    private static final int NUM_PAGES = 4;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    Animation anim;
    public static int SPLASH_TIME_OUT= 5200;
    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory2);
        txth = findViewById(R.id.txtintro);
        splashImg = findViewById(R.id.img1);
        lottieAnimationView1 = findViewById(R.id.lottie2);
        lottieAnimationView2 = findViewById(R.id.lottie3);
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //anim animresourcefile
        anim= AnimationUtils.loadAnimation(this,R.anim.o_b_anim);
        viewPager.startAnimation(anim);
        splashImg.animate().translationY(-4000).setDuration(1000).setStartDelay(5000);
        txth.animate().translationY(4000).setDuration(1000).setStartDelay(5000);
        lottieAnimationView1.animate().translationX(-1600).setDuration(800).setStartDelay(4900);
        lottieAnimationView2.animate().translationX(1600).setDuration(800).setStartDelay(4900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSharedPref = getSharedPreferences("SharedPref",MODE_PRIVATE);
                boolean isFirstTime = mSharedPref.getBoolean("firstTime",true);
                if (isFirstTime){
                    //Ir al tutorial
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                }else{
                    Intent intent = new Intent(IntroductoryActivity2.this, MainActivityMachineList.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    OnBoardingFragment1 tab1 = new OnBoardingFragment1();
                    return tab1;
                case 1:
                    OnBoardingFragment2 tab2 = new OnBoardingFragment2();
                    return tab2;
                case 2:
                    OnBoardingFragment3 tab3 = new OnBoardingFragment3();
                    return tab3;
                case 3:
                    OnBoardingFragment4 tab4 = new OnBoardingFragment4();
                    return tab4;
            }
            return null;

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    @Override
    public void onStop() {
        finish();
        super.onStop();
    }
}