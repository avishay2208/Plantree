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
import com.example.sqlproject.Utils;
import com.example.sqlproject.entities.DataCallback;
import com.example.sqlproject.entities.Plant;
import com.example.sqlproject.entities.Tree;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleAdapterPlantsUser extends RecyclerView.Adapter<RecycleAdapterPlantsUser.MyViewHolder> {

    private final ArrayList<Plant> plants;
    private final ArrayList<Tree> trees;
    private final RecycleViewClickListener listener;

    public RecycleAdapterPlantsUser(ArrayList<Tree> trees, int userID, RecycleViewClickListener listener) {
        this.plants = new ArrayList<>();
        this.trees = trees != null ? trees : new ArrayList<>();
        this.listener = listener;

        Utils.importPlantsByUserID(userID, new DataCallback<Plant>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Plant> filteredPlants) {
                plants.addAll(filteredPlants);
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RecycleAdapterPlantsUser", "Error loading plants: " + e.getMessage());
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvPlantID;
        private final TextView tvTreeType;
        private final TextView tvTreeID;
        private final TextView tvPlantAddress;
        private final TextView tvPlantDate;
        private final TextView tvPrice;
        private final ImageView ivTree;

        public MyViewHolder(final View view) {
            super(view);

            tvPlantID = view.findViewById(R.id.tvUSERplantID);
            tvTreeType = view.findViewById(R.id.tvUSERplantTreeType);
            tvTreeID = view.findViewById(R.id.tvUSERplantTreeID);
            tvPlantAddress = view.findViewById(R.id.tvUSERplantAddress);
            tvPlantDate = view.findViewById(R.id.tvUSERplantDate);
            tvPrice = view.findViewById(R.id.tvUSERplantPrice);
            ivTree = view.findViewById(R.id.ivUSERplantTree);

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position < plants.size()) {
            Plant plant = plants.get(position);

            int treeIndex = plant.getTreeID();
            Tree tree = (treeIndex >= 0 && treeIndex < trees.size()) ? trees.get(treeIndex) : null;

            holder.tvPlantID.setText("Plant ID: " + plant.getPlantID());
            holder.tvTreeType.setText(plant.getTreeType() + " tree");
            holder.tvTreeID.setText("Tree ID: " + plant.getTreeID());
            holder.tvPlantAddress.setText("At: " + plant.getPlantAddress());
            holder.tvPlantDate.setText(String.valueOf(plant.getPlantDate()));
            holder.tvPrice.setText(plant.getPrice() + "₪");

            if (tree != null && tree.getImageURL() != null && !tree.getImageURL().isEmpty()) {
                Picasso.get().load(tree.getImageURL()).into(holder.ivTree);
            } else {
                holder.ivTree.setImageResource(R.drawable.plantree_logo);
            }
        } else {
            Log.e("RecycleAdapterPlantsUser", "Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public interface RecycleViewClickListener {
        void onClick(View v, int position);
    }
}