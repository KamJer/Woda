package com.kamjer.woda.activity.mainactivity.addwaterdialog.recyclerWater.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.kamjer.woda.R;
import com.kamjer.woda.activity.mainactivity.addwaterdialog.view.ImageWaterInGlass;
import com.kamjer.woda.activity.mainactivity.addwaterdialog.recyclerWater.interfacewater.WaterAmountInterface;
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
