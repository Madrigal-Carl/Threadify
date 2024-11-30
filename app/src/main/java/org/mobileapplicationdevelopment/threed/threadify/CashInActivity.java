package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executors;

public class CashInActivity extends AppCompatActivity implements View.OnClickListener {

    Button submit, add10, add20, add50, add100, add200, add500, add1000, add5000, add10000;
    EditText addInput;
    DatabaseHelper db;

    // Initializes the CashInActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EAEFF3")));
            getSupportActionBar().setTitle("Cash In");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Finding views and initializing the database helper
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
        db = new DatabaseHelper(this);

        // Set onClickListener for all buttons
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

    // Handles back button action from the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Updates the input field with the selected money amount
    private void changeInputText(int money) {
        double sum = 0.0;
        if (!addInput.getText().toString().isEmpty()) {
            sum = Double.parseDouble(addInput.getText().toString());
        }
        addInput.setText(String.format("%d", sum + money));
    }

    // Handles button clicks to update the input field or submit the amount
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.add10: changeInputText(10); break;
            case R.id.add20: changeInputText(20); break;
            case R.id.add50: changeInputText(50); break;
            case R.id.add100: changeInputText(100); break;
            case R.id.add200: changeInputText(200); break;
            case R.id.add500: changeInputText(500); break;
            case R.id.add1000: changeInputText(1000); break;
            case R.id.add5000: changeInputText(5000); break;
            case R.id.add10000: changeInputText(10000); break;
            case R.id.submit:
                if (!addInput.getText().toString().isEmpty()) {
                    cashIn(Double.parseDouble(addInput.getText().toString()));
                } else {
                    addInput.setError("Please enter an input");
                }
                break;
            default: break;
        }
    }

    // Handles cash-in operation and updates the database
    private void cashIn(double money) {
        if (money <= 0) return;

        // Confirmation
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to add PHP " + money + " to your balance?")
                .setPositiveButton("Proceed", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    AlertDialog progressDialog = new AlertDialog.Builder(CashInActivity.this)
                            .setMessage("Processing transaction, please wait...")
                            .setCancelable(false)
                            .show();

                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            db.addBalance(money);
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(CashInActivity.this)
                                        .setMessage("Cash-in successful! Added PHP " + money)
                                        .setPositiveButton("Proceed", (successDialog, successI) -> {
                                            Intent toMainMenu = new Intent(CashInActivity.this, MainMenuActivity.class);
                                            startActivity(toMainMenu);
                                            finish();
                                        })
                                        .setCancelable(false)
                                        .show();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                Toast.makeText(CashInActivity.this, "Cash-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(false)
                .show();
    }

    // Handles device back button navigation
    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
