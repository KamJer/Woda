package com.kamjer.woda.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.alarmactivity.alarm.AlarmReceiver;
import com.kamjer.woda.activity.alarmactivity.alarm.NotificationWorker;
import com.kamjer.woda.activity.mainactivity.MainActivity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
        int initialDelay = hourInterval * 3600000;
//        defining time for initial delay
        long testMilis = 0;
//        get delta time (if desiredTime is in a future dTime is positive, if it is in a past it is negative)
        long dTime = LocalTime.now().until(desiredTime, ChronoUnit.MILLIS);
        if (dTime > 0) {
//            if dTime is in a future time remaining is how long app should wait before firing first notification
            testMilis = dTime;
        } else {
//            getting positive value from dTime
            dTime *= -1;
//            finding out when is next notification
            double multi = (double) dTime / initialDelay;
            double multiTest = (int) Math.ceil(multi);
//            difference between what part of a period we are on and the end of ot
            double test = multiTest - multi;
//            calculating using the difference to calculating how mach time is left
            testMilis = (int) (test * initialDelay);
        }

        Toast.makeText(context, String.valueOf(testMilis), Toast.LENGTH_SHORT).show();

        Data data = new Data.Builder()
                .putString(NotificationWorker.TIME_CONSTRAINT_START, timeConstraintStart.toString())
                .putString(NotificationWorker.TIME_CONSTRAINT_END, timeConstraintEnd.toString())
                .build();

        PeriodicWorkRequest notRec = new PeriodicWorkRequest.Builder(
                    NotificationWorker.class,
                    hourInterval,
                    TimeUnit.HOURS)
                .setConstraints(Constraints.NONE)
                .setInitialDelay(testMilis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                notRec);
    }

    public static void cancelAlarms(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORKER_NAME);
    }

    public static void showNotification(Context context, String title, String message) {
        // Create an Intent for the activity you want to start.
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the TaskStackBuilder and add the intent, which inflates the back
        // stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack.
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.glass)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
