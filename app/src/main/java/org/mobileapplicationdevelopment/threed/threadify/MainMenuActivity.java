package org.mobileapplicationdevelopment.threed.threadify;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {
    TextView current_balance;
    Button cash_in, cash_out, buy_load, pay_bills, transaction_history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        //Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.actionlogo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("   Threadify");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        //end of action bar


        current_balance = findViewById(R.id.current_balanceView);
        cash_in = findViewById(R.id.cash_inBtn);
        cash_out = findViewById(R.id.cash_outBtn);
        buy_load = findViewById(R.id.buy_loadBtn);
        pay_bills = findViewById(R.id.pay_billsBtn);
        transaction_history = findViewById(R.id.transaction_historyBtn);

        cash_in.setOnClickListener(this);
        cash_out.setOnClickListener(this);
        buy_load.setOnClickListener(this);
        pay_bills.setOnClickListener(this);
        transaction_history.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
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
                Intent toCashOut = new Intent(MainMenuActivity.this, CashOutActivity.class);
                startActivity(toCashOut);
                finish();
                break;

            case R.id.buy_loadBtn:
                Intent toBuyLoad = new Intent(MainMenuActivity.this, BuyLoadActivity.class);
                startActivity(toBuyLoad);
                finish();
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
}