package com.example.healthmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipsViewHolder> {

    List<TipModel> tipsList;

    public TipsAdapter(List<TipModel> tipsList) {
        this.tipsList = tipsList;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tip, parent, false);

        return new TipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {

        TipModel tip = tipsList.get(position);

        holder.imgTip.setImageResource(tip.getImage());
    }

    @Override
    public int getItemCount() {
        return tipsList.size();
    }

    public static class TipsViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTip;


        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTip = itemView.findViewById(R.id.imgTip);
        }
    }
}