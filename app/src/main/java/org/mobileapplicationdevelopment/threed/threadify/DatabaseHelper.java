package org.mobileapplicationdevelopment.threed.threadify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.SQLException;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Threadify.db";
    private static final int DATABASE_VERSION = 1;

    // Define constants for the Users table
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Define constants for the Wallets table
    private static final String TABLE_WALLETS = "Wallets";
    private static final String COLUMN_WALLET_ID = "wallet_id";
    private static final String COLUMN_WALLET_USER_ID = "user_id";
    private static final String COLUMN_WALLET_BALANCE = "current_balance";

    // Define constants for the Transaction History table
    private static final String TABLE_TRANSACTION_HISTORY = "Transaction_History";
    private static final String COLUMN_TRANSACTION_ID = "transaction_history_id";
    private static final String COLUMN_TRANSACTION_USER_ID = "user_id";
    private static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    private static final String COLUMN_TRANSACTION_AMOUNT = "transaction_amount";
    private static final String COLUMN_TRANSACTION_DATE = "transaction_date";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Users table
        String userQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s VARCHAR(50) NOT NULL, " +
                        "%s VARCHAR(18) NOT NULL UNIQUE, " +
                        "%s VARCHAR(18) NOT NULL)",
                TABLE_USERS, COLUMN_USER_ID, COLUMN_FULLNAME, COLUMN_USERNAME, COLUMN_PASSWORD
        );
        db.execSQL(userQuery);

        // Create the Wallets table
        String walletQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s REAL DEFAULT 0.0, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                TABLE_WALLETS, COLUMN_WALLET_ID, COLUMN_WALLET_USER_ID, COLUMN_WALLET_BALANCE,
                COLUMN_WALLET_USER_ID, TABLE_USERS, COLUMN_USER_ID
        );
        db.execSQL(walletQuery);

        // Create the Transaction History table
        String transactionHistoryQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s TEXT CHECK(%s IN ('send money', 'receive money', 'buy load')) NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                TABLE_TRANSACTION_HISTORY, COLUMN_TRANSACTION_ID, COLUMN_TRANSACTION_USER_ID,
                COLUMN_TRANSACTION_TYPE, COLUMN_TRANSACTION_TYPE, COLUMN_TRANSACTION_AMOUNT,
                COLUMN_TRANSACTION_DATE, COLUMN_TRANSACTION_USER_ID, TABLE_USERS, COLUMN_USER_ID
        );
        db.execSQL(transactionHistoryQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WALLETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Add a new user and create their wallet
    public Boolean addUser(String fullname, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;"); // Enable foreign key constraints
        db.beginTransaction();

        try {
            // Insert user data into the Users table
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_FULLNAME, fullname);
            cv.put(COLUMN_USERNAME, username);
            cv.put(COLUMN_PASSWORD, password);
            long userResult = db.insert(TABLE_USERS, null, cv);

            if (userResult == -1) {
                throw new SQLException(String.format("%s username is already taken", username));
            }

            // Retrieve the user's ID to create their wallet
            Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                cursor.close();

                // Insert wallet data into the Wallets table
                ContentValues walletValues = new ContentValues();
                walletValues.put(COLUMN_WALLET_USER_ID, userId);
                walletValues.put(COLUMN_WALLET_BALANCE, 0.0);
                long walletResult = db.insert(TABLE_WALLETS, null, walletValues);

                if (walletResult == -1) {
                    throw new SQLException("Error creating wallet");
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
        return false;
    }

    // Authenticate a user's login credentials
    public boolean userAuth(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.user_id, u.fullname, u.username, w.current_balance " +
                "FROM Users u " +
                "INNER JOIN Wallets w ON u.user_id = w.user_id " +
                "WHERE u.username = ? AND u.password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            // Store user details in SharedPreferences upon successful login
            SharedPreferences pref = new SharedPreferences(context);
            pref.setLoginState(true);
            pref.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            pref.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            pref.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME)));
            pref.setBalance(cursor.getString(cursor.getColumnIndexOrThrow("current_balance")));

            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();
        return false;
    }

    // Add balance to the logged-in user's wallet
    public void addBalance(int money) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        db.beginTransaction();
        try {
            // Fetch current wallet balance
            String query = "SELECT " + COLUMN_WALLET_BALANCE + " FROM " + TABLE_WALLETS + " WHERE " + COLUMN_WALLET_USER_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                double currentBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WALLET_BALANCE));
                cursor.close();

                // Update wallet balance
                double newBalance = currentBalance + money;
                ContentValues values = new ContentValues();
                values.put(COLUMN_WALLET_BALANCE, newBalance);
                db.update(TABLE_WALLETS, values, COLUMN_WALLET_USER_ID + " = ?", new String[]{String.valueOf(userId)});

                // Update balance in SharedPreferences
                pref.setBalance(String.valueOf(newBalance));
                db.setTransactionSuccessful();
            } else {
                cursor.close();
                throw new SQLException("User wallet not found.");
            }
        } catch (SQLException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Check if a username exists
    public boolean checkUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.user_id FROM Users u WHERE u.username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean userExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return userExists;
    }

    // Transfer money to another user
    public void sendCashToUser(String username, int money) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        SharedPreferences pref = new SharedPreferences(context);
        int senderId = pref.getUserId();

        db.beginTransaction();
        try {
            // Fetch sender's balance and validate transaction
            String senderQuery = "SELECT " + COLUMN_WALLET_BALANCE + " FROM " + TABLE_WALLETS + " WHERE " + COLUMN_WALLET_USER_ID + " = ?";
            Cursor senderCursor = db.rawQuery(senderQuery, new String[]{String.valueOf(senderId)});
            if (!senderCursor.moveToFirst()) {
                senderCursor.close();
                throw new SQLException("Sender wallet not found.");
            }
            double senderBalance = senderCursor.getDouble(senderCursor.getColumnIndexOrThrow(COLUMN_WALLET_BALANCE));
            senderCursor.close();

            if (senderBalance < money) throw new SQLException("Insufficient funds.");

            // Fetch recipient's wallet and user ID
            String recipientQuery = "SELECT w." + COLUMN_WALLET_BALANCE + ", u." + COLUMN_USER_ID +
                    " FROM " + TABLE_USERS + " u " +
                    "INNER JOIN " + TABLE_WALLETS + " w ON u." + COLUMN_USER_ID + " = w." + COLUMN_WALLET_USER_ID +
                    " WHERE u." + COLUMN_USERNAME + " = ?";
            Cursor recipientCursor = db.rawQuery(recipientQuery, new String[]{username});
            if (!recipientCursor.moveToFirst()) {
                recipientCursor.close();
                throw new SQLException("Recipient not found.");
            }
            double recipientBalance = recipientCursor.getDouble(recipientCursor.getColumnIndexOrThrow(COLUMN_WALLET_BALANCE));
            int recipientId = recipientCursor.getInt(recipientCursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            recipientCursor.close();

            // Deduct money from sender
            ContentValues senderValues = new ContentValues();
            senderValues.put(COLUMN_WALLET_BALANCE, senderBalance - money);
            db.update(TABLE_WALLETS, senderValues, COLUMN_WALLET_USER_ID + " = ?", new String[]{String.valueOf(senderId)});

            // Add money to recipient
            ContentValues recipientValues = new ContentValues();
            recipientValues.put(COLUMN_WALLET_BALANCE, recipientBalance + money);
            db.update(TABLE_WALLETS, recipientValues, COLUMN_WALLET_USER_ID + " = ?", new String[]{String.valueOf(recipientId)});

            // Update SharedPreferences with the new sender balance
            pref.setBalance(String.valueOf(senderBalance - money));

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
