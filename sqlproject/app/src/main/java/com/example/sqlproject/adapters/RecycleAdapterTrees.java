package com.example.sqlproject.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqlproject.R;
import com.example.sqlproject.entities.Tree;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RecycleAdapterTrees extends RecyclerView.Adapter<RecycleAdapterTrees.MyViewHolder> {
    private final List<Tree> trees;
    private final boolean isILS;
    private final RecycleAdapterTrees.RecycleViewClickListener listener;
    DecimalFormat df = new DecimalFormat("#.00");

    public RecycleAdapterTrees(List<Tree> trees, boolean isILS, RecycleAdapterTrees.RecycleViewClickListener listener) {
        this.trees = trees;
        this.isILS = isILS;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTrees(List<Tree> newTrees) {
        List<Tree> copyOfNewTrees = new ArrayList<>(newTrees);
        this.trees.clear();
        this.trees.addAll(copyOfNewTrees);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tVType;
        private final TextView tVPrice;
        private final ImageView iVTree;

        public MyViewHolder(final View view) {
            super(view);
            tVType = view.findViewById(R.id.tVTreeType);
            tVPrice = view.findViewById(R.id.tVTreePrice);
            iVTree = view.findViewById(R.id.TreeIV);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecycleAdapterTrees.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tree, parent, false);
        return new RecycleAdapterTrees.MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecycleAdapterTrees.MyViewHolder holder, int position) {
        Tree tree = trees.get(position);
        holder.tVType.setText(tree.getType());
        if (isILS)
            holder.tVPrice.setText(tree.getPrice() + "₪");
        else
            holder.tVPrice.setText(df.format(tree.getPrice()) + "$");
        if (tree.getImageURL() != null && !tree.getImageURL().isEmpty()) {
            Picasso.get()
                    .load(tree.getImageURL())
                    .into(holder.iVTree);
        } else {
            holder.iVTree.setImageResource(R.drawable.plantree_logo);
        }
        Picasso.get().load(tree.getImageURL()).into(holder.iVTree);
    }

    @Override
    public int getItemCount() {
        return trees.size();
    }

    public interface RecycleViewClickListener {
        void onClick(View v, int position);
    }
}