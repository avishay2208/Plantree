package com.example.sqlproject.entities;

import android.util.Log;

import java.util.List;

public class PlantsDataCallback implements DataCallback<Plant>{

    @Override
    public void onSuccess(List<Plant> plants) {
        if (plants != null && !plants.isEmpty()) {
            Plants.setPlants(plants);
            Log.d("PlantsDataCallback", "Plants successfully imported: " + plants.size());
        } else {
            Log.e("PlantsDataCallback", "No plants found.");
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("PlantsDataCallback", "Failed to import plants: " + e.getMessage());
    }
}
