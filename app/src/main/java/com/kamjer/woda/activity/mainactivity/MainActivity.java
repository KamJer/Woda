package com.kamjer.woda.activity.mainactivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamjer.woda.R;
import com.kamjer.woda.activity.alarmactivity.AlarmActivity;
import com.kamjer.woda.activity.mainactivity.addwaterdialog.AddWaterDialog;
import com.kamjer.woda.activity.calendaractivity.CalendarActivity;
import com.kamjer.woda.activity.useractivity.UserActivity;
import com.kamjer.woda.activity.mainactivity.listeners.ChangeWatersGestureListener;
import com.kamjer.woda.activity.mainactivity.waterimage.WaterImage;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.utils.AppInitializer;
import com.kamjer.woda.utils.WaterAppErrorHandler;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class MainActivity extends AppCompatActivity {
    private WaterViewModel waterViewModel;

    private ActivityResultLauncher<Intent> addRemoveWaterDrankDialogLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private TextView textViewDate;
    private TextView textViewWaterToDrink;
    private ProgressBar progressBarWaterDrank;
    private WaterImage waterImage;
    
    private GestureDetectorCompat gestureDetector;

    private HashMap<Long, Type> typeHashMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      creating View model and setting data base to it
        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);
//      checking if it is a restart (rotation of a screen) or a app is starting
        if (savedInstanceState == null) {
//          initializing app
            AppInitializer.initialize(getApplicationContext());
//          loading data for today
            waterViewModel.loadWaterDayWithWatersByDate(LocalDate.now());
        }

        gestureDetector = new GestureDetectorCompat(this, new ChangeWatersGestureListener(this, waterViewModel));

//      handling response from WaterDialog
        addRemoveWaterDrankDialogLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int resultData = data.getIntExtra(AddWaterDialog.WATER_AMOUNT_NAME, WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO);
                            Type type = Optional.ofNullable((Type) data.getSerializableExtra(AddWaterDialog.TYPE_NAME))
                                    .orElse(new Type("", Color.BLACK));
                            if (type.getId() != 0) {
                                updateWaterFromDialogs(resultData, type);
                            }
                        }
                    }
                });

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                onClickNotificationActivityButtonAction();
            } else {
                Toast.makeText(this, getString(R.string.text_notification_permission_not_granted), Toast.LENGTH_LONG).show();
            }
        });

//      creating behavior for error in a RxJava
        RxJavaPlugins.setErrorHandler(new WaterAppErrorHandler(this));

        setObservers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.main_activity_layout);

//      handling UI elements
        ImageButton calendarButton = findViewById(R.id.buttonCalendar);
        calendarButton.setOnClickListener(this::onClickCalendarButtonAction);

        textViewDate = findViewById(R.id.textViewDate);

        ImageButton userButton = findViewById(R.id.buttonUser);
        userButton.setOnClickListener(this::onClickUserButtonAction);

        waterImage = findViewById(R.id.imageWater);

        textViewWaterToDrink = findViewById(R.id.textViewWaterToDrink);

        progressBarWaterDrank = findViewById(R.id.progressBarWaterDrank);

        FloatingActionButton addWaterDrankButton = findViewById(R.id.buttonAddWaterDrank);
        addWaterDrankButton.setOnClickListener(this::onClickAddWaterDrankAction);

        FloatingActionButton removeWaterDrankButton = findViewById(R.id.buttonRemoveWaterDrank);
        removeWaterDrankButton.setOnClickListener(this::onClickRemoveWaterDrankAction);

        ImageButton test = findViewById(R.id.buttonNotification);
        test.setOnClickListener(v -> showNotificationsActivityAction());
    }

    public void setObserverOnWaters(WaterDayWithWaters waterDayWithWaters) {
        if (waterDayWithWaters != null) {
            //      handling glass image
            waterImage.setAmount(waterDayWithWaters);
            waterImage.invalidate();
//      handling progress bar
            progressBarWaterDrank.setMax(waterDayWithWaters.getWaterDay().getWaterToDrink());
            int sum = waterDayWithWaters.getWaterDaySum();
            progressBarWaterDrank.setProgress(sum);
            String waterStatus = sum + " / " + waterDayWithWaters.getWaterDay().getWaterToDrink();
            textViewWaterToDrink.setText(waterStatus);
//      handling date on top of a screen
            textViewDate.setText(waterViewModel.getActiveDate().toString());
        }
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
        Intent addWaterToDrinkIntent = new Intent(this, AddWaterDialog.class);
        addWaterToDrinkIntent.putExtra(AddWaterDialog.ACTIVE_WATER_DAY_WITH_WATERS_NAME, waterViewModel.getWaterDayWithWatersValue());
        addWaterToDrinkIntent.putExtra(AddWaterDialog.WATER_TYPES_NAME, typeHashMap);
        addRemoveWaterDrankDialogLauncher.launch(addWaterToDrinkIntent);
    }
    /**
     * Action for removing water button
     * @param v view returned from setOnClickListener(...)
     */
    private  void onClickRemoveWaterDrankAction(View v) {
        Intent removeWaterToDrinkIntent = new Intent(this, AddWaterDialog.class);
        removeWaterToDrinkIntent.putExtra(AddWaterDialog.IS_REMOVE_NAME, true);
        removeWaterToDrinkIntent.putExtra(AddWaterDialog.ACTIVE_WATER_DAY_WITH_WATERS_NAME, waterViewModel.getWaterDayWithWatersValue());
        removeWaterToDrinkIntent.putExtra(AddWaterDialog.WATER_TYPES_NAME, typeHashMap);
        addRemoveWaterDrankDialogLauncher.launch(removeWaterToDrinkIntent);
    }

    private void updateWaterFromDialogs(int result, Type type) {
        List<Water> watersUpdated = waterViewModel.getWaterDayWithWatersValue().getWaters();
        Water waterUpdated = watersUpdated
                .stream()
                .filter(water ->
                        water.getTypeId() == type.getId())
                .findFirst()
                .map(water -> {
                    water.setWaterDrank(water.getWaterDrank() + result);
                    return water;
                })
                .orElseGet(() -> new Water(result, type, waterViewModel.getWaterDayWithWatersValue().getWaterDay())
                );
        if (waterUpdated.getWaterDrank() <= 0) {
            waterViewModel.deleteWater(waterUpdated);
        } else {
//          checking if waterDay is already in a database and if it is don't insert it
            if (!waterViewModel.getWaterDayWithWatersValue().getWaterDay().isInserted()) {
                waterViewModel.insertWaterDay(waterViewModel.getWaterDayWithWatersValue().getWaterDay(), () -> {
//                  sets new id for a waterDay
                    WaterDay waterDayToInsert = waterViewModel.getWaterDayWithWatersValue().getWaterDay();
                    waterDayToInsert.setInserted(true);
//                  setting water day for a water
//                  triggers insertion of a water if insertion of a day was successful
                    waterViewModel.insertWater(waterUpdated);
                });
            } else {
                waterViewModel.insertWater(waterUpdated);
            }
        }
    }

    private void onClickNotificationActivityButtonAction(){
        Intent userIntent = new Intent(this, AlarmActivity.class);
        this.startActivity(userIntent);
    }

    private  void showNotificationsActivityAction() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            onClickNotificationActivityButtonAction();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setObservers() {
        //      creating observer for liveData in a ViewModel
        waterViewModel.setWaterDayWithWatersObserver(this, this::setObserverOnWaters);
        waterViewModel.setAllTypesObserver(this, longTypeHashMap -> {
            typeHashMap = longTypeHashMap;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        waterViewModel.clearDisposable();
        AppInitializer.clearAppResources();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setObservers();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}