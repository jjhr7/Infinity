package com.example.infinitycrop.ui.list_machine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.infinitycrop.R;

public class ModifyMachine extends AppCompatActivity {
    private String s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_machine);

        Bundle extras = getIntent().getExtras();
        s = extras.getString("description");
    }
}