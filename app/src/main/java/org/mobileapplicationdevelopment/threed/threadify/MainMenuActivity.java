package org.mobileapplicationdevelopment.threed.threadify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView current_balance;
    Button cash_in, cash_out, buy_load, pay_bills, transaction_history;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize shared preferences
        pref = new SharedPreferences(this);

        // Set up action bar with logo and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.actionlogo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("   Threadify");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Finding views in the layout
        current_balance = findViewById(R.id.current_balanceView);
        cash_in = findViewById(R.id.cash_inBtn);
        cash_out = findViewById(R.id.cash_outBtn);
        buy_load = findViewById(R.id.buy_loadBtn);
        pay_bills = findViewById(R.id.pay_billsBtn);
        transaction_history = findViewById(R.id.transaction_historyBtn);

        // Set current balance from shared preferences
        current_balance.setText("PHP " + pref.getBalance());

        // Set click listeners for buttons
        cash_in.setOnClickListener(this);
        cash_out.setOnClickListener(this);
        buy_load.setOnClickListener(this);
        pay_bills.setOnClickListener(this);
        transaction_history.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Handle button clicks based on their IDs
        switch (view.getId()) {
            case R.id.cash_inBtn:
                // Navigate to the CashInActivity
                Intent toCashIn = new Intent(MainMenuActivity.this, CashInActivity.class);
                startActivity(toCashIn);
                finish();
                break;

            case R.id.cash_outBtn:
                // Navigate to the SendCashActivity
                Intent toCashOut = new Intent(MainMenuActivity.this, SendCashActivity.class);
                startActivity(toCashOut);
                finish();
                break;

            case R.id.buy_loadBtn:
                // Notify the user that this feature is under development
                Toast.makeText(this, "This feature is under development", Toast.LENGTH_SHORT).show();
                break;

            case R.id.pay_billsBtn:
                // Navigate to the PayBillsActivity
                Intent toPayBills = new Intent(MainMenuActivity.this, PayBillsActivity.class);
                startActivity(toPayBills);
                finish();
                break;

            case R.id.transaction_historyBtn:
                // Navigate to the TransactionHistoryActivity
                Intent toTransactionHistory = new Intent(MainMenuActivity.this, TransactionHistoryActivity.class);
                startActivity(toTransactionHistory);
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.profile_id) {
            // Navigate to ProfileActivity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.about_id) {
            // Navigate to AboutActivity
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.developers_id) {
            // Navigate to DevelopersActivity
            Intent intent = new Intent(this, DevelopersActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.log_out_id) {
            // Clear shared preferences and navigate to LoginActivity
            pref.clearPreferences();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to display an exit confirmation dialog when the back button is pressed
    protected void exitByBackKey() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the application?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Override back button behavior to show the exit confirmation dialog
        exitByBackKey();
    }
}
