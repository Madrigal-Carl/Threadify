package org.mobileapplicationdevelopment.threed.threadify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

        // Finding views in the layout
        register = findViewById(R.id.registerBtn);
        login = findViewById(R.id.loginBtn);
        username = findViewById(R.id.usernameLoginFld);
        password = findViewById(R.id.passwordLoginFld);

        // Initialize database helper
        db = new DatabaseHelper(this);

        // Register button to redirect the user to the register activity
        register.setOnClickListener(view -> {
            Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(toRegister);
            finish();
        });

        // Login button to trigger the login process
        login.setOnClickListener(view -> userAuthentication());
    }

    // Method to handle user authentication
    private void userAuthentication() {
        // Retrieve user input from the username and password fields
        String user_name = username.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Validate input fields and show error messages if they are empty
        if (user_name.isEmpty() && pass.isEmpty()) {
            username.setError("Input your username");
            password.setError("Input your password");
            return;
        } else if (user_name.isEmpty()) {
            username.setError("Input your username");
            return;
        } else if (pass.isEmpty()) {
            password.setError("Input your password");
            return;
        }

        // Check credentials using the database helper
        if (db.userAuth(user_name, pass)) {
            SharedPreferences pref = new SharedPreferences(this);

            Toast.makeText(this, String.format("Welcome %s", pref.getFullname()), Toast.LENGTH_SHORT).show();
            Intent toMain = new Intent(this, MainMenuActivity.class);
            startActivity(toMain);
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display an exit confirmation dialog when the back button is pressed
    protected void exitByBackKey() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        exitByBackKey();
    }
}
