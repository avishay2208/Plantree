package com.example.sqlproject.entities;

import android.util.Log;

import com.example.sqlproject.entities.DataCallback;
import com.example.sqlproject.entities.Location;
import com.example.sqlproject.entities.Locations;
import java.util.List;

public class LocationsDataCallback implements DataCallback<Location> {
    @Override
    public void onSuccess(List<Location> locations) {
        if (locations != null && !locations.isEmpty()) {
            Locations.setLocations(locations);
            Log.d("LocationsDataCallback", "Locations successfully imported: " + locations.size());
        } else {
            Log.e("LocationsDataCallback", "No locations found.");
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("LocationsDataCallback", "Failed to import locations: " + e.getMessage());
    }
}
