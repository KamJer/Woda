package com.kamjer.woda.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.alarmactivity.AlarmActivity;

import java.time.LocalTime;
import java.util.Optional;

public class SharedPreferencesRepository {

    public static final int DEFAULT_WATER_TO_DRINK = 1500;

    private static final String WATER_AMOUNT_TO_DRINK_NAME = "waterAmountToDrink";
    private static final String NOTIFICATIONS_ACTIVE_NAME = "isNotificationsActive";
    private static final String SELECTED_NOTIFICATIONS_TIME_NAME = "selectedNotificationsTime";
    private static final String CONSTRAINT_NOTIFICATIONS_TIME_START_NAME = "constraintNotificationsTimeStart";
    private static final String CONSTRAINT_NOTIFICATIONS_TIME_END_NAME = "constraintNotificationsTimeEnd";
    private static final String HOUR_NOTIFICATION_PERIOD_NAME = "hourNotificationPeriodName";

    public static final LocalTime TIME_CONSTRAINT_START_DEFAULT = LocalTime.of(8, 0);
    public static final LocalTime TIME_CONSTRAINT_END_DEFAULT = LocalTime.of(22, 0);

    private final MutableLiveData<Integer> waterAmountToDrinkMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> notificationsActiveLiveData = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> selectedNotificationsTimeLiveData = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> constraintNotificationTimeStartLiveData = new MutableLiveData<>();
    private final MutableLiveData<LocalTime> constraintNotificationTimeEndLiveData = new MutableLiveData<>();

    private final MutableLiveData<Integer> hourNotificationPeriodLiveData = new MutableLiveData<>();

    private static SharedPreferencesRepository sharedPreferencesRepository;

    public static SharedPreferencesRepository getInstance(){
        if (sharedPreferencesRepository == null) {
            sharedPreferencesRepository = new SharedPreferencesRepository();
        }
        return sharedPreferencesRepository;
    }

    /**
     * Loads water amount to drink from SharedPreferences
     * @param applicationContext context of an app
     */
    public void loadWaterAmount(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        setWaterAmountToDrink(sharedPref.getInt(WATER_AMOUNT_TO_DRINK_NAME, DEFAULT_WATER_TO_DRINK));
    }

