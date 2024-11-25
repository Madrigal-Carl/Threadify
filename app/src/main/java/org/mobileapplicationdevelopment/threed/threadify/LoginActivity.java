package org.mobileapplicationdevelopment.threed.threadify;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    EditText username, password;
    Button login;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.registerBtn);
        login = findViewById(R.id.loginBtn);
        username = findViewById(R.id.usernameLoginFld);
        password = findViewById(R.id.passwordLoginFld);
        db = new DatabaseHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(toRegister);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAuthentication();
            }
        });
    }

    public void userAuthentication() {
        String user_name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user_name.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill up", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.userAuth(user_name, pass)) {
            Toast.makeText(this, String.format("Welcome ",user_name), Toast.LENGTH_SHORT).show();
            Intent toMain = new Intent(this, MainMenuActivity.class);
            startActivity(toMain);
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }

    }

    protected void exitByBackKey() {
        AlertDialog alert = new AlertDialog.Builder(this).setMessage("Do you want to exit the application").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        exitByBackKey();
    }
}