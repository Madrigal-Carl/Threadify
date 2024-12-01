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

public class RegisterActivity extends AppCompatActivity {

    TextView login;
    EditText fullname, username, password, confirm_password;
    Button register;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Finding views in the layout
        login = findViewById(R.id.loginBtn);
        fullname = findViewById(R.id.fullnameRegisterFld);
        username = findViewById(R.id.usernameRegisterFld);
        password = findViewById(R.id.passwordRegisterFld);
        confirm_password = findViewById(R.id.confirmpasswordRegisterFld);
        register = findViewById(R.id.registerBtn);

        // Initialize database helper
        db = new DatabaseHelper(this);

        // Login button to redirect the user to the login activity
        login.setOnClickListener(view -> {
            Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(toLogin);
            finish();
        });

        // Register button to trigger the registration process
        register.setOnClickListener(view -> registerUser());
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Registering new user
    private void registerUser() {
        String full_name = fullname.getText().toString().trim();
        String user_name = username.getText().toString().trim();
        String user_password = password.getText().toString().trim();
        String confirm_pass = confirm_password.getText().toString().trim();

        // Check for empty fields
        if (full_name.isEmpty() && user_name.isEmpty() && user_password.isEmpty()) {
            fullname.setError("Input your full name");
            username.setError("Input your username");
            password.setError("Input your password");
            return;
        } else if (full_name.isEmpty() && user_name.isEmpty()) {
            fullname.setError("Input your full name");
            username.setError("Input your username");
            return;
        } else if (full_name.isEmpty() && user_password.isEmpty()) {
            fullname.setError("Input your full name");
            password.setError("Input your password");
            return;
        } else if (user_name.isEmpty() && user_password.isEmpty()) {
            username.setError("Input your username");
            password.setError("Input your password");
            return;
        } else if (full_name.isEmpty()) {
            fullname.setError("Input your full name");
            return;
        } else if (user_name.isEmpty()) {
            username.setError("Input your username");
            return;
        } else if (user_password.isEmpty()) {
            password.setError("Input your password");
            return;
        }

        // Validate username and password length and spaces
        if (user_name.length() < 8) {
            username.setError("Username must be at least 8 characters");
            return;
        } else if (user_name.contains(" ")) {
            username.setError("Username must not contain spaces");
            return;
        }

        if (user_password.length() < 8) {
            password.setError("Password must be at least 8 characters");
            return;
        } else if (user_password.contains(" ")) {
            password.setError("Password must not contain spaces");
            return;
        } else if (!user_password.matches("[a-zA-Z0-9]*")) {
            password.setError("Password must not contain special characters");
            return;
        }

        // Check if passwords match
        if (!user_password.equals(confirm_pass)) {
            Toast.makeText(this, "Passwords do not match. Please re-enter.", Toast.LENGTH_SHORT).show();
            password.setText("");
            confirm_password.setText("");
            return;
        }

        // Add user to database
        if (db.addUser(full_name, user_name, user_password)) {
            new AlertDialog.Builder(this)
                    .setTitle("Account Created")
                    .setMessage("Your account is now ready.")
                    .setPositiveButton("Proceed", (dialogInterface, i) -> {
                        Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                    }).show();
        }
    }

}