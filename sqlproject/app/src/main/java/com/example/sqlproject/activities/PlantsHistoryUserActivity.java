package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.example.sqlproject.Utils;
import com.example.sqlproject.adapters.RecycleAdapterPlantsUser;
import com.example.sqlproject.entities.DataCallback;
import com.example.sqlproject.entities.Trees;
import com.example.sqlproject.entities.TreesDataCallback;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class PlantsHistoryUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecycleAdapterPlantsUser.RecycleViewClickListener listener;
    private DrawerLayout drawerLayout;
    private TextView tvEmptyPlantsUser;
    private RecyclerView recyclerView;
    private final TreesDataCallback treesDataCallback = new TreesDataCallback();

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plants_history_user_activity);

        recyclerView = findViewById(R.id.rVuserPlant);
        tvEmptyPlantsUser = findViewById(R.id.tvEmptyPlantsUser);
        drawerLayout = findViewById(R.id.drawerLayoutUserPlant);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbarUserPlant);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_planted_trees);

        tvEmptyPlantsUser.setVisibility(View.GONE);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tVUserName);
        TextView navEmail = headerView.findViewById(R.id.textView3);
        navUsername.setText(Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName());
        navEmail.setText(Users.loggedOnUser.getEmail());

        initRecycleView();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(PlantsHistoryUserActivity.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant) {
            Intent intent = new Intent(PlantsHistoryUserActivity.this, TreesListActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(PlantsHistoryUserActivity.this, InfoUpdateActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_log_out) {
            LogoutDialogBoxActivity logoutDialogBoxActivity = new LogoutDialogBoxActivity(this);
            logoutDialogBoxActivity.show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
    }

    void initRecycleView() {
        tvEmptyPlantsUser.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Utils.importPlantsByUserID(Users.loggedOnUser.getID(), new DataCallback() {
            @Override
            public void onSuccess(List filteredPlants) {
                if (filteredPlants.isEmpty()) {
                    tvEmptyPlantsUser.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmptyPlantsUser.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    itemClick();
                    RecycleAdapterPlantsUser adapter = new RecycleAdapterPlantsUser(
                            Trees.getTrees(treesDataCallback),
                            Users.loggedOnUser.getID(),
                            listener
                    );
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Exception e) {
                Log.e("initRecycleView", "Error loading plants: " + e.getMessage());
                tvEmptyPlantsUser.setText("Error loading plants. Please try again later.");
                tvEmptyPlantsUser.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    void itemClick() {
        listener = (v, position) -> {
        };
    }
}