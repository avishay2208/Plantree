package com.example.sqlproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlproject.R;
import com.example.sqlproject.SaveUserToFile;
import com.example.sqlproject.Serialization;
import com.example.sqlproject.entities.User;
import com.example.sqlproject.entities.Users;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignIn, btnSignUp;
    private EditText eTeMail, etPassword;
    private ProgressBar progressBar;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initButtons();
        initEditTexts();
        initProgressBars();
        isUserSavedToFile();

        testFirebaseConnection();
    }

    private void initButtons() {
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void initEditTexts() {
        eTeMail = findViewById(R.id.eTeMailSignIn);
        etPassword = findViewById(R.id.eTpasswordSignIn);
    }

    private void initProgressBars() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void testFirebaseConnection() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("Firebase", "Firebase connection successful");
                } else {
                    Log.d("Firebase", "No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Firebase connection failed: " + error.getMessage());
            }
        });
    }

    public void isUserSavedToFile() {
        if (SaveUserToFile.getValue(MainActivity.this, "SAVED_USER") != null) {
            Users.setLoggedOnUser(SaveUserToFile.getValue(MainActivity.this, "SAVED_USER"));
            navigateToHome();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignIn) {
            handleSignIn();
        } else if (view == btnSignUp) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void handleSignIn() {
        progressBar.setVisibility(View.VISIBLE);
        String email = eTeMail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            eTeMail.setError("Email is required");
            eTeMail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (password.isEmpty() || password.length() < 4) {
            etPassword.setError("Password is invalid");
            etPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String normalizedEmail = email.toLowerCase();

        usersRef.orderByChild("email").equalTo(normalizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    boolean userFound = false;

                    Log.d("Firebase", "Query snapshot: " + snapshot.getValue());

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String json = new Gson().toJson(userSnapshot.getValue());
                        Log.d("Firebase", "User JSON: " + json);
                        User user = Serialization.convertStringJsonToObject(User.class, json);

                        if (user != null) {
                            Log.d("Firebase", "User email: " + user.getEmail());

                            if (user.getPassword().equals(password)) {
                                Users.setLoggedOnUser(user);
                                Toast.makeText(MainActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                                SaveUserToFile.saveString(MainActivity.this, user);
                                userFound = true;
                                break;
                            }
                        } else
                            Log.d("Firebase", "User data missing for key: " + userSnapshot.getKey());
                    }

                    if (!userFound) {
                        showError();
                    }
                } else {
                    showError();
                    Log.d("Firebase", "Query snapshot: " + snapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage() + "\n Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
    }
}
