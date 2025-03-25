package com.example.sqlproject.entities;

import com.example.sqlproject.Utils;

import java.util.ArrayList;
import java.util.List;

public class Trees extends ArrayList<Tree> {

    private static Trees trees = new Trees();
    public static Tree chosenTree;

    public static Trees getTrees(TreesDataCallback treesDataCallback) {
        Utils.importTrees(treesDataCallback);

        return trees;
    }

    public static void setTrees(Trees trees) {
        Trees.trees = trees;
    }

    public static void setTrees(List<Tree> treesList) {
        Trees.trees.clear();
        Trees.trees.addAll(treesList);
    }

    public static Tree getTreeByType(String type) {
        return trees.stream().filter(item -> item.getType().equals(type)).findAny().orElse(null);
    }

    public static void setChosenTree(Tree tree) {
        chosenTree = tree;
    }

    public static List<String> getTreeTypesOnly() {
        List<String> types = new ArrayList<>();
        trees.forEach(tree -> types.add(tree.getType()));
        return types;
    }
}
