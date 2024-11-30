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
    private final Context context;
    private static final String DATABASE_NAME = "Threadify.db";
    private static final int DATABASE_VERSION = 1;

    // Define constants for the Users table
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONENUMBER = "phone_number";
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

    // Constructor
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
                        "%s VARCHAR(30) NOT NULL UNIQUE, " +
                        "%s VARCHAR(30) NOT NULL, " +
                        "%s VARCHAR(30) UNIQUE, " +
                        "%s INTEGER UNIQUE, " +
                        "%s VARCHAR(18) NOT NULL) ",
                TABLE_USERS, COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_FULLNAME, COLUMN_EMAIL, COLUMN_PHONENUMBER, COLUMN_PASSWORD
        );
        db.execSQL(userQuery);

        // Insert sample user data
        String insertUserQuery = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s) VALUES " +
                        "('john_doe', 'John Doe', 'john.doe@example.com', '1234567890', 'password123'), " +
                        "('jane_doe', 'Jane Doe', 'jane.doe@example.com', '2345678901', 'password123'), " +
                        "('michael_smith', 'Michael Smith', 'michael.smith@example.com', '3456789012', 'password123'), " +
                        "('emily_johnson', 'Emily Johnson', 'emily.johnson@example.com', '4567890123', 'password123'), " +
                        "('chris_williams', 'Chris Williams', 'chris.williams@example.com', '5678901234', 'password123'), " +
                        "('sophia_brown', 'Sophia Brown', 'sophia.brown@example.com', '6789012345', 'password123'), " +
                        "('david_taylor', 'David Taylor', 'david.taylor@example.com', '7890123456', 'password123'), " +
                        "('olivia_moore', 'Olivia Moore', 'olivia.moore@example.com', '8901234567', 'password123'), " +
                        "('james_clark', 'James Clark', 'james.clark@example.com', '9012345678', 'password123'), " +
                        "('isabella_lewis', 'Isabella Lewis', 'isabella.lewis@example.com', '0123456789', 'password123')",
                TABLE_USERS, COLUMN_USERNAME, COLUMN_FULLNAME, COLUMN_EMAIL, COLUMN_PHONENUMBER, COLUMN_PASSWORD
        );
        db.execSQL(insertUserQuery);

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
                        "%s TEXT CHECK(%s IN ('Send Money', 'Receive Money', 'buy load')) NOT NULL, " +
                        "%s REAL NOT NULL, " +
                        "%s DATE DEFAULT (CURRENT_DATE), " +
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
        db.execSQL("PRAGMA foreign_keys=ON;");
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
            String query = String.format(
                    "SELECT %s FROM %s WHERE %s = ?",
                    COLUMN_USER_ID, TABLE_USERS, COLUMN_USERNAME
            );
            Cursor cursor = db.rawQuery(query, new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                cursor.close();

                // Insert wallet data into the Wallets table
                ContentValues walletValues = new ContentValues();
                walletValues.put(COLUMN_WALLET_USER_ID, userId);
                walletValues.put(COLUMN_WALLET_BALANCE, 0.0);
                db.insert(TABLE_WALLETS, null, walletValues);
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
        String query = String.format(
                "SELECT u.%s, u.%s, u.%s, u.%s, w.%s " +
                        "FROM %s u INNER JOIN %s w ON u.%s = w.%s " +
                        "WHERE u.%s = ? AND u.%s = ?",
                COLUMN_USER_ID, COLUMN_PASSWORD, COLUMN_FULLNAME, COLUMN_USERNAME, COLUMN_WALLET_BALANCE,
                TABLE_USERS, TABLE_WALLETS, COLUMN_USER_ID, COLUMN_WALLET_USER_ID,
                COLUMN_USERNAME, COLUMN_PASSWORD
        );
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            // Store user details in SharedPreferences upon successful login
            SharedPreferences pref = new SharedPreferences(context);
            pref.setLoginState(true);
            pref.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            pref.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            pref.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME)));
            pref.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
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
    public void addBalance(Double money) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        db.beginTransaction();
        try {
            // Fetch current wallet balance
            String query = String.format(
                    "SELECT %s FROM %s WHERE %s = ?",
                    COLUMN_WALLET_BALANCE, TABLE_WALLETS, COLUMN_WALLET_USER_ID
            );
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                double currentBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WALLET_BALANCE));
                cursor.close();

                // Update wallet balance
                double newBalance = currentBalance + money;
                ContentValues walletValues = new ContentValues();
                walletValues.put(COLUMN_WALLET_BALANCE, newBalance);
                db.update(TABLE_WALLETS, walletValues, COLUMN_WALLET_USER_ID + " = ?", new String[]{String.valueOf(userId)});

                // Insert transaction into the Transaction History table
                ContentValues transactionValues = new ContentValues();
                transactionValues.put(COLUMN_TRANSACTION_USER_ID, userId);
                transactionValues.put(COLUMN_TRANSACTION_TYPE, "Receive Money");
                transactionValues.put(COLUMN_TRANSACTION_AMOUNT, money);
                db.insert(TABLE_TRANSACTION_HISTORY, null, transactionValues);

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
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                COLUMN_USER_ID, TABLE_USERS, COLUMN_USERNAME
        );
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean userExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return userExists;
    }


    // Check if a username exists excluding the current user
    public boolean checkOtherUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        SharedPreferences pref = new SharedPreferences(context);
        int senderId = pref.getUserId();

        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s != ?",
                COLUMN_USER_ID, TABLE_USERS, COLUMN_USERNAME, COLUMN_USER_ID
        );
        Cursor cursor = db.rawQuery(query, new String[]{username, String.valueOf(senderId)});

        boolean userExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return userExists;
    }


    // Transfer money to another user
    public void sendCashToUser(String username, double money) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        SharedPreferences pref = new SharedPreferences(context);
        int senderId = pref.getUserId();

        db.beginTransaction();
        try {
            // Fetch sender's balance and validate transaction
            String senderQuery = String.format(
                    "SELECT %s FROM %s WHERE %s = ?",
                    COLUMN_WALLET_BALANCE, TABLE_WALLETS, COLUMN_WALLET_USER_ID
            );
            Cursor senderCursor = db.rawQuery(senderQuery, new String[]{String.valueOf(senderId)});

            if (!senderCursor.moveToFirst()) {
                senderCursor.close();
                throw new SQLException("Sender wallet not found.");
            }
            double senderBalance = senderCursor.getDouble(senderCursor.getColumnIndexOrThrow(COLUMN_WALLET_BALANCE));
            senderCursor.close();

            if (senderBalance < money) throw new SQLException("Insufficient funds.");

            // Fetch recipient's wallet and user ID
            String recipientQuery = String.format(
                    "SELECT w.%s, u.%s FROM %s u " +
                            "INNER JOIN %s w ON u.%s = w.%s WHERE u.%s = ?",
                    COLUMN_WALLET_BALANCE, COLUMN_USER_ID, TABLE_USERS,
                    TABLE_WALLETS, COLUMN_USER_ID, COLUMN_WALLET_USER_ID, COLUMN_USERNAME
            );
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

            // Insert sender's transaction history
            ContentValues senderTransaction = new ContentValues();
            senderTransaction.put(COLUMN_TRANSACTION_USER_ID, senderId);
            senderTransaction.put(COLUMN_TRANSACTION_TYPE, "Send Money");
            senderTransaction.put(COLUMN_TRANSACTION_AMOUNT, money);
            db.insert(TABLE_TRANSACTION_HISTORY, null, senderTransaction);

            // Insert recipient's transaction history
            ContentValues recipientTransaction = new ContentValues();
            recipientTransaction.put(COLUMN_TRANSACTION_USER_ID, recipientId);
            recipientTransaction.put(COLUMN_TRANSACTION_TYPE, "Receive Money");
            recipientTransaction.put(COLUMN_TRANSACTION_AMOUNT, money);
            db.insert(TABLE_TRANSACTION_HISTORY, null, recipientTransaction);

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

    // Get all transaction history of the user
    public Cursor getAllTransactionHistory() {
        SQLiteDatabase db = this.getReadableDatabase();

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE_TRANSACTION_HISTORY, COLUMN_TRANSACTION_USER_ID
        );
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    // Set phone number of the user
    public void setFullName(String newFullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, newFullName);

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        // Update the database
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (rowsAffected > 0) {
            // Update SharedPreferences
            pref.setFullname(newFullName);
        } else {
            Toast.makeText(context, "Failed to update full name. No rows affected.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    // Set phone number of the user
    public void setUsername(String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        // Update the database
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (rowsAffected > 0) {
            // Update SharedPreferences
            pref.setUsername(newUsername);
        } else {
            Toast.makeText(context, "Failed to update username. No rows affected.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }


    // Set email of the user
    public void setEmail(String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, newEmail);

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        // Update the database
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (rowsAffected > 0) {
            // Update SharedPreferences
            pref.setEmail(newEmail);
        } else {
            Toast.makeText(context, "Failed to update email. No rows affected.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    // Set phone number of user
    public void setPhoneNumber(String newPhoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONENUMBER, newPhoneNumber);

        SharedPreferences pref = new SharedPreferences(context);
        int userId = pref.getUserId();

        // Update the database
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});

        if (rowsAffected > 0) {
            // Update SharedPreferences
            pref.setPhoneNumber(newPhoneNumber);
        } else {
            Toast.makeText(context, "Failed to update phone number. No rows affected.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    // Deletes the user account with its transaction history and wallet
    public void deleteUserAccount(Context context) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            SharedPreferences pref = new SharedPreferences(context);
            int userId = pref.getUserId();

            if (userId == -1) {
                throw new IllegalArgumentException("Invalid user ID");
            }

            db.beginTransaction();

            String deleteUserQuery = String.format(
                    "DELETE FROM %s WHERE %s = ?",
                    TABLE_USERS, COLUMN_USER_ID
            );
            db.execSQL(deleteUserQuery, new Object[]{userId});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error deleting account!", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    // Retrieves all username excluding the current user
    public Cursor getAllUsernames(Context context) {
        SQLiteDatabase db = this.getReadableDatabase();

        SharedPreferences pref = new SharedPreferences(context);
        int currentUserId = pref.getUserId();

        String query = String.format(
                "SELECT %s FROM %s WHERE %s != ?",
                COLUMN_USERNAME, TABLE_USERS, COLUMN_USER_ID
        );
        return db.rawQuery(query, new String[]{String.valueOf(currentUserId)});
    }

}
