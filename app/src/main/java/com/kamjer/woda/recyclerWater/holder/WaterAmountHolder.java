package com.kamjer.woda.recyclerWater.holder;

import android.content.Context;
import android.graphics.Path;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.PathParser;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.kamjer.woda.R;
import com.kamjer.woda.activity.addwaterDialog.view.ImageWaterInGlass;
import com.kamjer.woda.recyclerWater.adapter.WaterAmountAdapter;
import com.kamjer.woda.recyclerWater.interfacewater.WaterAmountInterface;
import com.kamjer.woda.viewmodel.WaterViewModel;

public class WaterAmountHolder extends RecyclerView.ViewHolder {

    private TextView textWaterAmount;
    private ImageWaterInGlass waterImage;
    private ShapeableImageView glassImage;
    private WaterAmountInterface waterAmountInterface;

    private int amount = 0;

    public WaterAmountHolder(@NonNull View itemView) {
        super(itemView);
        textWaterAmount = itemView.findViewById(R.id.textAmountWater);
        waterImage = itemView.findViewById(R.id.buttonShapeableWater);
        glassImage = itemView.findViewById(R.id.buttonShapeableGlass);
    }

    public void bind(WaterAmountInterface waterAmountInterface, int amount) {
        this.waterAmountInterface = waterAmountInterface;
        this.amount = amount;
        if (amount<= WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO) {
            glassImage.setImageResource(R.drawable.glass);
        } else {
            glassImage.setImageResource(R.drawable.pitcher);
        }
        waterImage.setAmount(amount);
        textWaterAmount.setText(String.valueOf(amount));
        setGlassButtonAction();
    }

    private void setGlassButtonAction() {
        itemView.setOnClickListener(v -> waterAmountInterface.action(amount));
    }

    public void setWaterAmountInterface(WaterAmountInterface waterAmountInterface) {
        this.waterAmountInterface = waterAmountInterface;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
