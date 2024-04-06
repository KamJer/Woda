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
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.WaterDataRepository;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AddWaterDialog extends AppCompatActivity {

    private int waterAmount = WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO;

    public static final String IS_REMOVE_NAME = "isRemove";
    public static final String ACTIVE_WATER_DAY_WITH_WATERS_NAME = "activeWaterDayWithWaters";
    public static final String WATER_TYPES_NAME = "waterTypes";

    public static final String TYPE_NAME = "type";
    public static final String WATER_AMOUNT_NAME = "waterAmount";

    private boolean isRemove;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.add_water_dialog);

        String buttonText;
        isRemove = getIntent().getBooleanExtra(IS_REMOVE_NAME, false);
        WaterDayWithWaters activeWaterDayWithWaters = Optional
                .ofNullable((WaterDayWithWaters) getIntent().getSerializableExtra(ACTIVE_WATER_DAY_WITH_WATERS_NAME))
                .orElse(new WaterDayWithWaters(LocalDate.now(), SharedPreferencesRepository.DEFAULT_WATER_TO_DRINK));
        HashMap<Long, Type> waterTypes = Optional
                .ofNullable((HashMap<Long, Type>) getIntent().getSerializableExtra(WATER_TYPES_NAME))
                .orElse(new HashMap<>());

        EditText editTextAddWaterAmount = findViewById(R.id.editTextAddWaterAmount);

        Spinner spinnerTypeSList = findViewById(R.id.spinnerTypeList);

        List<Type> typeNames;

        if (isRemove) {
            buttonText = getResources().getString(R.string.remove_water_button_text);
//      getting used types in an active day to the list of types
            typeNames = new ArrayList<>(WaterDataRepository.getUsedTypes
                    (waterTypes, activeWaterDayWithWaters.getWaters()));
        } else {
            buttonText = getResources().getString(R.string.add_water_button_text);
//      converting map of types to the list of types
            typeNames = new ArrayList<>(waterTypes.values());
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
        if (isRemove) {
            waterAmount *= -1;
        }
        getIntent().putExtra(WATER_AMOUNT_NAME, waterAmount);

        if (spinnerTypeSList.getSelectedItem() != null) {
            getIntent().putExtra(TYPE_NAME, (Type) spinnerTypeSList.getSelectedItem());
            setResult(Activity.RESULT_OK, getIntent());
        }
        this.finish();
    }
}
