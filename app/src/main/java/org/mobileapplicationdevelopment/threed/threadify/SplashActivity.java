package org.mobileapplicationdevelopment.threed.threadify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = new SharedPreferences(this);

        Thread mythread = new Thread() {
            public void run() {
                try {
                    sleep(3000);

                    Intent myIntent;
                    if (pref.isLoggedIn()) {
                        myIntent = new Intent(SplashActivity.this, MainMenuActivity.class);
                    } else {
                        myIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(myIntent);
                    finish();
                }catch (Exception e){

                }
            }
        };

        mythread.start();
    }
}