package org.mobileapplicationdevelopment.threed.threadify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize SharedPreferences to check user login status
        SharedPreferences pref = new SharedPreferences(this);

        // Create a thread to simulate a splash screen delay
        Thread mythread = new Thread(() -> {
            try {
                Thread.sleep(3000);

                // Navigate to the appropriate activity based on login status
                Intent myIntent;
                if (pref.isLoggedIn()) {
                    myIntent = new Intent(SplashActivity.this, WelcomeBackActivity.class);
                } else {
                    myIntent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(myIntent); // Start the activity
                finish();
            } catch (Exception ignored) {
            }
        });

        // Start the thread
        mythread.start();
    }
}