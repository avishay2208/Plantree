package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sqlproject.R;
import com.example.sqlproject.Utils;
import com.example.sqlproject.entities.DataCallback;
import com.example.sqlproject.entities.Location;
import com.example.sqlproject.entities.Locations;
import com.example.sqlproject.entities.LocationsDataCallback;
import com.example.sqlproject.entities.Trees;
import com.example.sqlproject.entities.Users;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class TreeDataPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private ImageView treeImageView;
    private TextView tvStock;
    private EditText etType, etPrice, etStock, etImageUrl;
    private Button btnPlant, btnSeeMap, btnUpdateData, btnSaveData, btnCancelSave;
    private TextInputLayout textInputLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private DatabaseReference treesRef;
    private final DecimalFormat df = new DecimalFormat("#.00");
    private final LocationsDataCallback locationDataCallback = new LocationsDataCallback();
    private boolean isILS = true;
    private double usd_Currency;
    private int grayColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tree_data_preview_activity);

        context = this;
        treesRef = FirebaseDatabase.getInstance().getReference("trees");
        grayColor = ContextCompat.getColor(this, R.color.gray);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isILS = extras.getBoolean("isILS");
            usd_Currency = extras.getDouble("usd_Currency");
        }

        fetchInitialData(() -> {
            progressBar.setVisibility(View.GONE);
            initViews();
            loadTreeData();
        });
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

    private void initViews() {
        treeImageView = findViewById(R.id.iVTree);
        tvStock = findViewById(R.id.tvStock);
        etType = findViewById(R.id.etTypeOfTree);
        etPrice = findViewById(R.id.etPriceOfTree);
        etStock = findViewById(R.id.etStockOfTree);
        etImageUrl = findViewById(R.id.etImageUrlOfTree);
        etType.setEnabled(false);
        etPrice.setEnabled(false);

        textInputLayout = findViewById(R.id.inputLayout);
        autoCompleteTextView = findViewById(R.id.inputTV);
        List<String> addresses = Locations.getAddressesOnly(locationDataCallback);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_drop_down, addresses);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            Locations.setChosenLocation(Locations.getLocationByAddress(addresses.get(position)));
            textInputLayout.setErrorEnabled(false);
        });

        btnPlant = findViewById(R.id.btnPlant);
        btnSeeMap = findViewById(R.id.btnSeeMap);
        btnUpdateData = findViewById(R.id.btnUpdateTreeData);
        btnSaveData = findViewById(R.id.btnSaveTreeData);
        btnCancelSave = findViewById(R.id.btnCancelUpdateTree);

        btnPlant.setOnClickListener(this);
        btnSeeMap.setOnClickListener(this);
        btnUpdateData.setOnClickListener(this);
        btnSaveData.setOnClickListener(this);
        btnCancelSave.setOnClickListener(this);

        btnSaveData.setVisibility(View.GONE);
        btnCancelSave.setVisibility(View.GONE);
        etImageUrl.setVisibility(View.GONE);
        etStock.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void loadTreeData() {
        Picasso.get().load(Trees.chosenTree.getImageURL()).into(treeImageView);
        etType.setText(Trees.chosenTree.getType());
        tvStock.setText(Trees.chosenTree.getStock() + " in stock");
        if (isILS) {
            etPrice.setText(Trees.chosenTree.getPrice() + "₪");
        } else {
            etPrice.setText(df.format(Trees.chosenTree.getPrice()) + "$");
        }

        if (Trees.chosenTree.getStock() == 0) {
            btnPlant.setClickable(false);
            btnPlant.setBackgroundColor(grayColor);
        }

        if (!Users.loggedOnUser.isAdmin()) {
            btnUpdateData.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndDisplayLocations();
    }

    public void fetchAndDisplayLocations() {
        Utils.importLocations(new DataCallback<Location>() {
            @Override
            public void onSuccess(List<Location> locations) {
                if (locations != null && !locations.isEmpty()) {
                    List<String> addresses = locations.stream()
                            .map(Location::getAddress)
                            .collect(Collectors.toList());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            TreeDataPreviewActivity.this,
                            R.layout.item_drop_down,
                            addresses
                    );
                    autoCompleteTextView.setAdapter(adapter);
                } else {
                    Log.e("TreeDataPreviewActivity", "No locations fetched.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TreeDataPreviewActivity", "Error fetching locations: " + e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdateTreeData) {
            enableEditMode();
        } else if (v.getId() == R.id.btnSaveTreeData) {
            saveTreeData();
            cancelEditMode();
        } else if (v.getId() == R.id.btnCancelUpdateTree) {
            cancelEditMode();
        } else if (v.getId() == R.id.btnPlant && !autoCompleteTextView.getText().toString().isEmpty()) {
            navigateToPayment();
        } else if (v.getId() == R.id.btnSeeMap && !autoCompleteTextView.getText().toString().isEmpty()) {
            MapViewDialogBoxActivity mapViewDialogBoxActivity = new MapViewDialogBoxActivity(this);
            mapViewDialogBoxActivity.show();
        } else
            textInputLayout.setError("Please select an address");
    }

    private void navigateToPayment() {
        Intent intent = new Intent(context, PaymentForTreesActivity.class);
        intent.putExtra("isILS", isILS);
        intent.putExtra("usd_Currency", usd_Currency);

        context.startActivity(intent);
    }

    private void enableEditMode() {
        textInputLayout.setErrorEnabled(false);
        etType.setEnabled(true);
        etPrice.setEnabled(true);
        tvStock.setVisibility(View.GONE);
        btnPlant.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        btnSeeMap.setVisibility(View.GONE);
        btnUpdateData.setVisibility(View.GONE);

        etImageUrl.setVisibility(View.VISIBLE);
        etStock.setVisibility(View.VISIBLE);
        btnSaveData.setVisibility(View.VISIBLE);
        btnCancelSave.setVisibility(View.VISIBLE);

        etType.setText(Trees.chosenTree.getType());
        etPrice.setText(String.valueOf(Trees.chosenTree.getPrice()));
        etStock.setText(String.valueOf(Trees.chosenTree.getStock()));
        etImageUrl.setText(Trees.chosenTree.getImageURL());
    }

    private void cancelEditMode() {
        etType.setEnabled(false);
        etPrice.setEnabled(false);
        etStock.setVisibility(View.GONE);
        etImageUrl.setVisibility(View.GONE);
        btnSaveData.setVisibility(View.GONE);
        btnCancelSave.setVisibility(View.GONE);

        tvStock.setVisibility(View.VISIBLE);
        btnUpdateData.setVisibility(View.VISIBLE);
        btnPlant.setVisibility(View.VISIBLE);
        textInputLayout.setVisibility(View.VISIBLE);
        btnSeeMap.setVisibility(View.VISIBLE);

        loadTreeData();
    }

    private void saveTreeData() {
        String updatedType = etType.getText().toString().trim();
        String updatedPrice = etPrice.getText().toString().trim();
        String updatedStock = etStock.getText().toString().trim();
        String updatedImageUrl = etImageUrl.getText().toString().trim();

        if (validateFields(updatedType, updatedPrice, updatedStock, updatedImageUrl)) {
            Trees.chosenTree.setType(updatedType);
            Trees.chosenTree.setPrice(Double.parseDouble(updatedPrice));
            Trees.chosenTree.setStock(Integer.parseInt(updatedStock));
            Trees.chosenTree.setImageURL(updatedImageUrl);

            treesRef.child(String.valueOf(Trees.chosenTree.getID())).setValue(Trees.chosenTree)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Tree data updated successfully", Toast.LENGTH_SHORT).show();
                        cancelEditMode();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update tree data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private boolean validateFields(String type, String price, String stock, String imageUrl) {
        if (type.isEmpty() || price.isEmpty() || stock.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Double.parseDouble(price);
            Integer.parseInt(stock);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price or stock value", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}