package org.mobileapplicationdevelopment.threed.threadify;

import android.content.Context;
import java.text.DecimalFormat;

public class SharedPreferences {
    // Preference file name
    private static final String PREF_NAME = "ThreadifyPrefs";

    // Keys for shared preferences
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONENUMBER = "phoneNumber";
    private static final String KEY_BALANCE = "balance";

    private final android.content.SharedPreferences sharedPreferences;
    private final android.content.SharedPreferences.Editor editor;

    // Constructor
    public SharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Set login state
    public void setLoginState(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Set user ID
    public void setUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Get user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Set user's full name
    public void setFullname(String fullname) {
        editor.putString(KEY_FULLNAME, fullname);
        editor.apply();
    }

    // Get user's full name
    public String getFullname() {
        return sharedPreferences.getString(KEY_FULLNAME, "");
    }

    // Set user's password
    public void setPassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    // Get user's password
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    // Get user's email
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    // Set user's email
    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }



    // Set user's phone number
    public void setPhoneNumber(String phoneNumber) {
        editor.putString(KEY_PHONENUMBER, phoneNumber);
        editor.apply();
    }

    // Get user's phone number
    public String getPhoneNumber() {
        return sharedPreferences.getString(KEY_PHONENUMBER, "");
    }

    // Set username
    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    // Get username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    // Set wallet balance
    public void setBalance(String balance) {
        editor.putString(KEY_BALANCE, balance);
        editor.apply();
    }

    // Get formatted wallet balance
    public String getBalance() {
        String rawBalance = sharedPreferences.getString(KEY_BALANCE, "0.0");

        try {
            double balance = Double.parseDouble(rawBalance);
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(balance);
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    // Clear all preferences
    public void clearPreferences() {
        editor.clear();
        editor.apply();
    }
}
