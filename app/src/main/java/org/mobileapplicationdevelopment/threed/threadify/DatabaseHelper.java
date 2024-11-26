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
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_FULLNAME, fullname);
            cv.put(COLUMN_USERNAME, username);
            cv.put(COLUMN_PASSWORD, password);

            long userResult = db.insert(TABLE_USERS, null, cv);
            if (userResult == -1) {
                throw new SQLException(String.format("%s username is already taken", username));
            }

            Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                cursor.close();

                ContentValues walletValues = new ContentValues();
                walletValues.put(COLUMN_WALLET_USER_ID, userId);
                walletValues.put(COLUMN_WALLET_BALANCE, 0.0);
                long walletResult = db.insert(TABLE_WALLETS, null, walletValues);
                if (walletResult == -1) {
                    throw new SQLException("Error creating wallet");
                }
            }

            db.setTransactionSuccessful();
            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SQLException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
        return false;
    }

    public boolean userAuth(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.user_id, u.fullname, u.username, w.current_balance " +
                "FROM Users u " +
                "INNER JOIN Wallets w ON u.user_id = w.user_id " +
                "WHERE u.username = ? AND u.password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            SharedPreferences pref = new SharedPreferences(context);

            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME));
            String user_name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String user_balance = cursor.getString(cursor.getColumnIndexOrThrow("current_balance"));

            pref.setLoginState(true);
            pref.setUserId(userId);
            pref.setUsername(user_name);
            pref.setFullname(fullname);
            pref.setBalance(user_balance);

            cursor.close();
            db.close();
            return true;
        }

        cursor.close();
        db.close();
        return false;
    }
}
