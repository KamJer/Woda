package com.kamjer.woda.activity.addwaterDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.mycalendarview.CalendarViewAdapter;
import com.kamjer.woda.R;
import com.kamjer.woda.recyclerWater.adapter.WaterAmountAdapter;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddWaterDialog extends AppCompatActivity {

    private int waterAmount = WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO;

    public AddWaterDialog() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.add_water_dialog);

        EditText editTextAddWaterAmount = findViewById(R.id.editTextAddWaterAmount);
        
        RecyclerView buttonsRecycler = findViewById(R.id.recyclerButtons);
        buttonsRecycler.setLayoutManager(new GridLayoutManager(this, 5));
        WaterAmountAdapter adapter = new WaterAmountAdapter(this, amount -> {
            waterAmount = amount;
            editTextAddWaterAmount.setText(String.valueOf(amount));
        }, 10);
        buttonsRecycler.setAdapter(adapter);

        Button buttonGetWaterAmount = findViewById(R.id.buttonGetWaterAmount);
        buttonGetWaterAmount.setOnClickListener(v -> onClickGetWaterAction(v, editTextAddWaterAmount));
    }

    private void onClickGetWaterAction(View v, EditText editTextAddWaterAmount) {
        String waterAmountText = String.valueOf(editTextAddWaterAmount.getText());
        if (!waterAmountText.isEmpty()) {
            waterAmount = Integer.parseInt(waterAmountText);
        }
        getIntent().putExtra("waterAmount", waterAmount);
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    private static Path createWaterPath(Path glassPath, float waterLevel) {
        Path waterPath = new Path(glassPath);

        android.graphics.RectF bounds = new android.graphics.RectF();
        glassPath.computeBounds(bounds, true);

        // Interpolate the water level based on the bounds of the glass
        float waterTop = bounds.top + (1 - waterLevel) * bounds.height();

        // Clear the existing path data and move to the starting point
        waterPath.reset();
        waterPath.moveTo(bounds.left, waterTop);

        // Draw the water path
        waterPath.lineTo(bounds.right, waterTop);
        waterPath.lineTo(bounds.right, bounds.bottom);
        waterPath.lineTo(bounds.left, bounds.bottom);
        waterPath.close();

        return waterPath;
    }
}
