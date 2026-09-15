package com.example.petcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "PetCarePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_PROFILE_IMAGE = "profileImage";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String name, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void updateProfileImage(String uri) {
        editor.putString(KEY_PROFILE_IMAGE, uri);
        editor.apply();
    }

    public String getProfileImage() {
        return pref.getString(KEY_PROFILE_IMAGE, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}