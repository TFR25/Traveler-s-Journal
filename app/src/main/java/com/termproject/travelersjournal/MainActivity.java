package com.termproject.travelersjournal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final Handler splash = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splash.postDelayed(() -> {
            Intent i = new Intent(MainActivity.this, SignIn.class);
            startActivity(i);
            finish();
        },1000);
    }
}