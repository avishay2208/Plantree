package com.example.sqlproject.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.Tree;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DeleteTreeDialogBoxActivity extends Dialog implements View.OnClickListener {
    private final Context context;
    private Button btnDialogDeleteTree;
    private final Tree chosenTree;

    public DeleteTreeDialogBoxActivity(@NonNull Context context, Tree chosenTree) {
        super(context);
        this.context = context;
        this.chosenTree = chosenTree;
        Objects.requireNonNull(this.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_tree_dialog_box_activity);
        initButtons();
    }

    void initButtons() {
        btnDialogDeleteTree = findViewById(R.id.btnDialogDeleteTree);
        Button btnDialogCancelTreeDelete = findViewById(R.id.btnDialogCancelDeleteTree);

        btnDialogDeleteTree.setOnClickListener(this);
        btnDialogCancelTreeDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (btnDialogDeleteTree.isPressed()) {
            DatabaseReference treesRef = FirebaseDatabase.getInstance().getReference("trees");
            treesRef.child(String.valueOf(chosenTree.getID())).removeValue()
                    .addOnSuccessListener(unused -> Toast.makeText(context, "Tree deleted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete tree: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            dismiss();
        } else {
            dismiss();
        }
    }
}