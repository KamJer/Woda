package com.kamjer.woda.activity.alarmactivity.alarm;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kamjer.woda.R;
import com.kamjer.woda.utils.NotificationHelper;

import java.time.LocalTime;
import java.util.Calendar;

public class NotificationWorker extends Worker {

    public static final String TIME_CONSTRAINT_START = "timeConstraintStart";
    public static final String TIME_CONSTRAINT_END = "timeConstraintEnd";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        LocalTime timeConstraintStart = LocalTime.parse(getInputData().getString(TIME_CONSTRAINT_START));
        LocalTime timeConstraintEnd = LocalTime.parse(getInputData().getString(TIME_CONSTRAINT_END));
        if (LocalTime.now().isBefore(timeConstraintEnd) && LocalTime.now().isAfter(timeConstraintStart)) {
            NotificationHelper.createNotificationChannel(getApplicationContext());

            String drinkWaterNotificationTitle = getApplicationContext().getString(R.string.drink_water_notification_title);
            String drinkWaterNotificationMessage = getApplicationContext().getString(R.string.drink_water_notification_message);
            NotificationHelper.showNotification(getApplicationContext(), drinkWaterNotificationTitle, drinkWaterNotificationMessage);
        }
        return Result.success();
    }
}
