package org.mobileapplicationdevelopment.threed.threadify;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Custom adapter for RecyclerView to display transaction history
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<String> transaction_amount, transaction_type, transaction_date;

    // Constructor to initialize context, activity, and data lists
    public CustomAdapter(Activity activity, Context context, ArrayList<String> transaction_amount, ArrayList<String> transaction_type, ArrayList<String> transaction_date) {
        this.activity = activity;
        this.context = context;
        this.transaction_amount = transaction_amount;
        this.transaction_type = transaction_type;
        this.transaction_date = transaction_date;
    }

    // Inflates the layout for each item in the RecyclerView
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transaction_history_row, parent, false);
        return new MyViewHolder(view);
    }

    // Binds data to views in each item of the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.transaction_amount_value.setText(transaction_amount.get(position));
        holder.transaction_type_txt.setText(transaction_type.get(position));
        holder.transaction_date_txt.setText(transaction_date.get(position));
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return transaction_amount.size();
    }

    // ViewHolder class to cache views for each item in the RecyclerView
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView transaction_amount_php, transaction_amount_value, transaction_type_txt, transaction_date_txt;
        CardView cardView;

        // Initializes views from the layout
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_amount_php = itemView.findViewById(R.id.transaction_amount_php);
            transaction_amount_value = itemView.findViewById(R.id.transaction_amount_value);
            transaction_type_txt = itemView.findViewById(R.id.transaction_type_txt);
            transaction_date_txt = itemView.findViewById(R.id.transaction_date_txt);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
