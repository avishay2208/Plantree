package com.example.sqlproject.entities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.sqlproject.SaveUserToFile;
import com.example.sqlproject.Utils;
import com.example.sqlproject.activities.MainActivity;

import java.util.ArrayList;

public class Users extends ArrayList<User> {

    public Context context;
    @SuppressLint("StaticFieldLeak")
    private static Users users = new Users();
    public static User loggedOnUser;
    public static User chosenUser;

    public static Users getUsers() {
        Utils.importUsers();
        return users;
    }

    public static void setLoggedOnUser(User user) {
        loggedOnUser = user;
    }

    public static void userLogout(Context context) {
        setLoggedOnUser(null);
        SaveUserToFile.clearFile(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).finishAffinity();
    }

    public static void setUsers(Users users) {
        Users.users = users;
    }

    public static void setChosenUser(User user) {
        chosenUser = user;
    }
}
