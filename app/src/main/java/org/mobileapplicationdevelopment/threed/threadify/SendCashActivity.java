package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendCashActivity extends AppCompatActivity implements View.OnClickListener {

    Button submit, add10, add20, add50, add100, add200, add500, add1000, add5000, add10000;
    EditText addInput, receiver;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_cash);

        // Set up the action bar with a back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle("Send Money");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Finding views in the layout
        receiver = findViewById(R.id.receiver);
        addInput = findViewById(R.id.addInput);
        submit = findViewById(R.id.submit);
        add10 = findViewById(R.id.add10);
        add20 = findViewById(R.id.add20);
        add50 = findViewById(R.id.add50);
        add100 = findViewById(R.id.add100);
        add200 = findViewById(R.id.add200);
        add500 = findViewById(R.id.add500);
        add1000 = findViewById(R.id.add1000);
        add5000 = findViewById(R.id.add5000);
        add10000 = findViewById(R.id.add10000);

        // Initialize the database helper
        db = new DatabaseHelper(this);

        // Set click listeners for buttons
        submit.setOnClickListener(this);
        add10.setOnClickListener(this);
        add20.setOnClickListener(this);
        add50.setOnClickListener(this);
        add100.setOnClickListener(this);
        add200.setOnClickListener(this);
        add500.setOnClickListener(this);
        add1000.setOnClickListener(this);
        add5000.setOnClickListener(this);
        add10000.setOnClickListener(this);
    }

    // Handle the action bar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Update the input amount when a button is clicked
    public void changeInputText(int money) {
        int sum = 0;
        if (!addInput.getText().toString().isEmpty()) {
            sum = Integer.parseInt(addInput.getText().toString());
        }
        addInput.setText("" + (sum + money));
    }

    // Handle button clicks for adding amounts or submitting the form
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add10:
                changeInputText(10);
                break;
            case R.id.add20:
                changeInputText(20);
                break;
            case R.id.add50:
                changeInputText(50);
                break;
            case R.id.add100:
                changeInputText(100);
                break;
            case R.id.add200:
                changeInputText(200);
                break;
            case R.id.add500:
                changeInputText(500);
                break;
            case R.id.add1000:
                changeInputText(1000);
                break;
            case R.id.add5000:
                changeInputText(5000);
                break;
            case R.id.add10000:
                changeInputText(10000);
                break;
            case R.id.submit:
                // Validate and send the cash
                if (!addInput.getText().toString().isEmpty()) {
                    sendCash(Integer.parseInt(addInput.getText().toString()));
                } else {
                    sendCash(0);
                }
                break;
            default:
                break;
        }
    }

    // Main method to handle sending cash
    public void sendCash(int money) {
        SharedPreferences pref = new SharedPreferences(this);

        // Check if the input amount is valid
        if (money <= 0) {
            Toast.makeText(this, "Enter a valid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user has sufficient balance
        if (money > Double.parseDouble(pref.getBalance())) {
            Toast.makeText(this, "Insufficient balance.", Toast.LENGTH_SHORT).show();
            addInput.setText("");
            return;
        }

        // Validate the recipient username
        String recipientUsername = receiver.getText().toString().trim();
        if (recipientUsername.isEmpty() || !db.checkUser(recipientUsername)) {
            Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a confirmation dialog
        AlertDialog confirmDialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to transfer " + money + " to " + recipientUsername + "?")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        AlertDialog progressDialog = new AlertDialog.Builder(SendCashActivity.this)
                                .setMessage("Processing transaction, please wait...")
                                .setCancelable(false) // Prevent user dismissal
                                .show();

                        // Delay for 3 seconds and then show the success dialog
                        new android.os.Handler().postDelayed(() -> {
                            progressDialog.dismiss();

                            // Show success message
                            AlertDialog successDialog = new AlertDialog.Builder(SendCashActivity.this)
                                    .setMessage("Successfully transferred money!")
                                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Navigate to the main menu
                                            Intent toMainMenu = new Intent(SendCashActivity.this, MainMenuActivity.class);
                                            startActivity(toMainMenu);
                                            finish();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }, 3000);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // Handle the back button press to navigate to the main menu
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
