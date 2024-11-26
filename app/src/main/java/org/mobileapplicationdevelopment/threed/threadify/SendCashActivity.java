package org.mobileapplicationdevelopment.threed.threadify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendCashActivity extends AppCompatActivity implements View.OnClickListener{

    Button submit, add10, add20, add50, add100, add200, add500, add1000, add5000, add10000;
    EditText addInput, receiver;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_cash);

        //Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle("Send Money");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

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

        db = new DatabaseHelper(this);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeInputText(int money){
        int sum = 0;
        if (!addInput.getText().toString().isEmpty()) {
            sum = Integer.parseInt(addInput.getText().toString());
        } else {
            sum = 0;
        }

        addInput.setText("" + (sum + money));
    }

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

    public void sendCash(int money) {
        SharedPreferences pref = new SharedPreferences(this);

        if (money <= 0) {
            Toast.makeText(this, "Enter a valid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (money > Double.parseDouble(pref.getBalance())) {
            Toast.makeText(this, "Insufficient balance.", Toast.LENGTH_SHORT).show();
            addInput.setText("");
            return;
        }

        String recipientUsername = receiver.getText().toString().trim();
        if (recipientUsername.isEmpty() || !db.checkUser(recipientUsername)) {
            Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.sendCashToUser(recipientUsername, money);
    }

}