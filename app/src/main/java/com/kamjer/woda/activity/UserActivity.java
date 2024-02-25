package com.kamjer.woda.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamjer.woda.R;
import com.kamjer.woda.viewmodel.WaterViewModel;

public class UserActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButtonSetWaterAmount;
    private EditText textViewWaterAmount;
    private WaterViewModel waterViewModel;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.user_activity_layout);

        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);

        floatingActionButtonSetWaterAmount = findViewById(R.id.floatingActionButtonSetWaterAmount);
        floatingActionButtonSetWaterAmount.setOnClickListener(this::floatingActionButtonSetWaterAmountAction);

        textViewWaterAmount = findViewById(R.id.editTextNumber);
        textViewWaterAmount.setText(String.valueOf(waterViewModel.getWaterAmountToDrink()));
    }

    private void floatingActionButtonSetWaterAmountAction(View view) {
        int waterAmountToDrink = Integer.parseInt(textViewWaterAmount.getText().toString());
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.water_amount_to_drink), waterAmountToDrink);
        editor.apply();
        waterViewModel.setWaterToDrink(waterAmountToDrink);
        this.finish();
    }
}
