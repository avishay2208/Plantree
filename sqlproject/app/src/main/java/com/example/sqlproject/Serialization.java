package com.example.sqlproject;

import com.example.sqlproject.entities.User;
import com.google.gson.Gson;

public class Serialization {

    public static <T> T convertStringJsonToObject(Class<T> clas, String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, clas);
    }

    public static String convertObjectToStringJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }
}