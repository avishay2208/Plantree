package com.example.sqlproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.User;
import com.example.sqlproject.entities.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFirstName, etLastName, eTeMail, etPhoneNumber, etPassword, etConfirmPassword;
    private Button btnSaveChanges, btnDeleteUser;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_update_activity);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        initButtons();
        initEditTexts();
    }

    void initButtons() {
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnSaveChanges.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);
    }

    void initEditTexts() {
        etFirstName = findViewById(R.id.eTfirstNameUpdate);
        etLastName = findViewById(R.id.eTlastNameUpdate);
        eTeMail = findViewById(R.id.eTeMailUpdate);
        etPhoneNumber = findViewById(R.id.eTphoneNumberUpdate);
        etPassword = findViewById(R.id.eTpasswordUpdate);
        etConfirmPassword = findViewById(R.id.eTconfirmPasswordUpdate);

        User loggedUser = Users.loggedOnUser;
        etFirstName.setText(loggedUser.getFirstName());
        etLastName.setText(loggedUser.getLastName());
        eTeMail.setText(loggedUser.getEmail());
        etPhoneNumber.setText(loggedUser.getPhoneNumber());
        etPassword.setText(loggedUser.getPassword());
        etConfirmPassword.setText(loggedUser.getPassword());
    }

    @Override
    public void onClick(View v) {
        if (btnSaveChanges.isPressed()) {
            updateUser();
        }
        if (btnDeleteUser.isPressed()) {
            deleteUser();
        }
    }

    private void updateUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = eTeMail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (validateFields(firstName, lastName, email, phoneNumber, password)) {
            User updatedUser = new User(Users.loggedOnUser.getID(), firstName, lastName, Users.loggedOnUser.isAdmin(), email, password,
                    phoneNumber, Users.loggedOnUser.getPlantCounter(), Users.loggedOnUser.getJoinDate());

            usersRef.child(String.valueOf(updatedUser.getID())).setValue(updatedUser)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Info updated successfully!", Toast.LENGTH_SHORT).show();
                        Users.setLoggedOnUser(updatedUser);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private boolean validateFields(String firstName, String lastName, String email, String phoneNumber, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.contains("@") || !email.contains(".com")) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.length() != 10) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(etConfirmPassword.getText().toString().trim())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void deleteUser() {
        if (Users.loggedOnUser.getID() == 1) {
            Toast.makeText(this, "Cannot delete admin account", Toast.LENGTH_SHORT).show();
        } else {
            DeleteUserDialogBoxActivity deleteUserDialogBoxActivity = new DeleteUserDialogBoxActivity(this, Users.loggedOnUser.getID());
            deleteUserDialogBoxActivity.show();
        }
    }
}