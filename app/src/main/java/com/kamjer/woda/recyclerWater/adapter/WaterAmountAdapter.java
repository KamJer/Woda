package com.kamjer.woda.recyclerWater.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.woda.R;
import com.kamjer.woda.recyclerWater.holder.WaterAmountHolder;
import com.kamjer.woda.recyclerWater.interfacewater.WaterAmountInterface;

public class WaterAmountAdapter extends RecyclerView.Adapter<WaterAmountHolder> {

    private LayoutInflater mInflater;
    private WaterAmountInterface waterAmountInterface;
    private int size;

    public WaterAmountAdapter(Context context, WaterAmountInterface waterAmountInterface, int size) {
        this.waterAmountInterface = waterAmountInterface;
        this.size = size;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public WaterAmountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.button_water_amount_layout, parent, false);
        return new WaterAmountHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaterAmountHolder holder, int position) {
        int adPosition = position + 1;
        int amount = 50;
        if (adPosition == size) {
            amount = 1000;
        } else {
            amount += 50 * adPosition;
        }
        holder.bind(waterAmountInterface, amount);
    }

    @Override
    public int getItemCount() {
        return size;
    }
}
