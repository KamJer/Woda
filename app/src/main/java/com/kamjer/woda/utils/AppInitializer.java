package com.kamjer.woda.utils;

import android.content.Context;

import com.kamjer.woda.repository.ResourcesRepository;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.SqlRepository;
import com.kamjer.woda.repository.WaterDataRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class AppInitializer {

    private static final CompositeDisposable disposable = new CompositeDisposable();

    public static void initialize(Context applicationContext) {
        initializeSql(applicationContext);
        loadResources(applicationContext);
//      creating database;
        createDatabase(applicationContext);
//      loading preferences
        loadSharedPreferences(applicationContext);
    }

    /**
     * Loads custom sql queries
     * @param applicationContext context of an app
     */
    private static void initializeSql(Context applicationContext) {
        SqlRepository.getInstance().loadSqlQuery(applicationContext);
    }

    /**
     * Creates database, loads default types of water
     * @param applicationContext context of an app
     */
    private static void createDatabase(Context applicationContext) {
        WaterDataRepository.getInstance().createWaterDatabase(applicationContext);
        WaterDataRepository.getInstance().getAllTypes();
    }

    private static void loadSharedPreferences(Context applicationContext) {
        SharedPreferencesRepository.getInstance().loadConstraintNotificationTimeEnd(applicationContext);
        SharedPreferencesRepository.getInstance().loadConstraintNotificationTimeStart(applicationContext);
        SharedPreferencesRepository.getInstance().loadSelectedNotificationTime(applicationContext);
        SharedPreferencesRepository.getInstance().loadActiveNotification(applicationContext);
        SharedPreferencesRepository.getInstance().loadWaterAmount(applicationContext);
        SharedPreferencesRepository.getInstance().loadHourNotificationPeriod(applicationContext);
    }

    private static void loadResources(Context applicationContext) {
        ResourcesRepository.getInstance().loadDefaultTypeDeleteMessage(applicationContext);
        ResourcesRepository.getInstance().loadDefaultTypes(applicationContext);
    }

    /**
     * Clears resources from repositories making sure that there are no memory leaks
     */
    public static void clearAppResources() {
        disposable.clear();
    }
}
