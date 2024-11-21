package org.mobileapplicationdevelopment.threed.threadify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Threadify.db";
    private static final int DATABASE_VERSION = 1;

//    USERS TABLE
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FULLNAME = "fullname";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

//    WALLETS TABLE
    private static final String TABLE_WALLETS = "Wallets";
    private static final String COLUMN_WALLET_ID = "wallet_id";
    private static final String COLUMN_WALLET_USER_ID = "user_id";
    private static final String COLUMN_WALLET_BALANCE = "current_balance";

//    TRANSACTION HISTORY
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

        String userQuery = String.format(
                "CREATE TABLE %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s VARCHAR(50) NOT NULL, " +
                        "%s VARCHAR(18) NOT NULL UNIQUE, " +
                        "%s VARCHAR(18) NOT NULL)",
                TABLE_USERS, COLUMN_USER_ID, COLUMN_FULLNAME, COLUMN_USERNAME, COLUMN_PASSWORD
        );
        db.execSQL(userQuery);

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

    public Boolean addUser(String fullname, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FULLNAME, fullname);
        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, cv);

        if (result == -1) {

            Toast.makeText(context, String.format("%s username is already taken", username), Toast.LENGTH_SHORT).show();
            db.close();
            return false;
        } else {

            Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int userIdColumnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
                    if (userIdColumnIndex != -1) {
                        long userId = cursor.getLong(userIdColumnIndex);

                        ContentValues walletValues = new ContentValues();
                        walletValues.put(COLUMN_WALLET_USER_ID, userId);
                        walletValues.put(COLUMN_WALLET_BALANCE, 0.0);

                        long walletResult = db.insert(TABLE_WALLETS, null, walletValues);

                        if (walletResult == -1) {
                            Toast.makeText(context, "Error creating wallet", Toast.LENGTH_SHORT).show();
                            db.close();
                            return false;
                        } else {
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
                            db.close();
                            return true;
                        }
                    } else {
                        Toast.makeText(context, "User ID column not found", Toast.LENGTH_SHORT).show();
                        db.close();
                        return false;
                    }
                } else {
                    Toast.makeText(context, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    db.close();
                    return false;
                }
            } else {
                Toast.makeText(context, "Error executing query", Toast.LENGTH_SHORT).show();
                db.close();
                return false;
            }
        }
    }

    public boolean userAuth(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        SharedPreferences pref = new SharedPreferences(context);

        String selection = String.format("%s = ? AND %s = ?", COLUMN_USERNAME, COLUMN_PASSWORD);
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_FULLNAME, COLUMN_USERNAME},
                selection, selectionArgs,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {

            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME));
            String user_name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));

            pref.setLoginState(true);
            pref.setUserId(userId);
            pref.setUsername(user_name);
            pref.setFullname(fullname);

            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();
        return false;
    }
}
