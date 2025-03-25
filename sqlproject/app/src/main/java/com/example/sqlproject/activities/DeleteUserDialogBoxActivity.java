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
import com.example.sqlproject.entities.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DeleteUserDialogBoxActivity extends Dialog implements View.OnClickListener {
    private final Context context;
    private Button btnDialogDeleteUser;
    private final int userID;

    public DeleteUserDialogBoxActivity(@NonNull Context context, int userID) {
        super(context);
        this.context = context;
        this.userID = userID;
        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_user_dialog_box_activity);
        initButtons();
    }

    void initButtons() {
        btnDialogDeleteUser = findViewById(R.id.btnDialogDeleteUser);
        Button btnDialogCancelUserDelete = findViewById(R.id.btnDialogCancelDeleteUser);

        btnDialogDeleteUser.setOnClickListener(this);
        btnDialogCancelUserDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (btnDialogDeleteUser.isPressed()) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference plantsRef = FirebaseDatabase.getInstance().getReference("plants");

            usersRef.child(String.valueOf(userID)).removeValue()
                    .addOnSuccessListener(unused -> plantsRef.orderByChild("userID").equalTo(userID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                                        plantSnapshot.getRef().removeValue();
                                    }
                                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Failed to delete user's plants: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }))
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            dismiss();
        } else
            dismiss();

        if (Users.loggedOnUser.getID() == userID) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finishAffinity();
        }
    }
}