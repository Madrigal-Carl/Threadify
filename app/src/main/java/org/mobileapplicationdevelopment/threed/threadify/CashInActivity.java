package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CashInActivity extends AppCompatActivity implements View.OnClickListener{

    Button submit, add10, add20, add50, add100, add200, add500, add1000, add5000, add10000;
    EditText addInput;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        // Set up the action bar (navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle("Cash In");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Finding views in the layout
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

        // Initialize database helper
        db = new DatabaseHelper(this);

        // Set onClickListener for all buttons to trigger action when clicked
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

    // Handle item selection from the options menu (back button in the action bar)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Helper method to update the input field with the selected money amount
    public void changeInputText(int money){
        int sum = 0;
        if (!addInput.getText().toString().isEmpty()) {
            sum = Integer.parseInt(addInput.getText().toString());
        } else {
            sum = 0;
        }
        addInput.setText("" + (sum + money));
    }

    // Handle button clicks to update the input field with predefined amounts
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Add respective amounts to the input field
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

            // Submit button to finalize the cash-in process
            case R.id.submit:
                if (!addInput.getText().toString().isEmpty()) {
                    cashIn(Integer.parseInt(addInput.getText().toString()));
                } else {
                    cashIn(0);
                }
                break;

            default:
                break;
        }
    }

    // Method to handle cash-in operation and update the database
    public void cashIn(int money) {
        if (money <= 0) {
            return;
        }

        // Update the user's balance in the database
        db.addBalance(money);

        // Display an alert dialog showing the success message and amount added
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage("Cash-in successful! Added PHP " + money)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent toLogin = new Intent(CashInActivity.this, MainMenuActivity.class);
                        startActivity(toLogin);
                        finish();
                    }
                }).show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Navigate back to the main menu activity when the back button is pressed
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }
}
