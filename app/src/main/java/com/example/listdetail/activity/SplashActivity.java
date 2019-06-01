package com.example.listdetail.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.listdetail.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_splash);
        Intent intent= new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
