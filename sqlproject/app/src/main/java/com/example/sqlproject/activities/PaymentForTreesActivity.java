package com.example.sqlproject.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.Tree;
import com.example.sqlproject.entities.Trees;

import java.text.DecimalFormat;

public class PaymentForTreesActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etCardHolderID, etCardNumber, etCardDate, etCardCVV;
    private TextView tvTotalPrice;
    private boolean isILS = true;
    private double usd_Currency;
    private final DecimalFormat df = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_for_planting_trees_activity);

        // Retrieve extras from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isILS = extras.getBoolean("isILS");
            usd_Currency = extras.getDouble("usd_Currency");
        }

        initViews();
        displayTotalPrice();
    }

    private void initViews() {
        etCardHolderID = findViewById(R.id.eTcardID);
        etCardNumber = findViewById(R.id.eTcardNumber);
        etCardDate = findViewById(R.id.eTcardDate);
        etCardCVV = findViewById(R.id.eTcardCVV);
        tvTotalPrice = findViewById(R.id.tVtotal);
        Button btnPay = findViewById(R.id.btnPay);

        btnPay.setOnClickListener(this);

        // Add TextWatcher for automatic "/" insertion in card date field
        etCardDate.addTextChangedListener(new TextWatcher() {
            private boolean isEditing;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;  // Prevent infinite loop
                isEditing = true;

                String input = s.toString().replaceAll("[^\\d]", ""); // Remove non-digit characters
                String formatted = "";

                if (input.length() >= 2) {
                    formatted = input.substring(0, 2) + "/";
                    if (input.length() > 2) {
                        formatted += input.substring(2);
                    }
                } else {
                    formatted = input;
                }

                etCardDate.setText(formatted);
                etCardDate.setSelection(formatted.length()); // Move cursor to end

                isEditing = false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayTotalPrice() {
        Tree chosenTree = Trees.chosenTree;
        double treePrice = chosenTree.getPrice();
        if (isILS) {
            tvTotalPrice.setText("Total Price: " + treePrice + "₪");
        } else {
            tvTotalPrice.setText("Total Price: " + df.format(treePrice) + "$");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPay && validatePaymentDetails()) {
            PurchaseDialogBoxActivity purchaseDialogBoxActivity = new PurchaseDialogBoxActivity(this, isILS, usd_Currency);
            purchaseDialogBoxActivity.show();
        }
    }

    private boolean validatePaymentDetails() {
        boolean isValid = true;

        if (etCardHolderID.getText().toString().length() != 9) {
            etCardHolderID.setError("Incorrect ID");
            isValid = false;
        }
        if (etCardNumber.getText().toString().length() != 16) {
            etCardNumber.setError("Incorrect card number");
            isValid = false;
        }
        if (etCardDate.getText().toString().length() != 5) {  // Format should be MM/YY
            etCardDate.setError("Incorrect date format (MM/YY)");
            isValid = false;
        }
        if (etCardCVV.getText().toString().length() != 3) {
            etCardCVV.setError("Incorrect CVV");
            isValid = false;
        }

        return isValid;
    }
}