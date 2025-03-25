package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlproject.R;
import com.example.sqlproject.Serialization;
import com.example.sqlproject.adapters.RecycleAdapterUsers;
import com.example.sqlproject.entities.User;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context context;
    private RecyclerView recyclerView;
    private TextView tvUserCounter;
    private CheckBox cbIsAdmin;
    private List<User> usersList;
    private List<User> filteredUsers;
    private DatabaseReference usersRef;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_users_activity);

        context = this;
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initViews();
        loadUsers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rVusersList);
        SearchView searchView = findViewById(R.id.searchViewUsers);
        tvUserCounter = findViewById(R.id.tvUserCounter);
        drawerLayout = findViewById(R.id.drawerLayoutAdminUsersManagement);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbarAdminUsersManagement);
        Button btnSaveChanges = findViewById(R.id.btnSaveChangesManageUsers);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUserManageUsers);
        cbIsAdmin = findViewById(R.id.cBisAdmin);

        btnSaveChanges.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
        cbIsAdmin.setOnClickListener(this);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String json = new Gson().toJson(userSnapshot.getValue());
                    User user = Serialization.convertStringJsonToObject(User.class, json);
                    if (user != null) {
                        usersList.add(user);
                    }
                }
                filteredUsers = new ArrayList<>(usersList);
                updateRecyclerView();
                tvUserCounter.setText(usersList.size() + " users in total");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView() {
        RecycleAdapterUsers userAdapter = new RecycleAdapterUsers(filteredUsers, position -> {
            Users.setChosenUser(filteredUsers.get(position));
            cbIsAdmin.setChecked(Users.chosenUser.isAdmin());
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void filterUsers(String query) {
        if (query.isEmpty()) {
            filteredUsers = new ArrayList<>(usersList);
        } else {
            filteredUsers = new ArrayList<>();
            for (User user : usersList) {
                if ((user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(query.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }
        }
        updateRecyclerView();
    }

    @Override
    public void onClick(View v) {
        if (Users.chosenUser == null) {
            Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == R.id.btnSaveChangesManageUsers) {
            updateUserAdminStatus();
        } else if (v.getId() == R.id.btnDeleteUserManageUsers) {
            deleteUser();
        }
    }

    private void updateUserAdminStatus() {
        Users.chosenUser.setIsAdmin(cbIsAdmin.isChecked());
        if (Users.chosenUser.getID() == 1)
            Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show();
        else {
        usersRef.child(String.valueOf(Users.chosenUser.getID())).setValue(Users.chosenUser)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    if (Users.chosenUser.getID() == Users.loggedOnUser.getID())
                        Users.userLogout(this);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteUser() {
        if (Users.chosenUser.getID() == 1) {
            Toast.makeText(this, "Cannot delete admin account", Toast.LENGTH_SHORT).show();
        } else {
            DeleteUserDialogBoxActivity deleteUserDialogBoxActivity = new DeleteUserDialogBoxActivity(this, Users.chosenUser.getID());
            deleteUserDialogBoxActivity.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(ManageUsersActivity.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant) {
            Intent intent = new Intent(ManageUsersActivity.this, TreesListActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees) {
            Intent intent;
            if (Users.loggedOnUser.isAdmin()) {
                intent = new Intent(ManageUsersActivity.this, PlantsHistoryAdminActivity.class);
            } else {
                intent = new Intent(ManageUsersActivity.this, PlantsHistoryUserActivity.class);
            }
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(ManageUsersActivity.this, InfoUpdateActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_log_out){
            LogoutDialogBoxActivity logoutDialogBoxActivity = new LogoutDialogBoxActivity(this);
            logoutDialogBoxActivity.show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}