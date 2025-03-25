package com.example.sqlproject.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.Location;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DeleteLocationDialogBoxActivity extends Dialog implements View.OnClickListener {
    private final Context context;
    private Button btnDialogDeleteLocation;
    private final Location chosenLocation;

    public DeleteLocationDialogBoxActivity(@NonNull Context context, Location chosenLocation) {
        super(context);
        this.context = context;
        this.chosenLocation = chosenLocation;
        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_location_dialog_box_activity);
        initButtons();
    }

    void initButtons() {
        btnDialogDeleteLocation = findViewById(R.id.btnDialogDeleteLocation);
        Button btnDialogCancelLocationDelete = findViewById(R.id.btnDialogCancelDeleteLocation);

        btnDialogDeleteLocation.setOnClickListener(this);
        btnDialogCancelLocationDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (btnDialogDeleteLocation.isPressed()) {
            DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("locations");
            locationsRef.child(String.valueOf(chosenLocation.getID())).removeValue()
                    .addOnSuccessListener(unused -> Toast.makeText(context, "Location deleted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            dismiss();
        } else {
            dismiss();
        }
    }
}