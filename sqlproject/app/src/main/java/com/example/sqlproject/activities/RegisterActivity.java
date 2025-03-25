package com.example.sqlproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlproject.R;
import com.example.sqlproject.Utils;
import com.example.sqlproject.entities.User;
import com.example.sqlproject.entities.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFirstName, etLastName, etPhoneNumber, etEmail, etPassword, etConfirmPassword;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        initViews();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.eTfirstNameReg);
        etLastName = findViewById(R.id.eTlastNameReg);
        etPhoneNumber = findViewById(R.id.eTphoneNumberReg);
        etEmail = findViewById(R.id.eTeMailReg);
        etPassword = findViewById(R.id.eTpasswordReg);
        etConfirmPassword = findViewById(R.id.eTconfirmPasswordReg);
        Button btnRegister = findViewById(R.id.btnRegReg);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegReg)
            registerUser();
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (validateInputs(firstName, lastName, phoneNumber, email, password, confirmPassword)) {
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(RegisterActivity.this, "This email address is already in use", Toast.LENGTH_SHORT).show();
                        etEmail.requestFocus();
                        etEmail.setError("already in use");
                    } else {
                        usersRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int newUserId = 1;
                                boolean isAdmin = true;

                                if (snapshot.exists()) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        String lastUserId = userSnapshot.getKey();
                                        if (lastUserId != null) {
                                            newUserId = Integer.parseInt(lastUserId) + 1;
                                            isAdmin = false;
                                        }
                                    }
                                }

                                User newUser = new User(
                                        newUserId,
                                        firstName,
                                        lastName,
                                        isAdmin,
                                        email,
                                        password,
                                        phoneNumber,
                                        0,
                                        Utils.getCurrentDate()
                                );

                                usersRef.child(String.valueOf(newUserId)).setValue(newUser)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                                            Users.setLoggedOnUser(newUser);
                                            navigateToHome();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInputs(String firstName, String lastName, String phoneNumber, String email, String password, String confirmPassword) {
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (firstName.length() < 2) {
            etFirstName.requestFocus();
            etFirstName.setError("First name is too short");
            return false;
        }
        if (lastName.length() < 2) {
            etLastName.requestFocus();
            etLastName.setError("Last name is too short");
            return false;
        }
        if (phoneNumber.length() != 10) {
            etPhoneNumber.requestFocus();
            etPhoneNumber.setError("Invalid phone number");
            return false;
        }
        if (!email.contains("@") || !email.contains(".com")) {
            etEmail.requestFocus();
            etEmail.setError("Invalid email");
            return false;
        }
        if (password.length() < 3) {
            etPassword.requestFocus();
            etPassword.setError("Password is too short");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.requestFocus();
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}