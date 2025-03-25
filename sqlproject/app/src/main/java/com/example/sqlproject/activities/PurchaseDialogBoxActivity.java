package com.example.sqlproject.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sqlproject.R;
import com.example.sqlproject.SendSms;
import com.example.sqlproject.Utils;
import com.example.sqlproject.entities.Locations;
import com.example.sqlproject.entities.Plant;
import com.example.sqlproject.entities.Trees;
import com.example.sqlproject.entities.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Objects;

public class PurchaseDialogBoxActivity extends Dialog implements View.OnClickListener {
    private final Context context;
    private Button btnDialogClose;
    private DatabaseReference plantsRef, usersRef, treesRef;
    private final DecimalFormat df = new DecimalFormat("#.00");
    private double treePrice;
    private double treePriceInILS;
    private final boolean isILS;
    private final double usd_Currency;

    public PurchaseDialogBoxActivity(@NonNull Context context, boolean isILS, double usd_Currency) {
        super(context);
        this.context = context;
        this.isILS = isILS;
        this.usd_Currency = usd_Currency;

        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_dialog_box);

        plantsRef = FirebaseDatabase.getInstance().getReference("plants");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        treesRef = FirebaseDatabase.getInstance().getReference("trees");
        treePrice = Trees.chosenTree.getPrice();

        treePriceInILS = isILS ? treePrice : treePrice / usd_Currency;

        initButtons();
        processPayment();
    }

    void initButtons() {
        btnDialogClose = findViewById(R.id.btnDialogClose);
        btnDialogClose.setOnClickListener(this);
    }

    private void processPayment() {
        if (usersRef != null && treesRef != null && plantsRef != null) {
            int updatedPlantCounter = Users.loggedOnUser.getPlantCounter() + 1;
            Users.loggedOnUser.setPlantCounter(updatedPlantCounter);
            usersRef.child(String.valueOf(Users.loggedOnUser.getID()))
                    .child("plantCounter").setValue(updatedPlantCounter);

            int updatedTreeStock = Trees.chosenTree.getStock() - 1;
            treesRef.child(String.valueOf(Trees.chosenTree.getID()))
                    .child("stock").setValue(updatedTreeStock);

            plantsRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int newPlantId = 0;
                    if (snapshot.exists()) {
                        for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                            Plant lastPlant = plantSnapshot.getValue(Plant.class);
                            if (lastPlant != null) {
                                newPlantId = lastPlant.getPlantID() + 1;
                            }
                        }
                    }

                    Plant newPlant = new Plant(newPlantId,
                            Users.loggedOnUser.getID(),
                            Users.loggedOnUser.getFirstName() + " " + Users.loggedOnUser.getLastName(),
                            Trees.chosenTree.getID(),
                            Trees.chosenTree.getType(),
                            Locations.chosenLocation.getAddress(),
                            Utils.getCurrentDate(),
                            treePriceInILS);

                    plantsRef.child(String.valueOf(newPlantId)).setValue(newPlant)
                            .addOnSuccessListener(unused -> sendConfirmationSms())
                            .addOnFailureListener(e -> Toast.makeText(context, "Payment failed.\nPlease try again later.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Payment failed.\nPlease try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "Database connection error.\nPlease try again later.", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    private void sendConfirmationSms() {
        String message = "Thank you for your purchase!\nYour " + Trees.chosenTree.getType()
                + " will be planted within one business day at " + Locations.chosenLocation.getAddress()
                + ".\nTotal price: " + df.format(treePrice) + (isILS ? "₪" : "$");

        if (!SendSms.hasSMSPermission((Activity) context)) {
            SendSms.askForPermission((Activity) context);
        }
        SendSms.sendSMS("+972586602669", message, (Activity) context);
    }

    @Override
    public void onClick(View view) {
        if (btnDialogClose.isPressed()) {
            Intent intent = new Intent(context, HomeActivity.class);
            context.startActivity(intent);
            ((Activity) context).finishAffinity();
            dismiss();
        }
    }
}