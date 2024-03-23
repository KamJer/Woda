package com.kamjer.woda.activity.alarmactivity.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kamjer.woda.R;
import com.kamjer.woda.utils.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String drinkWaterNotificationTitle = context.getString(R.string.drink_water_notification_title);
        String drinkWaterNotificationMessage = context.getString(R.string.drink_water_notification_message);
        NotificationHelper.showNotification(context, drinkWaterNotificationTitle, drinkWaterNotificationMessage);
    }
}
