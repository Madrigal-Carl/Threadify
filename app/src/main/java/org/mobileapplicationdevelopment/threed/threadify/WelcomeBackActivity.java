package org.mobileapplicationdevelopment.threed.threadify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeBackActivity extends AppCompatActivity {

    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_back);

        startBtn = findViewById(R.id.startBtn);

        // Button to redirect the user to the main menu activity
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(WelcomeBackActivity.this, MainMenuActivity.class);
                startActivity(toRegister);
                finish();
            }
        });
    }
}
