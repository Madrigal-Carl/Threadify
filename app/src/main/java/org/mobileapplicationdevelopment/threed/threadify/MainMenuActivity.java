package org.mobileapplicationdevelopment.threed.threadify;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    TextView current_balance;
    Button cash_in, cash_out, buy_load, pay_bills, transaction_history;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        pref = new SharedPreferences(this);

        //Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.actionlogo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("   Threadify");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        current_balance = findViewById(R.id.current_balanceView);
        cash_in = findViewById(R.id.cash_inBtn);
        cash_out = findViewById(R.id.cash_outBtn);
        buy_load = findViewById(R.id.buy_loadBtn);
        pay_bills = findViewById(R.id.pay_billsBtn);
        transaction_history = findViewById(R.id.transaction_historyBtn);

        current_balance.setText("PHP " + pref.getBalance());

        cash_in.setOnClickListener(this);
        cash_out.setOnClickListener(this);
        buy_load.setOnClickListener(this);
        pay_bills.setOnClickListener(this);
        transaction_history.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cash_inBtn:
                Intent toCashIn = new Intent(MainMenuActivity.this, CashInActivity.class);
                startActivity(toCashIn);
                finish();
                break;

            case R.id.cash_outBtn:
                Intent toCashOut = new Intent(MainMenuActivity.this, SendCashActivity.class);
                startActivity(toCashOut);
                finish();
                break;

            case R.id.buy_loadBtn:
                Toast.makeText(this, "This feature is under development", Toast.LENGTH_SHORT).show();
                break;

            case R.id.pay_billsBtn:
                Intent toPayBills = new Intent(MainMenuActivity.this, PayBillsActivity.class);
                startActivity(toPayBills);
                finish();
                break;

            case R.id.transaction_historyBtn:
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
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_id) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.about_id) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.developers_id) {
            Intent intent = new Intent(this, DevelopersActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.log_out_id) {
            pref.clearPreferences();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}