package com.example.infinitycrop.ui.login;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitycrop.MainActivity;
import com.example.infinitycrop.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener!=null){
            mAuth.removeAuthStateListener(listener);
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        mAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null)
                {
                    Toast.makeText(LoginActivity.this, "You're already login with uid:" + user.getUid(), Toast.LENGTH_SHORT).show();
                    // mAuth.signOut();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.login)
                            .build(),RC_SIGN_IN);
                }
            }
        };

    }

}