package com.kamjer.woda.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.alarmactivity.alarm.AlarmReceiver;
import com.kamjer.woda.activity.alarmactivity.alarm.NotificationWorker;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class NotificationHelper {
    public static final String CHANNEL_ID = "channel_id";
    public static final String DRINK_WATER_CHANNEL_NAME = "Drink_water_channel";
    public static final String DRINK_WATER_CHANNEL_DESCRIPTION = "Channel for notifications for app Woda";

    public static final String WORKER_NAME = "NotificationWorker";
    public static final int NOTIFICATION_ID = 1;

    public static void createNotificationChannel(Context context) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, DRINK_WATER_CHANNEL_NAME, importance);
        channel.setDescription(DRINK_WATER_CHANNEL_DESCRIPTION);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void setNotificationAlarm(Context context, LocalDateTime desiredTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, desiredTime.getHour());
        calendar.set(Calendar.MINUTE, desiredTime.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public static void setNotificationRepeatableAlarm(Context context,
                                                      LocalTime desiredTime,
                                                      int hourInterval,
                                                      LocalTime timeConstraintStart,
                                                      LocalTime timeConstraintEnd) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, desiredTime.getHour());
        calendar.set(Calendar.MINUTE, desiredTime.getMinute());
        calendar.set(Calendar.SECOND, 0);

        Data data = new Data.Builder()
                .putString(NotificationWorker.TIME_CONSTRAINT_START, timeConstraintStart.toString())
                .putString(NotificationWorker.TIME_CONSTRAINT_END, timeConstraintEnd.toString())
                .build();

        PeriodicWorkRequest notRec = new PeriodicWorkRequest.Builder(
                    NotificationWorker.class,
                    hourInterval,
                    TimeUnit.HOURS)
                .setInitialDelay(hourInterval, TimeUnit.HOURS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                notRec);
    }

    public static void cancelAlarms(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORKER_NAME);
    }

    public static void showNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.glass)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
