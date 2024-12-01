package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AccountSettingActivity extends AppCompatActivity {

    EditText fullname, username, email, phoneNumber;
    Boolean onEdit = false;
    SharedPreferences pref;
    DatabaseHelper db;

    // Initializes activity and pre-populates user information
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        pref = new SharedPreferences(this);
        db = new DatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_profile);
            getSupportActionBar().setTitle("Account");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#131a30")));
        }

        fullname = findViewById(R.id.fullnameFld);
        username = findViewById(R.id.usernameFld);
        email = findViewById(R.id.emailFld);
        phoneNumber = findViewById(R.id.phoneNumberFld);

        fullname.setText(pref.getFullname());
        username.setText(pref.getUsername());
        email.setText(pref.getEmail());
        phoneNumber.setText(pref.getPhoneNumber());

    }

    // Toggles input fields between enabled and disabled states
    private void disableInput(boolean enable) {
        fullname.setEnabled(!enable);
        username.setEnabled(!enable);
        email.setEnabled(!enable);
        phoneNumber.setEnabled(!enable);
    }

    // Enables user to edit their information
    private void editInformation() {
        disableInput(onEdit);
        onEdit = true;
    }

    // Checks if any user information has been modified
    private boolean checkChanges(String fullname, String username, String email, String phone) {
        return !pref.getFullname().equals(fullname) ||
                !pref.getUsername().equals(username) ||
                !pref.getEmail().equals(email) ||
                !pref.getPhoneNumber().equals(phone);
    }

    // Saves user information after validating changes and confirming the password
    private void saveInformation() {
        String full = fullname.getText().toString().trim();
        String user = username.getText().toString().trim();
        String user_email = email.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();

        if (!checkChanges(full, user, user_email, phone)){
            disableInput(onEdit);
            onEdit = false;
            return;
        }

        if (!user_email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Please enter a valid Gmail address.", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Enter your password");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Password")
                .setMessage("Please enter your password to proceed.")
                .setView(inputPassword)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Get the password input from the EditText
                    String enteredPassword = inputPassword.getText().toString().trim();
                    String correctPassword = pref.getPassword();

                    if (enteredPassword.equals(correctPassword)) {
                        if (db.checkOtherUser(username.getText().toString())){
                            Toast.makeText(AccountSettingActivity.this, "Username is already used", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!full.isEmpty()){
                            db.setFullName(full);
                        }

                        if (!user.isEmpty()){
                            db.setUsername(user);
                        }

                        if (!user_email.isEmpty()){
                            db.setEmail(user_email);
                        }

                        if (!phone.isEmpty()){
                            db.setPhoneNumber(phone);
                        }
                        disableInput(onEdit);
                        onEdit = false;
                        Toast.makeText(getApplicationContext(), "Your Information has been updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User canceled, nothing happens
                })
                .show();
    }

    // Loads menu with account settings options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_setting_bar, menu);
        return true;
    }

    // Handles actions for menu items like edit, save, or delete
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.editSaveBtn) {
            if (!onEdit){
                editInformation();
            } else {
                saveInformation();
            }
        } else if (id == R.id.deleteBtn) {
            new AlertDialog.Builder(AccountSettingActivity.this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to delete your account?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteUserAccount(AccountSettingActivity.this);

                        pref.clearPreferences();

                        Intent intent = new Intent(AccountSettingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    // Handles back button navigation to MainMenuActivity
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}