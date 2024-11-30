package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class TransactionHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView transactionImg;
    DatabaseHelper db;
    ArrayList<String> transaction_amount, transaction_type, transaction_date;
    CustomAdapter customAdapter;
    TextView noTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // Setting up the action bar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EAEFF3")));
            getSupportActionBar().setTitle("Transaction History");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        noTransactions = findViewById(R.id.noTransactions);
        transactionImg = findViewById(R.id.noTransactionImg);

        transaction_amount = new ArrayList<>();
        transaction_type = new ArrayList<>();
        transaction_date = new ArrayList<>();

        storeTransactionHistory();
        reverseListOrder();

        customAdapter = new CustomAdapter(TransactionHistoryActivity.this, this, transaction_amount, transaction_type, transaction_date);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Store all user transaction history to the array
    public void storeTransactionHistory() {
        // Fetch transaction history from the database
        Cursor cursor = db.getAllTransactionHistory();

        if (cursor.getCount() == 0) {
            // Show the "No transaction history" message
            noTransactions.setVisibility(View.VISIBLE);
            transactionImg.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Hide the message and show the RecyclerView
            noTransactions.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {
                // Add data to respective ArrayLists
                transaction_amount.add(cursor.getString(cursor.getColumnIndexOrThrow("transaction_amount")));
                transaction_type.add(cursor.getString(cursor.getColumnIndexOrThrow("transaction_type")));
                transaction_date.add(cursor.getString(cursor.getColumnIndexOrThrow("transaction_date")));
            }
        }
        cursor.close();
    }


    // Reverse the lists to show the latest transactions first
    private void reverseListOrder() {
        // Reverse all the lists to show the latest first
        Collections.reverse(transaction_amount);
        Collections.reverse(transaction_type);
        Collections.reverse(transaction_date);
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
}