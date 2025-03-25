package com.example.sqlproject.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.Plant;
import com.example.sqlproject.entities.Tree;
import com.example.sqlproject.entities.Trees;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecycleAdapterPlantsAdmin extends RecyclerView.Adapter<RecycleAdapterPlantsAdmin.MyViewHolder> {
    private final ArrayList<Plant> plants;
    private final RecycleViewClickListener listener;

    public RecycleAdapterPlantsAdmin(ArrayList<Plant> plants, RecycleViewClickListener listener) {
        this.plants = plants != null ? plants : new ArrayList<>();
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvPlantID;
        private final TextView tvTreeType;
        private final TextView tvTreeID;
        private final TextView tvUserName;
        private final TextView tvUserID;
        private final TextView tvPlantDate;
        private final TextView tvPlantAddress;
        private final TextView tvPrice;
        private final ImageView ivTree;
        private final RecycleViewClickListener listener;

        public MyViewHolder(final View view, RecycleViewClickListener listener) {
            super(view);

            tvPlantID = view.findViewById(R.id.tvADMINplantID);
            tvTreeType = view.findViewById(R.id.tvADMINplantTreeType);
            tvTreeID = view.findViewById(R.id.tvADMINplantTreeID);
            tvUserName = view.findViewById(R.id.tvADMINplantUserName);
            tvUserID = view.findViewById(R.id.tvADMINplantUserID);
            tvPlantDate = view.findViewById(R.id.tvADMINplantDate);
            tvPlantAddress = view.findViewById(R.id.tvADMINplantAddress);
            tvPrice = view.findViewById(R.id.tvADMINplantPrice);
            ivTree = view.findViewById(R.id.ivADMINplantTree);
            this.listener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onClick(view, position);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_admin, parent, false);
        return new MyViewHolder(itemView, listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Plant plant = plants.get(position);
        Tree tree = Trees.getTreeByType(plant.getTreeType());

        if (tree == null) {
            Log.e("RecycleAdapterPlantsAdmin", "Tree is unexpectedly null for Plant: " + plant.getTreeType());
            return;
        }

        holder.tvPlantID.setText("Plant ID: " + plant.getPlantID());
        holder.tvTreeType.setText(plant.getTreeType() + " tree");
        holder.tvTreeID.setText("Tree ID: " + plant.getTreeID());
        holder.tvUserName.setText("By: " + plant.getUserName());
        holder.tvUserID.setText("User ID: " + plant.getUserID());
        holder.tvPlantDate.setText(String.valueOf(plant.getPlantDate()));
        holder.tvPlantAddress.setText("At: " + plant.getPlantAddress());
        holder.tvPrice.setText(plant.getPrice() + "₪");

        Picasso.get().load(tree.getImageURL()).into(holder.ivTree);
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public interface RecycleViewClickListener {
        void onClick(View v, int position);
    }
}