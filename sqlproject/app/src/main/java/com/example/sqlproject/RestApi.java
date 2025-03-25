package com.example.sqlproject;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RestApi {
    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public static void writeData(String path, Object data) {
        database.child(path).setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Data written successfully to Firebase!");
                    } else {
                        System.err.println("Error writing data: " + task.getException());
                    }
                });
    }

    public static void readData(String path, DataCallback callback) {
        Log.d("RestApi", "Fetching data from path: " + path);

        database.child(path).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Object result = task.getResult().getValue();
                Log.d("RestApi", "Data fetched successfully: " + result);

                if (result != null) {
                    callback.onSuccess(result);
                } else {
                    Log.e("RestApi", "No data found at path: " + path);
                    callback.onFailure(new Exception("No data found at path: " + path));
                }
            } else {
                Exception error = task.getException();
                Log.e("RestApi", "Error fetching data: " + (error != null ? error.getMessage() : "Unknown error"));
                callback.onFailure(error);
            }
        });
    }

    // Define a callback interface for read operations
    public interface DataCallback {
        void onSuccess(Object data);
        void onFailure(Exception e);
    }
}