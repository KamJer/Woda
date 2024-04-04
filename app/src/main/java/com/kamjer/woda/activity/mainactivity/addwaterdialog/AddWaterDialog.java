package com.kamjer.woda.activity.mainactivity.addwaterdialog;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.activity.mainactivity.addwaterdialog.recyclerWater.adapter.WaterAmountAdapter;
import com.kamjer.woda.repository.WaterDataRepository;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddWaterDialog extends AppCompatActivity {

    private int waterAmount = WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.add_water_dialog);

        String buttonText = getIntent().getStringExtra("buttonText");
        boolean isRemove = getIntent().getBooleanExtra("isRemove", false);

        EditText editTextAddWaterAmount = findViewById(R.id.editTextAddWaterAmount);

        Spinner spinnerTypeSList = findViewById(R.id.spinnerTypeList);

        List<Type> typeNames;

//        TODO:Wrong pass data from parent activity
        if (isRemove) {
//      getting used types in an active day to the list of types
            typeNames = new ArrayList<>(WaterDataRepository.getInstance().getUsedTypes
                    (WaterDataRepository.getInstance().getWaterDayValue().getWaters()));
        } else {
//      converting map of types to the list of types
            typeNames = new ArrayList<>(WaterDataRepository
                    .getInstance()
                    .getWaterTypes()
                    .values());
        }

        ArrayAdapter<Type> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                typeNames);
        spinnerTypeSList.setAdapter(spinnerAdapter);
        
        RecyclerView buttonsRecycler = findViewById(R.id.recyclerButtons);
        buttonsRecycler.setLayoutManager(new GridLayoutManager(this, 5));
        WaterAmountAdapter adapter = new WaterAmountAdapter(this, amount -> {
            waterAmount = amount;
            editTextAddWaterAmount.setText(String.valueOf(amount));
        }, 10);
        buttonsRecycler.setAdapter(adapter);

        Button buttonGetWaterAmount = findViewById(R.id.buttonGetWaterAmount);
        buttonGetWaterAmount.setText(buttonText);
        buttonGetWaterAmount.setOnClickListener(v -> onClickGetWaterAction(v, editTextAddWaterAmount, spinnerTypeSList));
    }

    private void onClickGetWaterAction(View v, EditText editTextAddWaterAmount, Spinner spinnerTypeSList) {
        String waterAmountText = String.valueOf(editTextAddWaterAmount.getText());
        if (!waterAmountText.isEmpty()) {
            waterAmount = Integer.parseInt(waterAmountText);
        }
        getIntent().putExtra("waterAmount", waterAmount);
        getIntent().putExtra("type", Optional.ofNullable((Type) spinnerTypeSList.getSelectedItem()).orElse(new Type("", 0)).getId());
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    }
}
