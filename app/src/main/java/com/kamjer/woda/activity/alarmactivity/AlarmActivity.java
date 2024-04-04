package com.kamjer.woda.activity.alarmactivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.woda.R;
import com.kamjer.woda.utils.NotificationHelper;
import com.kamjer.woda.viewmodel.AlarmViewModel;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AlarmActivity extends AppCompatActivity {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H : mm");

    private SwitchCompat switchCompatNotifications;
    private Button buttonSelectHourStart;
    private Button buttonConstraintTimeStart;
    private Button buttonConstraintTimeEnd;

    private Spinner spinnerHourSelectNotification;

//    private LocalTime selectedTime;
    private int selectedHourRepeating = 1;

    private AlarmViewModel alarmViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(AlarmViewModel.class);

        if (savedInstanceState == null) {
//            selectedTime = alarmViewModel.getSelectedNotificationsTimeLiveData();
//            timeConstraintStart = alarmViewModel.getConstraintNotificationTimeStartLiveData();
//            timeConstraintEnd = alarmViewModel.getConstraintNotificationTimeEndLiveData();
        }

        alarmViewModel.isNotificationsActiveObserver(this, isChecked -> {
            switchCompatNotifications.setChecked(isChecked);
        });

        alarmViewModel.setSelectedNotificationsTimeObserver(this, time -> {
            buttonSelectHourStart.setText(time.format(TIME_FORMATTER));
        });

        alarmViewModel.setConstraintNotificationsTimeStartObserver(this, time -> {
            buttonConstraintTimeStart.setText(time.format(TIME_FORMATTER));
        });

        alarmViewModel.setConstraintNotificationsTimeEndObserver(this, time -> {
            buttonConstraintTimeEnd.setText(time.format(TIME_FORMATTER));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.alarm_activity_layout);

        switchCompatNotifications = findViewById(R.id.switchNotifications);

        switchCompatNotifications.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    setNotificationsActions(isChecked);
                });

        buttonSelectHourStart = findViewById(R.id.buttonSelectHourStart);

        if (!alarmViewModel.isNotificationsActive()) {
            alarmViewModel.setSelectedNotificationsTime(getApplicationContext(), LocalTime.now());
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
            alarmViewModel.setSelectedNotificationsTime(getApplicationContext(), LocalTime.of(hour, minute));
            resetNotifications();
        }));

        buttonConstraintTimeStart = findViewById(R.id.buttonConstraintTimeStart);
        buttonConstraintTimeStart.setOnClickListener(v ->
                showTimePickerDialog(buttonConstraintTimeStartAction(buttonConstraintTimeStart)));

        buttonConstraintTimeEnd = findViewById(R.id.buttonConstraintTimeEnd);
        buttonConstraintTimeEnd.setOnClickListener(v ->
                showTimePickerDialog(buttonConstraintTimeEndAction(buttonConstraintTimeEnd)
        ));
    }

    private TimePickerDialog.OnTimeSetListener buttonConstraintTimeStartAction(Button buttonConstraintTimeStart) {
        return (view, hourOfDay, minute) -> {
            LocalTime timeConstraintStart = LocalTime.of(hourOfDay, minute);
            alarmViewModel.setConstraintNotificationTimeStart(getApplicationContext(), timeConstraintStart);
            resetNotifications();
        };
    }

    private TimePickerDialog.OnTimeSetListener buttonConstraintTimeEndAction(Button buttonConstraintTimeEnd) {
        return (view, hourOfDay, minute) -> {
            LocalTime timeConstraintEnd = LocalTime.of(hourOfDay, minute);
            alarmViewModel.setConstraintNotificationTimeEnd(getApplicationContext(), timeConstraintEnd);
            resetNotifications();
        };
    }

    private void setNotificationsActions(boolean isChecked) {
        alarmViewModel.setNotificationsActive(getApplicationContext(), isChecked);
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
        NotificationHelper.setNotificationRepeatableAlarm(
                getApplicationContext(),
                alarmViewModel.getSelectedNotificationsTime(),
                selectedHourRepeating,
                alarmViewModel.getConstraintNotificationTimeStart(),
                alarmViewModel.getConstraintNotificationTimeEnd());
    }

    private void resetNotifications() {
        if (switchCompatNotifications.isChecked()) {
            NotificationHelper.cancelAlarms(getApplicationContext());
            setNotifications();
        }
    }
}
