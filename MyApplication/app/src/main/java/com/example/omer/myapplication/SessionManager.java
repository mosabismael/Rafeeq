package com.example.omer.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Ala Ibrahim on 9/15/2016.
 */
public class SessionManager {

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context) {
        this.context = context;

        sharedPreferences = context.getSharedPreferences("login",0);
        editor = sharedPreferences.edit();
    }

    public void set_login (boolean isLogin) {

        editor.putBoolean("isLogin",isLogin);
        editor.commit();

        Log.d("Session Manager ", "User isLogin");

    } //

    public boolean isLogedIn () {

        return sharedPreferences.getBoolean("isLogin",false);
    } //

    public void setAdmin (boolean isAdmin) {

        editor.putBoolean("isAdmin",isAdmin);
        editor.commit();

        Log.d("Session Manager ", "User isAdmin");

    }
    public boolean isAdminIn () {

        return sharedPreferences.getBoolean("isAdmin",false);
    }
}
