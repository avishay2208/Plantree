package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlproject.CurrencyConverter;
import com.example.sqlproject.R;
import com.example.sqlproject.Utils;
import com.example.sqlproject.adapters.RecycleAdapterTrees;
import com.example.sqlproject.entities.Tree;
import com.example.sqlproject.entities.Trees;
import com.example.sqlproject.entities.TreesDataCallback;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TreesListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context context;
    private RecycleAdapterTrees.RecycleViewClickListener listener;
    private RecycleAdapterTrees treeAdapter;
    private DrawerLayout drawerLayout;
    private List<Tree> allTrees;
    private List<Tree> filteredTrees;
    private ToggleButton tbCurrency;
    private double usd_Currency = 1;
    private boolean isILS = true;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.trees_list_activity);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        fetchInitialData(
                trees -> {
                    progressBar.setVisibility(View.GONE);
                    allTrees = new ArrayList<>(trees);
                    filteredTrees = new ArrayList<>(trees);
                    initRecycleView();
                    initToggleButtons();
                },
                () -> {
                    progressBar.setVisibility(View.GONE);
                    showErrorAndExit();
                }
        );

        fetchUsdRate();

        drawerLayout = findViewById(R.id.drawerLayoutTreesList);
        NavigationView navigationView = findViewById(R.id.nav_view_trees_list);
        Toolbar toolbar = findViewById(R.id.toolbarTreesList);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_view_trees_list);


        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tVUserName);
        TextView navEmail = headerView.findViewById(R.id.textView3);
        navUsername.setText(Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName());
        navEmail.setText(Users.loggedOnUser.getEmail());

        SearchView searchView = findViewById(R.id.searchViewTrees);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTrees(newText);
                return false;
            }
        });
    }

    private void fetchInitialData(Consumer<List<Tree>> onSuccess, Runnable onFailure) {
        Utils.importTrees(new TreesDataCallback() {
            @Override
            public void onSuccess(List<Tree> trees) {
                super.onSuccess(trees);
                if (trees != null && !trees.isEmpty()) {
                    onSuccess.accept(trees);
                } else {
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                onFailure.run();
            }
        });
    }

    private void showErrorAndExit() {
        Toast.makeText(this, "Failed to load trees.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void fetchUsdRate() {
        executor.execute(() -> {
            try {
                double fetchedRate = CurrencyConverter.getUSD_Rate();
                mainHandler.post(() -> usd_Currency = fetchedRate);
            } catch (IOException e) {
                mainHandler.post(() -> Toast.makeText(TreesListActivity.this, "Failed to fetch USD rate. Using default.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(TreesListActivity.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees) {
            Intent intent;
            if (Users.loggedOnUser.isAdmin()) {
                intent = new Intent(TreesListActivity.this, PlantsHistoryAdminActivity.class);
            } else {
                intent = new Intent(TreesListActivity.this, PlantsHistoryUserActivity.class);
            }
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(TreesListActivity.this, InfoUpdateActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_log_out) {
            LogoutDialogBoxActivity logoutDialogBoxActivity = new LogoutDialogBoxActivity(this);
            logoutDialogBoxActivity.show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initToggleButtons() {
        tbCurrency = findViewById(R.id.tbCurrency);
        tbCurrency.setOnClickListener(this);
    }

    private void filterTrees(String query) {
        if (query.isEmpty()) {
            filteredTrees = allTrees;
        } else {
            filteredTrees = allTrees
                    .stream()
                    .filter(tree -> tree.getType().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }

        treeAdapter.updateTrees(filteredTrees);
    }

    @Override
    public void onClick(View view) {
        if (tbCurrency.isPressed()) {
            if (tbCurrency.isChecked()) {
                isILS = false;
                allTrees.forEach(tree -> tree.setPrice(tree.getPrice() * usd_Currency));
            } else {
                isILS = true;
                allTrees.forEach(tree -> tree.setPrice(tree.getPrice() / usd_Currency));
            }
            treeAdapter.updateTrees(filteredTrees);
            initRecycleView();
        }
    }

    private void initRecycleView() {
        RecyclerView recycleView;
        recycleView = findViewById(R.id.rVtreesList);

        itemClick();
        treeAdapter = new RecycleAdapterTrees(filteredTrees, isILS, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleView.setLayoutManager(layoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setAdapter(treeAdapter);
    }

    private void itemClick() {
        listener = (v, position) -> {
            Tree chosenTree = filteredTrees.get(position);
            Intent intent = new Intent(context, TreeDataPreviewActivity.class);
            intent.putExtra("type", chosenTree.getType());
            intent.putExtra("isILS", isILS);
            intent.putExtra("usd_Currency", usd_Currency);
            Trees.setChosenTree(chosenTree);
            context.startActivity(intent);
        };
    }
}