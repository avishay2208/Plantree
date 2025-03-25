package com.example.sqlproject.activities;

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

import java.util.Objects;

public class LogoutDialogBoxActivity extends Dialog implements View.OnClickListener {

    private final Context context;
    private Button btnDialogLogout;

    public LogoutDialogBoxActivity(@NonNull Context context) {
        super(context);
        this.context = context;

        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_dialog_box_activity);
        initButtons();
    }

    void initButtons() {
        btnDialogLogout = findViewById(R.id.btnDialogLogout);
        Button btnDialogCancel = findViewById(R.id.btnDialogCancelLogOut);

        btnDialogLogout.setOnClickListener(this);
        btnDialogCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (btnDialogLogout.isPressed()) {
            Users.userLogout(context);
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            dismiss();
        }
        else
            dismiss();
    }
}
