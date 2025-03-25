package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlproject.R;
import com.example.sqlproject.adapters.RecycleAdapterPlantsAdmin;
import com.example.sqlproject.Utils;
import com.example.sqlproject.entities.Plant;
import com.example.sqlproject.entities.Plants;
import com.example.sqlproject.entities.PlantsDataCallback;
import com.example.sqlproject.entities.Tree;
import com.example.sqlproject.entities.TreesDataCallback;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class PlantsHistoryAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecycleAdapterPlantsAdmin.RecycleViewClickListener listener;
    private DrawerLayout drawerLayout;
    private TextView tvEmptyPlantsAdmin;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plants_history_admin_activity);

        ProgressBar progressBar = findViewById(R.id.progressBarAdminPlantsActivity);
        progressBar.setVisibility(View.VISIBLE);

        drawerLayout = findViewById(R.id.drawerLayoutAdminPlant);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbarAdminPlant);
        tvEmptyPlantsAdmin = findViewById(R.id.tvEmptyPlantsAdmin);
        tvEmptyPlantsAdmin.setVisibility(View.GONE);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_planted_trees);

        fetchInitialData(() -> {
            progressBar.setVisibility(View.GONE);
            initRecycleView();
        });

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tVUserName);
        TextView navEmail = headerView.findViewById(R.id.textView3);
        navUsername.setText(Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName());
        navEmail.setText(Users.loggedOnUser.getEmail());
    }

    private void fetchInitialData(Runnable onComplete) {
        Utils.importPlants(new PlantsDataCallback() {
            @Override
            public void onSuccess(List<Plant> plants) {
                super.onSuccess(plants);
                if (plants != null && !plants.isEmpty()) {
                    onComplete.run();
                } else {
                    showErrorAndExit("No plants found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                showErrorAndExit("Error fetching plants: " + e.getMessage());
            }
        });

        Utils.importTrees(new TreesDataCallback() {
            @Override
            public void onSuccess(List<Tree> trees) {
                super.onSuccess(trees);
                if (trees != null && !trees.isEmpty()) {
                    onComplete.run();
                } else {
                    showErrorAndExit("No trees found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                showErrorAndExit("Error fetching trees: " + e.getMessage());
            }
        });
    }

    private void showErrorAndExit(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home){
            Intent intent = new Intent(PlantsHistoryAdminActivity.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant){
            Intent intent = new Intent(PlantsHistoryAdminActivity.this, TreesListActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(PlantsHistoryAdminActivity.this, InfoUpdateActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_log_out){
            LogoutDialogBoxActivity logoutDialogBoxActivity = new LogoutDialogBoxActivity(this);
            logoutDialogBoxActivity.show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {}

    void initRecycleView() {
        RecyclerView recycleView;
        recycleView = findViewById(R.id.rVadminPlant);
        if (Plants.getPlants().isEmpty()) {
            recycleView.setVisibility(View.GONE);
            tvEmptyPlantsAdmin.setVisibility(View.VISIBLE);
        } else {
            recycleView.setVisibility(View.VISIBLE);
            tvEmptyPlantsAdmin.setVisibility(View.GONE);
        }

        itemClick();
        RecycleAdapterPlantsAdmin adapter = new RecycleAdapterPlantsAdmin(Plants.getPlants(), listener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleView.setLayoutManager(layoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.setAdapter(adapter);
    }

    void itemClick() {
        listener = (v, position) -> {
        };
    }
}