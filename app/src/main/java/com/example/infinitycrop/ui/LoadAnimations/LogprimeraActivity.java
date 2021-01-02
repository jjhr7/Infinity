package com.example.infinitycrop.ui.LoadAnimations;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment1;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment2;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment3;
import com.example.infinitycrop.ui.OnBoardingFragment.OnBoardingFragment4;
import com.example.infinitycrop.ui.login.FragmentLog;

public class LogprimeraActivity extends AppCompatActivity {
    ImageView logo, splashimg;
    TextView eslogan;
    private final static int NUM_PAGES = 1;
    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory3);
        logo = findViewById(R.id.logiconintro);
        splashimg = findViewById(R.id.backgroundintro);
        eslogan = findViewById(R.id.textlogintro);
        FragmentLog fragmentLog = new FragmentLog();
        viewPager = findViewById(R.id.pager);

        FragmentManager fragmentmanager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentmanager.beginTransaction();
        fragmentTransaction.add(R.id.content,fragmentLog);
        fragmentTransaction.commit();

        splashimg.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
        eslogan.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
    }

    @Override
    public void onStop() {
        finish();
        super.onStop();
    }

}
