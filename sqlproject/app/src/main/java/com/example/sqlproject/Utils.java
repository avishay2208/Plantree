package com.example.sqlproject;

import android.annotation.SuppressLint;

import com.example.sqlproject.entities.DataCallback;
import com.example.sqlproject.entities.Location;
import com.example.sqlproject.entities.Plant;
import com.example.sqlproject.entities.Plants;
import com.example.sqlproject.entities.Tree;
import com.example.sqlproject.entities.Trees;
import com.example.sqlproject.entities.Users;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static void importUsers() {
        RestApi.readData("users", new RestApi.DataCallback() {
            @Override
            public void onSuccess(Object data) {
                String jsonUser = new Gson().toJson(data);
                Users.setUsers(Serialization.convertStringJsonToObject(Users.class, jsonUser));
            }

            @Override
            public void onFailure(Exception e) {
                System.err.println("Error importing users: " + e.getMessage());
            }
        });
    }

    public static void importPlants(DataCallback<Plant> callback) {
        RestApi.readData("plants", new RestApi.DataCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    String jsonPlant = new Gson().toJson(data);
                    List<Plant> plantList = new Gson().fromJson(
                            jsonPlant,
                            new TypeToken<List<Plant>>() {}.getType()
                    );
                    if (plantList != null && !plantList.isEmpty()){
                        Plants.setPlants(plantList);
                        callback.onSuccess(plantList);
                    }
                }
                catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static void importPlantsByUserID(int userID, DataCallback<Plant> callback) {
        RestApi.readData("plants", new RestApi.DataCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    List<Plant> allPlants = new Gson().fromJson(
                            new Gson().toJson(data),
                            new TypeToken<List<Plant>>() {}.getType()
                    );
                    List<Plant> filteredPlants = allPlants.stream()
                            .filter(plant -> plant.getUserID() == userID)
                            .collect(Collectors.toList());
                    callback.onSuccess(filteredPlants);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static void importTrees(DataCallback<Tree> callback) {
        RestApi.readData("trees", new RestApi.DataCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    String jsonTree = new Gson().toJson(data);
                    List<Tree> treeList = new Gson().fromJson(
                            jsonTree,
                            new TypeToken<List<Tree>>() {}.getType()
                    );
                    if (treeList != null && !treeList.isEmpty()) {
                        Trees.setTrees(treeList);
                        callback.onSuccess(treeList);
                    } else {
                        callback.onFailure(new Exception("No trees found."));
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static void importLocations(DataCallback<Location> callback) {
        RestApi.readData("locations", new RestApi.DataCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    String jsonLocation = new Gson().toJson(data);
                    List<Location> locationList = new Gson().fromJson(
                            jsonLocation,
                            new TypeToken<List<Location>>() {}.getType()
                    );
                    if (locationList != null && !locationList.isEmpty()) {
                        callback.onSuccess(locationList);
                    } else {
                        callback.onFailure(new Exception("No locations found."));
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static String getCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }
}
