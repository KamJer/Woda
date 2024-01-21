package com.kamjer.woda.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kamjer.woda.R;
import com.kamjer.woda.viewmodel.WaterViewModel;

public class AddWaterDialog extends AppCompatActivity {

    private int waterAmount = WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO;

    private ActivityResultLauncher<Intent> finishWithResultLauncher;

    public AddWaterDialog() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.add_water_dialog);



        EditText editTextAddWaterAmount = findViewById(R.id.editTextAddWaterAmount);
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
}
