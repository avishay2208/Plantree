package com.example.sqlproject.entities;

import android.util.Log;

import java.util.List;

public class TreesDataCallback implements DataCallback<Tree> {
    @Override
    public void onSuccess(List<Tree> trees) {
        if (trees != null && !trees.isEmpty()) {
            Trees.setTrees(trees);
            Log.d("TreesDataCallback", "Trees successfully imported: " + trees.size());
        } else {
            Log.e("TreesDataCallback", "No trees found.");
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e("TreesDataCallback", "Failed to import trees: " + e.getMessage());
    }
}