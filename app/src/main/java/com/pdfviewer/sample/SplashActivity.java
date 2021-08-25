package com.pdfviewer.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;



public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goToHomeActivity();
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtras(getIntent());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    finishAffinity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }
}
