package com.kamjer.woda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.woda.activity.AddWaterDialog;
import com.kamjer.woda.activity.CalendarActivity;
import com.kamjer.woda.activity.UserActivity;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.viewmodel.WaterDataRepository;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.time.LocalDate;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MainActivity extends AppCompatActivity {
    private WaterViewModel waterViewModel;

    private ActivityResultLauncher<Intent> startYourDialogLauncher;
    private ActivityResultLauncher<Intent> calendarActivityLauncher;

    private Button calendarButton;
    private Button userButton;
    private Button addWaterDrankButton;
    private TextView textViewDate;
    private TextView textViewWaterToDrink;
    private ProgressBar progressBarWaterDrank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      creating View model and setting data base to it
        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);
//      creating database
        waterViewModel.createDataBase(this);
//      loading waterSaved
        waterViewModel.loadWaterAmount(this);

//      handling response from addWaterDialog
        startYourDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int resultData = data.getIntExtra("waterAmount", WaterViewModel.DEFAULT_WATER_TO_DRINK);
                            Water waterToUpdate = waterViewModel.getWaterValue();
                            waterToUpdate.setWaterDrank(waterToUpdate.getWaterDrank() + resultData);
                            waterViewModel.setWater(waterToUpdate);
                            waterViewModel.insertWater(waterToUpdate);
                        }
                    }
                });

//      creating behavior for error in a RxJava
        RxJavaPlugins.setErrorHandler(e -> Log.e("errorHandler", e.getMessage()));

//      creating observer for liveData in a ViewModel
        waterViewModel.getWater().observe(this, water -> {
            progressBarWaterDrank.setMax(water.getWaterToDrink());
            progressBarWaterDrank.setProgress(water.getWaterDrank());
            String waterStatus = water.getWaterDrank() + " / " + water.getWaterToDrink();
            textViewWaterToDrink.setText(waterStatus);
            textViewDate.setText(water.getDate().toString());
        });

//      loading data for today
        waterViewModel.loadWaterByDate(LocalDate.now());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.main_activity_layout);

//      handling UI elements
        calendarButton = findViewById(R.id.buttonCalendar);
        calendarButton.setOnClickListener(this::onClickCalendarButtonAction);

        textViewDate = findViewById(R.id.textViewDate);

        userButton = findViewById(R.id.buttonUser);
        userButton.setOnClickListener(this::onClickUserButtonAction);

        addWaterDrankButton = findViewById(R.id.buttonAddWaterDrank);
        addWaterDrankButton.setOnClickListener(this::onClickAddWaterDrankAction);

        textViewWaterToDrink = findViewById(R.id.textViewWaterToDrink);

        progressBarWaterDrank = findViewById(R.id.progressBarWaterDrank);
    }

    /**
     * Action for a calendar button
     * @param v view returned from setOnClickListener(...)
     */
    private void onClickCalendarButtonAction(View v) {
        Intent calendarIntent = new Intent(this, CalendarActivity.class);
        this.startActivity(calendarIntent);
    }

    /**
     * Action for a user button
     * @param v view returned from setOnClickListener(...)
     */
    private void onClickUserButtonAction(View v) {
        Intent userIntent = new Intent(this, UserActivity.class);
        this.startActivity(userIntent);
    }

    /**
     * Action for adding water button
     * @param v view returned from setOnClickListener(...)
     */
    private  void onClickAddWaterDrankAction(View v) {
//        TODO: add action
        Intent addWaterToDrinkIntent = new Intent(this, AddWaterDialog.class);
        startYourDialogLauncher.launch(addWaterToDrinkIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        waterViewModel.getWater().removeObservers(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("tester", String.valueOf(waterViewModel.getWater().hasObservers()));
        waterViewModel.getWater().observe(this, water -> {
            progressBarWaterDrank.setMax(water.getWaterToDrink());
            progressBarWaterDrank.setProgress(water.getWaterDrank());
            String waterStatus = water.getWaterDrank() + " / " + water.getWaterToDrink();
            textViewWaterToDrink.setText(waterStatus);
            textViewDate.setText(water.getDate().toString());
        });
    }
}