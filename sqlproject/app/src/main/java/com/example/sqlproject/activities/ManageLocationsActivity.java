package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.sqlproject.entities.DataCallback;
import com.example.sqlproject.entities.Location;
import com.example.sqlproject.entities.Locations;
import com.example.sqlproject.entities.LocationsDataCallback;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.stream.Collectors;

public class ManageLocationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context context;
    private Button btnAddLocation, btnEditLocation, btnDeleteLocation, btnSaveChangesLocation, btnCancelSave, btnSaveNewLocation;
    private EditText etLocationAddress, etLocationLatitude, etLocationLongitude;
    private TextInputLayout textInputLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private DrawerLayout drawerLayout;
    private DatabaseReference locationsRef;
    private String locationAddress;
    private double locationLatitude, locationLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_locations_activity);

        context = this;
        locationsRef = FirebaseDatabase.getInstance().getReference("locations");

        initToolbarAndNavigation();
        fetchInitialData(() -> {
            initButtons();
            initEditTexts();
            initTextInput();
        });
    }

    @SuppressLint("SetTextI18n")
    private void initToolbarAndNavigation() {
        drawerLayout = findViewById(R.id.drawerLayoutAdminLocationsManagement);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbarAdminLocationsManagement);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tVUserName);
        TextView navEmail = headerView.findViewById(R.id.textView3);
        navUsername.setText(Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName());
        navEmail.setText(Users.loggedOnUser.getEmail());
    }

    private void fetchInitialData(Runnable onComplete) {
        Utils.importLocations(new LocationsDataCallback() {
            @Override
            public void onSuccess(List<Location> locations) {
                super.onSuccess(locations);
                if (locations != null && !locations.isEmpty()) {
                    onComplete.run();
                } else {
                    showErrorAndExit("No locations found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                super.onFailure(e);
                showErrorAndExit("Error fetching locations: " + e.getMessage());
            }
        });
    }

    private void showErrorAndExit(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    void initButtons() {
        btnAddLocation = findViewById(R.id.btnAddLocation);
        btnEditLocation = findViewById(R.id.btnEditManageLocations);
        btnDeleteLocation = findViewById(R.id.btnDeleteManageLocations);
        btnSaveChangesLocation = findViewById(R.id.btnSaveChangesManageLocations);
        btnSaveNewLocation = findViewById(R.id.btnSaveNewLocationManageLocations);
        btnCancelSave = findViewById(R.id.btnCancelChangesManageLocations);

        setButtonListeners(btnAddLocation, btnEditLocation, btnDeleteLocation, btnSaveChangesLocation, btnSaveNewLocation, btnCancelSave);

        toggleVisibility(btnSaveChangesLocation, btnSaveNewLocation, btnCancelSave);
    }

    void initEditTexts() {
        etLocationAddress = findViewById(R.id.etChangeAddress);
        etLocationLatitude = findViewById(R.id.etChangeLatitudeLocations);
        etLocationLongitude = findViewById(R.id.etChangeLongitudeLocations);

        toggleVisibility(etLocationAddress, etLocationLatitude, etLocationLongitude);
    }

    void initTextInput() {
        textInputLayout = findViewById(R.id.inputLayoutManageLocations);
        autoCompleteTextView = findViewById(R.id.inputTVManageLocations);

        Utils.importLocations(new DataCallback<Location>() {
            @Override
            public void onSuccess(List<Location> locations) {
                if (locations != null && !locations.isEmpty()) {
                    List<String> addresses = locations.stream()
                            .map(Location::getAddress)
                            .collect(Collectors.toList());
                    setupAutoCompleteTextView(addresses);
                } else {
                    Log.e("ManageLocationsActivity", "No locations fetched.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ManageLocationsActivity", "Error fetching locations: " + e.getMessage());
            }
        });
    }

    private void setupAutoCompleteTextView(List<String> addresses) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_drop_down, addresses);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            Locations.setChosenLocation(Locations.getLocationByAddress(addresses.get(position)));
            textInputLayout.setErrorEnabled(false);
        });
    }

    private void saveLocation(boolean isNewLocation) {
        locationAddress = etLocationAddress.getText().toString();
        locationLatitude = Double.parseDouble(etLocationLatitude.getText().toString());
        locationLongitude = Double.parseDouble(etLocationLongitude.getText().toString());

        if (validateFields(locationAddress, locationLatitude, locationLongitude)) {
            if (isNewLocation) {
                addNewLocation();
            } else {
                updateLocation();
            }
        }
    }

    private void addNewLocation() {
        locationsRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newLocationId = getNewLocationId(snapshot);

                Location newLocation = new Location(newLocationId, locationAddress, locationLatitude, locationLongitude);
                saveToDatabase(String.valueOf(newLocationId), newLocation, "Location added successfully", "Failed to add location");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to retrieve last location ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLocation() {
        int locationId = Locations.chosenLocation.getID();
        Location updatedLocation = new Location(locationId, locationAddress, locationLatitude, locationLongitude);

        saveToDatabase(String.valueOf(locationId), updatedLocation, "Location updated successfully", "Failed to update location");
    }

    private void saveToDatabase(String key, Location location, String successMessage, String failureMessage) {
        locationsRef.child(key).setValue(location)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
                    resetFormToOriginalState();
                })
                .addOnFailureListener(e -> Toast.makeText(context, failureMessage + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private int getNewLocationId(DataSnapshot snapshot) {
        int newLocationId = 0;
        if (snapshot.exists()) {
            for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                Location lastLocation = locationSnapshot.getValue(Location.class);
                if (lastLocation != null) {
                    newLocationId = lastLocation.getID() + 1;
                }
            }
        }
        return newLocationId;
    }

    private void toggleVisibility(View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    private void setButtonListeners(Button... buttons) {
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddLocation) prepareFormForAdding();
        else if (v == btnSaveNewLocation) saveLocation(true);
        else if (v == btnSaveChangesLocation) saveLocation(false);
        else if (v == btnCancelSave) resetFormToOriginalState();
        else if (Locations.chosenLocation != null) {
            locationAddress = Locations.chosenLocation.getAddress();
            locationLatitude = Locations.chosenLocation.getLatitude();
            locationLongitude = Locations.chosenLocation.getLongitude();
                if (v == btnEditLocation) prepareFormForEdit();
                else if (v == btnDeleteLocation) deleteLocation();
        } else {
            Toast.makeText(this, "select address", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLocation() {
        DeleteLocationDialogBoxActivity deleteLocationDialogBoxActivity = new DeleteLocationDialogBoxActivity(this, Locations.chosenLocation);
        deleteLocationDialogBoxActivity.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(ManageLocationsActivity.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant) {
            Intent intent = new Intent(ManageLocationsActivity.this, TreesListActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees) {
            Intent intent = Users.loggedOnUser.isAdmin()
                    ? new Intent(ManageLocationsActivity.this, PlantsHistoryAdminActivity.class)
                    : new Intent(ManageLocationsActivity.this, PlantsHistoryUserActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(ManageLocationsActivity.this, InfoUpdateActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_log_out) {
            new LogoutDialogBoxActivity(this).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean validateFields(final String address, final double latitude, final double longitude) {
        if (address.isEmpty() ||
                String.valueOf(latitude).isEmpty() ||
                String.valueOf(longitude).isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void prepareFormForEdit() {
        textInputLayout.setVisibility(View.GONE);
        autoCompleteTextView.setVisibility(View.GONE);
        btnEditLocation.setVisibility(View.GONE);
        btnDeleteLocation.setVisibility(View.GONE);
        btnAddLocation.setVisibility(View.GONE);
        btnCancelSave.setVisibility(View.VISIBLE);
        btnSaveChangesLocation.setVisibility(View.VISIBLE);
        etLocationAddress.setVisibility(View.VISIBLE);
        etLocationLatitude.setVisibility(View.VISIBLE);
        etLocationLongitude.setVisibility(View.VISIBLE);

        etLocationAddress.setText(Locations.chosenLocation.getAddress());
        etLocationLatitude.setText(String.valueOf(Locations.chosenLocation.getLatitude()));
        etLocationLongitude.setText(String.valueOf(Locations.chosenLocation.getLongitude()));
    }

    public void prepareFormForAdding() {
        textInputLayout.setVisibility(View.GONE);
        autoCompleteTextView.setVisibility(View.GONE);
        btnEditLocation.setVisibility(View.GONE);
        btnDeleteLocation.setVisibility(View.GONE);
        btnAddLocation.setVisibility(View.GONE);
        btnCancelSave.setVisibility(View.VISIBLE);
        btnSaveNewLocation.setVisibility(View.VISIBLE);
        etLocationAddress.setVisibility(View.VISIBLE);
        etLocationLatitude.setVisibility(View.VISIBLE);
        etLocationLongitude.setVisibility(View.VISIBLE);
    }

    public void resetFormToOriginalState() {
        textInputLayout.setVisibility(View.VISIBLE);
        autoCompleteTextView.setVisibility(View.VISIBLE);
        btnEditLocation.setVisibility(View.VISIBLE);
        btnDeleteLocation.setVisibility(View.VISIBLE);
        btnAddLocation.setVisibility(View.VISIBLE);
        btnCancelSave.setVisibility(View.GONE);
        btnSaveChangesLocation.setVisibility(View.GONE);
        btnSaveNewLocation.setVisibility(View.GONE);
        etLocationAddress.setVisibility(View.GONE);
        etLocationLatitude.setVisibility(View.GONE);
        etLocationLongitude.setVisibility(View.GONE);
        etLocationAddress.setText("");
        etLocationLatitude.setText("");
        etLocationLongitude.setText("");
    }
}