package com.kamjer.woda.activity.alarmactivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.woda.R;
import com.kamjer.woda.utils.NotificationHelper;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AlarmActivity extends AppCompatActivity {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H : mm");

    public static final LocalTime TIME_CONSTRAINT_START_DEFAULT = LocalTime.of(8, 0);
    public static final LocalTime TIME_CONSTRAINT_END_DEFAULT = LocalTime.of(22, 0);

    private SwitchCompat switchCompatNotifications;
    private Button buttonSelectHourStart;
    private Spinner spinnerHourSelectNotification;

    private LocalTime selectedTime;
    private int selectedHourRepeating = 1;

    private LocalTime timeConstraintStart = TIME_CONSTRAINT_START_DEFAULT;
    private LocalTime timeConstraintEnd = TIME_CONSTRAINT_END_DEFAULT;

    private WaterViewModel waterViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);

        if (savedInstanceState == null) {
            selectedTime = waterViewModel.getSelectedNotificationsTime();
            timeConstraintStart = waterViewModel.getConstraintNotificationTimeStart();
            timeConstraintEnd = waterViewModel.getConstraintNotificationTimeEnd();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.alarm_activity_layout);

        switchCompatNotifications = findViewById(R.id.switchNotifications);
        switchCompatNotifications.setChecked(waterViewModel.isNotificationsActive());

        switchCompatNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> setNotificationsActions(isChecked));

        buttonSelectHourStart = findViewById(R.id.buttonSelectHourStart);
        if (waterViewModel.isNotificationsActive()) {
            buttonSelectHourStart.setText(selectedTime.format(TIME_FORMATTER));
        } else {
            selectedTime= LocalTime.now();
            buttonSelectHourStart.setText(selectedTime.format(TIME_FORMATTER));
        }
        spinnerHourSelectNotification = findViewById(R.id.spinnerHourSelectNotification);

        Integer[] hours = new Integer[24];
        for (int i = 0; i < hours.length; i++) {
            hours[i] = i + 1;
        }
        ArrayAdapter<Integer> hourSelectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        spinnerHourSelectNotification.setAdapter(hourSelectAdapter);
        spinnerHourSelectNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHourRepeating = hours[position];
                if (switchCompatNotifications.isChecked()) {
                    NotificationHelper.cancelAlarms(getApplicationContext());
                    resetNotifications();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                do nothing when nothing is selected
            }
        });

        buttonSelectHourStart.setOnClickListener(v -> showTimePickerDialog((view, hour, minute) -> {
            this.selectedTime = LocalTime.of(hour, minute);
            waterViewModel.setSelectedNotificationsTime(getApplicationContext(), selectedTime);
            buttonSelectHourStart.setText(selectedTime.format(TIME_FORMATTER));
            resetNotifications();
        }));

        Button buttonConstraintTimeStart = findViewById(R.id.buttonConstraintTimeStart);
        buttonConstraintTimeStart.setText(timeConstraintStart.format(TIME_FORMATTER));
        buttonConstraintTimeStart.setOnClickListener(v ->
                showTimePickerDialog(buttonConstraintTimeStartAction(buttonConstraintTimeStart)));

        Button buttonConstraintTimeEnd = findViewById(R.id.buttonConstraintTimeEnd);
        buttonConstraintTimeEnd.setText(timeConstraintEnd.format(TIME_FORMATTER));
        buttonConstraintTimeEnd.setOnClickListener(v ->
                showTimePickerDialog(buttonConstraintTimeEndAction(buttonConstraintTimeEnd)
        ));
    }

    private TimePickerDialog.OnTimeSetListener buttonConstraintTimeStartAction(Button buttonConstraintTimeStart) {
        return (view, hourOfDay, minute) -> {
            timeConstraintStart = LocalTime.of(hourOfDay, minute);
            waterViewModel.setConstraintNotificationTimeStart(getApplicationContext(), timeConstraintStart);
            buttonConstraintTimeStart.setText(timeConstraintStart.format(TIME_FORMATTER));
            resetNotifications();
        };
    }

    private TimePickerDialog.OnTimeSetListener buttonConstraintTimeEndAction(Button buttonConstraintTimeEnd) {
        return (view, hourOfDay, minute) -> {
            timeConstraintEnd = LocalTime.of(hourOfDay, minute);
            waterViewModel.setConstraintNotificationTimeEnd(getApplicationContext(), timeConstraintEnd);
            buttonConstraintTimeEnd.setText(timeConstraintEnd.format(TIME_FORMATTER));
            resetNotifications();
        };
    }

    private void setNotificationsActions(boolean isChecked) {
        waterViewModel.setNotificationsActive(getApplicationContext(), isChecked);
        if (isChecked) {
            setNotifications();
        } else {
            NotificationHelper.cancelAlarms(getApplicationContext());
        }
    }

    private void showTimePickerDialog(TimePickerDialog.OnTimeSetListener action) {
        LocalTime currentTime = LocalTime.now();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                action,
                currentTime.getHour(),
                currentTime.getMinute(),
                true);
        timePickerDialog.show();
    }

    private void setNotifications() {
        waterViewModel.setSelectedNotificationsTime(getApplicationContext(), selectedTime);
        NotificationHelper.setNotificationRepeatableAlarm(
                getApplicationContext(),
                selectedTime,
                selectedHourRepeating,
                timeConstraintStart,
                timeConstraintEnd);
    }

    private void resetNotifications() {
        if (switchCompatNotifications.isChecked()) {
            NotificationHelper.cancelAlarms(getApplicationContext());
            setNotifications();
        }
    }
}
