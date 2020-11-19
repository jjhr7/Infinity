package com.example.infinitycrop.ui.profile.settings;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.R;
import com.example.infinitycrop.ui.profile.ProfileFragment;

public class AboutInfinityCrap extends AppCompatActivity {
    MediaPlayer mp;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_infinity_crap_profile);
        //volver a la anterior actividad
        ImageView backToProfile =findViewById(R.id.backToProfile);
        backToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                finish();
            }
        });
    }
    public void verPgWeb(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.androidcurso.com/"));
        startActivity(intent);
    }
    public void mandarCorreo(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "asunto");
        intent.putExtra(Intent.EXTRA_TEXT, "texto del correo");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"jtomas@upv.es"});
        startActivity(intent);
    }
    public void play(View view) {
        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();
    }
    @Override protected void onStop() {
        super.onStop();
    }



}
