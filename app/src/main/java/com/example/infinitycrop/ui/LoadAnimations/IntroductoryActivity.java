package com.example.infinitycrop.ui.LoadAnimations;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.login.LogActivity;

import static java.security.AccessController.getContext;

public class IntroductoryActivity extends AppCompatActivity {
    ImageView logo,splashImg;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);
        logo = findViewById(R.id.log);
        splashImg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);

        splashImg.animate().translationY(-4000).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(4000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(1600).setDuration(800).setStartDelay(3900);

        if (this.equals(0)) {
            finish();
        }
        new CountDownTimer(4660, 1000) {

            public void onTick(long millisUntilFinished) {


            }


            public void onFinish() {
                Intent intent= new Intent (getApplicationContext(), LogActivity.class);
                startActivity(intent);

            }

        }.start();

    }
    @Override
    public void onStop() {
        finish();
        super.onStop();
    }

}