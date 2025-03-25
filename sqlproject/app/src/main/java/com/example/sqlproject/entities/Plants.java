package com.example.sqlproject.entities;

import com.example.sqlproject.Utils;

import java.util.ArrayList;
import java.util.List;

public class Plants extends ArrayList<Plant> {

    private static Plants plants = new Plants();

    public static Plants getPlants() {
        if (plants.isEmpty())
            Utils.importPlants(new PlantsDataCallback());

        return plants;
    }

    public static void setPlants(Plants plants) {
        Plants.plants = plants;
    }

    public static void setPlants(List<Plant> plantsList) {
        Plants.plants.clear();
        Plants.plants.addAll(plantsList);
    }
}