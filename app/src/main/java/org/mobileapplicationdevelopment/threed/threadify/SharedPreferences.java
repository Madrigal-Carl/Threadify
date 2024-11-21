package org.mobileapplicationdevelopment.threed.threadify;

import android.content.Context;

public class SharedPreferences {
    private static final String PREF_NAME = "ThreadifyPrefs";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";

    private android.content.SharedPreferences sharedPreferences;
    private android.content.SharedPreferences.Editor editor;

    public SharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoginState(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public void setFullname(String fullname) {
        editor.putString(KEY_FULLNAME, fullname);
        editor.apply();
    }

    public String getFullname() {
        return sharedPreferences.getString(KEY_FULLNAME, "Unknown");
    }

    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "Guest");
    }

    public void clearPreferences() {
        editor.clear();
        editor.apply();
    }
}
