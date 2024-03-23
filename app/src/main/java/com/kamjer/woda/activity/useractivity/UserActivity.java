package com.kamjer.woda.activity.useractivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamjer.woda.R;
import com.kamjer.woda.activity.useractivity.coloreditdialog.ColorPickerDialog;
import com.kamjer.woda.activity.useractivity.coloreditdialog.ColorSelectorAction;
import com.kamjer.woda.activity.useractivity.typeView.TypeListViewAdapter;
import com.kamjer.woda.activity.useractivity.typeView.TypeNameChangedAction;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.util.ArrayList;
import java.util.Optional;

public class UserActivity extends AppCompatActivity {

    private EditText textViewWaterAmount;
    private WaterViewModel waterViewModel;

    private TypeListViewAdapter adapter;

    private ColorSelectorAction colorSelectorAction;
    private ColorSelectorAction buttonRemoveTypeAction;

    private TypeNameChangedAction typeNameChangedAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);

        buttonRemoveTypeAction = (type, position) -> {
            waterViewModel.removeType(type);
            adapter.removeType(type);
        };

//        dialog for choosing new color
        ActivityResultLauncher<Intent> colorEditStartYourDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String typeName = data.getStringExtra("type");
                            int color = data.getIntExtra("color", Color.BLACK);
                            Type typeToUpdate = Optional.ofNullable(waterViewModel.getWaterTypes()
                                    .get(data.getLongExtra("id", 0)))
                                    .orElse(new Type());
                            typeToUpdate.setColor(color);
                            typeToUpdate.setType(typeName);
                            int position = data.getIntExtra("position", 0);

                            waterViewModel.insertType(typeToUpdate);
                            adapter.getTypeList().set(position, typeToUpdate);
//                          notifying parent activity that new color was selected and it needs to be changed
                            adapter.notifyItemChanged(position);
                        }
                    }
                });

        colorSelectorAction = (type, position) -> {
            Intent colorPickerDialog = new Intent(this, ColorPickerDialog.class);
            colorPickerDialog.putExtra("id", type.getId());
            colorPickerDialog.putExtra("type", type.getType());
            colorPickerDialog.putExtra("color", type.getColor());
            colorPickerDialog.putExtra("position", position);
            colorEditStartYourDialogLauncher.launch(colorPickerDialog);
        };

        typeNameChangedAction = (type, newName) -> {
            type.setType(String.valueOf(newName));
            addTypeAction(type);
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.user_activity_layout);

        FloatingActionButton floatingActionButtonSetWaterAmount = findViewById(R.id.floatingActionButtonSetWaterAmount);
        floatingActionButtonSetWaterAmount.setOnClickListener(this::floatingActionButtonSetWaterAmountAction);

        ImageButton addWaterTypeButton = findViewById(R.id.imageButtonAddWaterType);

        addWaterTypeButton.setOnClickListener(v -> addNewTypeAction());

        textViewWaterAmount = findViewById(R.id.editTextNumber);
        textViewWaterAmount.setText(String.valueOf(waterViewModel.getWaterAmountToDrink()));

        adapter = new TypeListViewAdapter(
                new ArrayList<>(waterViewModel.getWaterTypes().values()), colorSelectorAction, buttonRemoveTypeAction, typeNameChangedAction);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView typeListRecycler = findViewById(R.id.recyclerViewTypeList);
        typeListRecycler.setLayoutManager(layoutManager);
        typeListRecycler.setAdapter(adapter);
    }

    private void floatingActionButtonSetWaterAmountAction(View view) {
        int waterAmountToDrink = Integer.parseInt(textViewWaterAmount.getText().toString());
        waterViewModel.setWaterAmount(getApplicationContext(), waterAmountToDrink);
        this.finish();
    }

    private void addNewTypeAction() {
        Type newType = new Type("", Color.BLACK);
        adapter.addType(newType);
    }

    private void addTypeAction(Type newType){
        waterViewModel.insertType(newType);
    }
}