    public void setWaterAmountToDrink(Context applicationContext, int waterAmountToDrink) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(WATER_AMOUNT_TO_DRINK_NAME, waterAmountToDrink);
        editor.apply();
        setWaterAmountToDrink(waterAmountToDrink);
    }

    private void setWaterAmountToDrink(int waterAmountToDrink) {
        waterAmountToDrinkMutableLiveData.postValue(waterAmountToDrink);
    }

    public int getWaterAmountToDrink() {
        return Optional.ofNullable(waterAmountToDrinkMutableLiveData.getValue()).orElse(DEFAULT_WATER_TO_DRINK);
    }

    /**
     * Loads if notifications are active from SharedPreferences
     * @param applicationContext context of an app
     */
    public void loadActiveNotification(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        boolean notificationsActive = sharedPref.getBoolean(NOTIFICATIONS_ACTIVE_NAME, false);
        SharedPreferencesRepository.getInstance().setNotificationsActive(notificationsActive);
    }

    public void setNotificationsActive(Context applicationContext, boolean notificationsActive) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NOTIFICATIONS_ACTIVE_NAME, notificationsActive);
        editor.apply();
        setNotificationsActive(notificationsActive);
    }

    private void setNotificationsActive(boolean isNotificationsActive) {
        this.notificationsActiveLiveData.postValue(isNotificationsActive);
    }

    public boolean isNotificationsActive() {
        return Optional.ofNullable(notificationsActiveLiveData.getValue()).orElse(false);
    }

    /**
     * Loads selected time for notifications to start
     * @param applicationContext context of an app
     */
    public void loadSelectedNotificationTime(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        LocalTime selectedNotificationTime = LocalTime.parse(sharedPref.getString(SELECTED_NOTIFICATIONS_TIME_NAME, LocalTime.now().toString()));
        setSelectedNotificationsTime(selectedNotificationTime);
    }

    public void setSelectedNotificationsTime(Context context, LocalTime selectedNotificationsTime) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SELECTED_NOTIFICATIONS_TIME_NAME, selectedNotificationsTime.toString());
        editor.apply();
        setSelectedNotificationsTime(selectedNotificationsTime);
    }

    public LocalTime getSelectedNotificationsTime() {
        return selectedNotificationsTimeLiveData.getValue();
    }

    private void setSelectedNotificationsTime(LocalTime selectedNotificationsTime) {
        this.selectedNotificationsTimeLiveData.postValue(selectedNotificationsTime);
    }

    /**
     * Loads selected start time for notifications to not fire
     * @param applicationContext context of an app
     */
    public void loadConstraintNotificationTimeStart(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        LocalTime constraintNotificationsTimeStart = LocalTime.parse(sharedPref.getString(CONSTRAINT_NOTIFICATIONS_TIME_START_NAME, TIME_CONSTRAINT_START_DEFAULT.toString()));
        setConstraintNotificationTimeStart(constraintNotificationsTimeStart);
    }

    public void setConstraintNotificationTimeStart(Context context, LocalTime constraintNotificationTimeStart) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CONSTRAINT_NOTIFICATIONS_TIME_START_NAME, constraintNotificationTimeStart.toString());
        editor.apply();
        setConstraintNotificationTimeStart(constraintNotificationTimeStart);
    }

    public LocalTime getConstraintNotificationTimeStart() {
        return constraintNotificationTimeStartLiveData.getValue();
    }

    private void setConstraintNotificationTimeStart(LocalTime constraintNotificationTimeStart) {
        this.constraintNotificationTimeStartLiveData.postValue(constraintNotificationTimeStart);
    }

    /**
     * Loads selected end time for notifications to not fire
     * @param applicationContext context of an app
     */
    public void loadConstraintNotificationTimeEnd(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        LocalTime constraintNotificationsTimeEnd = LocalTime.parse(sharedPref.getString(CONSTRAINT_NOTIFICATIONS_TIME_END_NAME, TIME_CONSTRAINT_END_DEFAULT.toString()));
        setConstraintNotificationTimeEnd(constraintNotificationsTimeEnd);
    }

    public void setConstraintNotificationTimeEnd(Context context, LocalTime constraintNotificationTimeEnd) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CONSTRAINT_NOTIFICATIONS_TIME_END_NAME, constraintNotificationTimeEnd.toString());
        editor.apply();
        setConstraintNotificationTimeEnd(constraintNotificationTimeEnd);
    }

    public LocalTime getConstraintNotificationTimeEnd() {
        return constraintNotificationTimeEndLiveData.getValue();
    }

    private void setConstraintNotificationTimeEnd(LocalTime constraintNotificationTimeEnd) {
        this.constraintNotificationTimeEndLiveData.postValue(constraintNotificationTimeEnd);
    }

    public void loadHourNotificationPeriod(Context applicationContext) {
        SharedPreferences sharedPref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int hourNotificationPeriod = sharedPref.getInt(HOUR_NOTIFICATION_PERIOD_NAME, 1);
    }

    public Integer getHourNotificationPeriod() {
        return hourNotificationPeriodLiveData.getValue();
    }

    public void setHourNotificationPeriod(Integer hourNotificationPeriod) {
        hourNotificationPeriodLiveData.postValue(hourNotificationPeriod);
    }

    public MutableLiveData<Integer> getWaterAmountToDrinkLiveData() {
        return waterAmountToDrinkMutableLiveData;
    }

    public MutableLiveData<Boolean> getNotificationsActiveLiveData() {
        return notificationsActiveLiveData;
    }

    public MutableLiveData<LocalTime> getSelectedNotificationsTimeLiveData() {
        return selectedNotificationsTimeLiveData;
    }

    public MutableLiveData<LocalTime> getConstraintNotificationTimeStartLiveData() {
        return constraintNotificationTimeStartLiveData;
    }

    public MutableLiveData<LocalTime> getConstraintNotificationTimeEndLiveData() {
        return constraintNotificationTimeEndLiveData;
    }

    public MutableLiveData<Integer> getHourNotificationPeriodLiveData() {
        return hourNotificationPeriodLiveData;
    }
}
