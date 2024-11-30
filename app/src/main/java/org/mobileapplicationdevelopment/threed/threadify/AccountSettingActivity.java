package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountSettingActivity extends AppCompatActivity {

    EditText fullname, username, email, phoneNumber;
    Button editSave, delete;
    Boolean onEdit = false;
    SharedPreferences pref;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize SharedPreferences and DatabaseHelper
        pref = new SharedPreferences(this);
        db = new DatabaseHelper(this);

        // Setting up the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        fullname = findViewById(R.id.fullnameFld);
        username = findViewById(R.id.usernameFld);
        email = findViewById(R.id.emailFld);
        phoneNumber = findViewById(R.id.phoneNumberFld);
        editSave = findViewById(R.id.editSaveBtn);
        delete = findViewById(R.id.deleteBtn);

        // Populate fields with SharedPreferences data
        fullname.setText(pref.getFullname());
        username.setText(pref.getUsername());
        email.setText(pref.getEmail());
        phoneNumber.setText(pref.getPhoneNumber());

        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!onEdit){
                    editInformation();
                } else {
                    saveInformation();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AccountSettingActivity.this)
                        .setTitle("Warning")
                        .setMessage("Are you sure you want to delete your account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteUserAccount(AccountSettingActivity.this);

                                pref.clearPreferences();

                                Intent intent = new Intent(AccountSettingActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

    }

    public void editInformation() {
        disableInput(onEdit);
        editSave.setText("Save");
        onEdit = true;
    }

    public void disableInput(boolean enable) {
        fullname.setEnabled(!enable);
        username.setEnabled(!enable);
        email.setEnabled(!enable);
        phoneNumber.setEnabled(!enable);
    }

    public boolean checkChanges(String fullname, String username, String email, String phone) {
        if (!pref.getFullname().equals(fullname) || !pref.getUsername().equals(username) || !pref.getEmail().equals(email) || !pref.getPhoneNumber().equals(phone)) {
            return true;
        }
        return false;
    }

    public void saveInformation() {
        String full = fullname.getText().toString().trim();
        String user = username.getText().toString().trim();
        String user_email = email.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();

        if (!checkChanges(full, user, user_email, phone)){
            disableInput(onEdit);
            editSave.setText("Edit");
            onEdit = false;
            return;
        }

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Enter your password");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Password")
                .setMessage("Please enter your password to proceed.")
                .setView(inputPassword)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                            editSave.setText("Edit");
                            onEdit = false;
                            Toast.makeText(getApplicationContext(), "Your Information has been updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled, nothing happens
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Navigate back to MainMenuActivity when back button is pressed
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}