package com.example.sqlproject;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sqlproject.entities.User;

import org.json.JSONException;

public class SaveUserToFile {

    final static String FILENAME="details";

    public static void saveString(Context context, User value) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("SAVED_USER", Serialization.convertObjectToStringJson(value));
        editor.apply();
    }
    public static User getValue(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME,0);
        return Serialization.convertStringJsonToObject(User.class, sp.getString(key,null));
    }

    public static void clearFile(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILENAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}