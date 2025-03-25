package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.sqlproject.entities.Tree;
import com.example.sqlproject.entities.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageTreesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context context;
    private Button btnAddTree, btnEditTree, btnDeleteTree, btnSaveNewTree, btnSaveTreeChanges, btnCancelAdd, btnCancelUpdate ;
    private EditText etAddTreeType, etAddTreeStock, etAddTreePrice, etAddTreeImageUrl;
    private TextInputLayout textInputLayout;
    private AutoCompleteTextView autoCompleteTextView;
    private String treeType, treeImageUrl;
    private int treeStock;
    private double treePrice;
    private DrawerLayout drawerLayout;
    private DatabaseReference treesRef;
    private Tree chosenTree;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_trees_activity);

        context = this;
        treesRef = FirebaseDatabase.getInstance().getReference("trees");

        drawerLayout = findViewById(R.id.drawerLayoutAdminTreesManagement);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbarAdminTreesManagement);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initButtons();
        initEditTexts();
        initTextInput();

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tVUserName);
        TextView navEmail = headerView.findViewById(R.id.textView3);
        navUsername.setText(Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName());
        navEmail.setText(Users.loggedOnUser.getEmail());
    }

    void initButtons() {
        btnAddTree = findViewById(R.id.btnAddTree);
        btnEditTree = findViewById(R.id.btnEditTree);
        btnDeleteTree = findViewById(R.id.btnDeleteTree);
        btnSaveNewTree = findViewById(R.id.btnSaveNewTree);
        btnSaveTreeChanges = findViewById(R.id.btnSaveTreeChanges);
        btnCancelAdd = findViewById(R.id.btnCancelSaveNewTree);
        btnCancelUpdate = findViewById(R.id.btnCancelChangesUpdateTree);
        btnAddTree.setOnClickListener(this);
        btnEditTree.setOnClickListener(this);
        btnDeleteTree.setOnClickListener(this);
        btnSaveNewTree.setOnClickListener(this);
        btnSaveTreeChanges.setOnClickListener(this);
        btnCancelAdd.setOnClickListener(this);
        btnCancelUpdate.setOnClickListener(this);

        btnSaveNewTree.setVisibility(View.GONE);
        btnSaveTreeChanges.setVisibility(View.GONE);
        btnCancelAdd.setVisibility(View.GONE);
        btnCancelUpdate.setVisibility(View.GONE);
    }

    void initEditTexts() {
        etAddTreeType = findViewById(R.id.etAddTreeType);
        etAddTreePrice = findViewById(R.id.etAddTreePrice);
        etAddTreeImageUrl = findViewById(R.id.etAddTreeImageUrl);
        etAddTreeStock = findViewById(R.id.etAddTreeStock);

        etAddTreeType.setVisibility(View.GONE);
        etAddTreeStock.setVisibility(View.GONE);
        etAddTreePrice.setVisibility(View.GONE);
        etAddTreeImageUrl.setVisibility(View.GONE);
    }

    void initTextInput() {
        textInputLayout = findViewById(R.id.inputLayoutManageTrees);
        autoCompleteTextView = findViewById(R.id.inputTVmanageTrees);

        treesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> treeTypes = new ArrayList<>();
                for (DataSnapshot treeSnapshot : snapshot.getChildren()) {
                    Tree tree = treeSnapshot.getValue(Tree.class);
                    if (tree != null) treeTypes.add(tree.getType());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ManageTreesActivity.this, R.layout.item_drop_down, treeTypes);
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedType = (String) parent.getItemAtPosition(position);
                    for (DataSnapshot treeSnapshot : snapshot.getChildren()) {
                        Tree tree = treeSnapshot.getValue(Tree.class);
                        if (tree != null && tree.getType().equals(selectedType)) {
                            chosenTree = tree;
                            textInputLayout.setErrorEnabled(false);
                            break;
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load trees: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(ManageTreesActivity.this, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        if (menuItem.getItemId() == R.id.nav_new_tree_plant) {
            Intent intent = new Intent(ManageTreesActivity.this, TreesListActivity.class);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_planted_trees) {
            Intent intent;
            if (Users.loggedOnUser.isAdmin()) {
                intent = new Intent(ManageTreesActivity.this, PlantsHistoryAdminActivity.class);
            } else {
                intent = new Intent(ManageTreesActivity.this, PlantsHistoryUserActivity.class);
            }
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.nav_account_center) {
            Intent intent = new Intent(ManageTreesActivity.this, InfoUpdateActivity.class);
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
    public void onClick(View v) {
        if (btnAddTree.isPressed()) {
            prepareTreeFormForAdding();
        }

        if (btnSaveNewTree.isPressed()) {
            if (validateTreeInputs()) {
                saveNewTree();
            }
        }

        if (btnSaveTreeChanges.isPressed()) {
            if (validateTreeInputs()) {
                saveTreeUpdates();
            }
        }

        if (btnEditTree.isPressed()) {
            if (chosenTree != null) {
                prepareTreeFormForEditing();
            } else {
                Toast.makeText(this, "Please select a tree to edit", Toast.LENGTH_SHORT).show();
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("Select a tree");
            }
        }

        if (btnCancelAdd.isPressed() || btnCancelUpdate.isPressed()) {
            resetTreeForm();
        }

        if (btnDeleteTree.isPressed()) {
            if (chosenTree != null) {
                deleteTree();
            } else {
                Toast.makeText(this, "Please select a tree to delete", Toast.LENGTH_SHORT).show();
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("Select a tree");
            }
        }
    }

    private void prepareTreeFormForAdding() {
        etAddTreeType.setVisibility(View.VISIBLE);
        etAddTreeStock.setVisibility(View.VISIBLE);
        etAddTreePrice.setVisibility(View.VISIBLE);
        etAddTreeImageUrl.setVisibility(View.VISIBLE);
        btnSaveNewTree.setVisibility(View.VISIBLE);
        btnCancelAdd.setVisibility(View.VISIBLE);

        textInputLayout.setVisibility(View.GONE);
        autoCompleteTextView.setVisibility(View.GONE);
        btnEditTree.setVisibility(View.GONE);
        btnDeleteTree.setVisibility(View.GONE);
        btnAddTree.setVisibility(View.GONE);

        etAddTreeType.setText("");
        etAddTreeStock.setText("");
        etAddTreePrice.setText("");
        etAddTreeImageUrl.setText("");
    }

    private boolean validateTreeInputs() {
        boolean valid = true;

        if (etAddTreeType.getText().toString().isEmpty()) {
            etAddTreeType.setError("Invalid Type");
            etAddTreeType.requestFocus();
            valid = false;
        }
        if (etAddTreeStock.getText().toString().isEmpty()) {
            etAddTreeStock.setError("Invalid Stock");
            etAddTreeStock.requestFocus();
            valid = false;
        }
        if (etAddTreePrice.getText().toString().isEmpty()) {
            etAddTreePrice.setError("Invalid Price");
            etAddTreePrice.requestFocus();
            valid = false;
        }
        if (etAddTreeImageUrl.getText().toString().isEmpty()) {
            etAddTreeImageUrl.setError("Invalid URL");
            etAddTreeImageUrl.requestFocus();
            valid = false;
        }

        return valid;
    }

    private void saveNewTree() {
        treeType = etAddTreeType.getText().toString();
        treeStock = Integer.parseInt(etAddTreeStock.getText().toString());
        treePrice = Double.parseDouble(etAddTreePrice.getText().toString());
        treeImageUrl = etAddTreeImageUrl.getText().toString();

        treesRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newTreeId = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot treeSnapshot : snapshot.getChildren()) {
                        Tree lastTree = treeSnapshot.getValue(Tree.class);
                        if (lastTree != null) {
                            newTreeId = lastTree.getID() + 1;
                        }
                    }
                }

                Tree newTree = new Tree(newTreeId, treeType, treeStock, treePrice, treeImageUrl);
                treesRef.child(String.valueOf(newTreeId)).setValue(newTree)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(ManageTreesActivity.this, "Tree added successfully", Toast.LENGTH_SHORT).show();
                            resetTreeForm();
                            initTextInput();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ManageTreesActivity.this, "Failed to add tree: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageTreesActivity.this, "Failed to retrieve last tree ID: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTreeUpdates() {
        treeType = etAddTreeType.getText().toString();
        treeStock = Integer.parseInt(etAddTreeStock.getText().toString());
        treePrice = Double.parseDouble(etAddTreePrice.getText().toString());
        treeImageUrl = etAddTreeImageUrl.getText().toString();

        Tree updatedTree = new Tree(chosenTree.getID(), treeType, treeStock, treePrice, treeImageUrl);

        treesRef.orderByChild("id").equalTo(chosenTree.getID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot treeSnapshot : snapshot.getChildren()) {
                                treeSnapshot.getRef().setValue(updatedTree)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(getApplicationContext(), "Tree updated successfully", Toast.LENGTH_SHORT).show();
                                            resetTreeForm();
                                            initTextInput();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getApplicationContext(), "Failed to update tree: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Tree not found in the database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void prepareTreeFormForEditing() {
        etAddTreeType.setVisibility(View.VISIBLE);
        etAddTreeStock.setVisibility(View.VISIBLE);
        etAddTreePrice.setVisibility(View.VISIBLE);
        etAddTreeImageUrl.setVisibility(View.VISIBLE);
        btnSaveTreeChanges.setVisibility(View.VISIBLE);
        btnCancelUpdate.setVisibility(View.VISIBLE);

        textInputLayout.setVisibility(View.GONE);
        autoCompleteTextView.setVisibility(View.GONE);
        btnEditTree.setVisibility(View.GONE);
        btnDeleteTree.setVisibility(View.GONE);
        btnAddTree.setVisibility(View.GONE);

        etAddTreeType.setText(chosenTree.getType());
        etAddTreeStock.setText(String.valueOf(chosenTree.getStock()));
        etAddTreePrice.setText(String.valueOf(chosenTree.getPrice()));
        etAddTreeImageUrl.setText(chosenTree.getImageURL());
    }

    void resetTreeForm() {
        etAddTreeType.setVisibility(View.GONE);
        etAddTreeStock.setVisibility(View.GONE);
        etAddTreePrice.setVisibility(View.GONE);
        etAddTreeImageUrl.setVisibility(View.GONE);
        btnSaveNewTree.setVisibility(View.GONE);
        btnSaveTreeChanges.setVisibility(View.GONE);
        btnCancelAdd.setVisibility(View.GONE);
        btnCancelUpdate.setVisibility(View.GONE);

        textInputLayout.setVisibility(View.VISIBLE);
        autoCompleteTextView.setVisibility(View.VISIBLE);
        btnEditTree.setVisibility(View.VISIBLE);
        btnDeleteTree.setVisibility(View.VISIBLE);
        btnAddTree.setVisibility(View.VISIBLE);
    }

    private void deleteTree() {
        DeleteTreeDialogBoxActivity deleteTreeDialogBoxActivity = new DeleteTreeDialogBoxActivity(this, chosenTree);
        deleteTreeDialogBoxActivity.show();
        resetTreeForm();
        initTextInput();
    }
}