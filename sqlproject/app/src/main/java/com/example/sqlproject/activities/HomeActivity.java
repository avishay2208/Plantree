package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sqlproject.R;
import com.example.sqlproject.Utils;
import com.example.sqlproject.entities.Plant;
import com.example.sqlproject.entities.Plants;
import com.example.sqlproject.entities.PlantsDataCallback;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context context;
    private Button btnPlantNewTree, btnAdminControls;
    private TextView tvNumOfAllPlants;
    private DrawerLayout drawerLayout;
    private static final boolean isILS = true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        context = this;

        Utils.importUsers();
        Utils.importPlants(new PlantsDataCallback());

        drawerLayout = findViewById(R.id.drawerLayoutAdminPlant);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbarAdminPlant);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        initButtons();
        initTextViews();

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tVUserName);
        TextView navEmail = headerView.findViewById(R.id.textView3);
        navUsername.setText(Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName());
        navEmail.setText(Users.loggedOnUser.getEmail());

        if (!Users.loggedOnUser.isAdmin()) {
            btnAdminControls.setVisibility(View.GONE);
            tvNumOfAllPlants.setVisibility(View.GONE);
        }

        fetchInitialPlantsData(() -> {
            initButtons();
            initTextViews();
        });
    }

    void initButtons() {
        btnPlantNewTree = findViewById(R.id.btnPlantNewTree);
        btnAdminControls = findViewById(R.id.btnAdminControls);
        btnPlantNewTree.setOnClickListener(this);
        btnAdminControls.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    void initTextViews() {
        TextView tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        TextView tvMemberSince = findViewById(R.id.tvMemberSince);
        TextView tvPlantCounter = findViewById(R.id.tvNumOfTreesPlanted);
        tvNumOfAllPlants = findViewById(R.id.tvAdminNumOfAllPlants);

        tvWelcomeUser.setText("Hello " + Users.loggedOnUser.getFirstName() + "!");
        tvMemberSince.setText("Member since: " + Users.loggedOnUser.getJoinDate());
        tvPlantCounter.setText("You planted " + Users.loggedOnUser.getPlantCounter() + " trees");
        tvNumOfAllPlants.setText(Plants.getPlants().size() + " trees have been planted in total");
    }

    private void fetchInitialPlantsData(Runnable onComplete) {
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
    }

    private void showErrorAndExit(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant) {
            Intent intent = new Intent(HomeActivity.this, TreesListActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees) {
            Intent intent;
            if (Users.loggedOnUser.isAdmin()) {
                intent = new Intent(HomeActivity.this, PlantsHistoryAdminActivity.class);
            } else {
                intent = new Intent(HomeActivity.this, PlantsHistoryUserActivity.class);
            }
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(HomeActivity.this, InfoUpdateActivity.class);
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
    public void onClick(View view) {
        if (btnPlantNewTree.isPressed()) {
            Intent intent = new Intent(context, TreesListActivity.class);
            intent.putExtra("isILS", isILS);
            context.startActivity(intent);
        }
        if (btnAdminControls.isPressed()) {
            Intent intent = new Intent(context, AdminControlsActivity.class);
            intent.putExtra("isILS", isILS);
            context.startActivity(intent);
        }
    }
}