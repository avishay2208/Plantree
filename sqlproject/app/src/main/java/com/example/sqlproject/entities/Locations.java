package com.example.sqlproject.entities;

import com.example.sqlproject.Utils;

import java.util.ArrayList;
import java.util.List;

public class Locations extends ArrayList<Location> {

    private static Locations locations = new Locations();
    public static Location chosenLocation;

    public static Location getLocationByAddress(String address) {
        return locations.stream().filter(item -> item.getAddress().equals(address)).findAny().get();
    }

    public static Locations getLocations(LocationsDataCallback locationDataCallback) {
        Utils.importLocations(locationDataCallback);

        return locations;
    }

    public static void setLocations(Locations locations) {
        Locations.locations.clear();
        Locations.locations = locations;
    }

    public static void setLocations(List<Location> locationList) {
        Locations.locations.clear();
        Locations.locations.addAll(locationList);
    }

    public static void setChosenLocation(Location location) {
        chosenLocation = location;
    }

    public static List<String> getAddressesOnly(LocationsDataCallback locationDataCallback) {
        List<String> addresses = new ArrayList<>();
        for (Location location : Locations.getLocations(locationDataCallback)) {
            addresses.add(location.getAddress());
        }
        return addresses;
    }
}