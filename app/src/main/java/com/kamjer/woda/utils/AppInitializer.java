package com.kamjer.woda.utils;

import android.content.Context;

import com.kamjer.woda.repository.ResourcesRepository;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.SqlRepository;
import com.kamjer.woda.repository.WaterDataRepository;

public class AppInitializer {

    public static void initialize(Context applicationContext) {
        initializeSql(applicationContext);
//      creating database;
        createDatabase(applicationContext);
//      loading preferences
        loadWaterAmount(applicationContext);
        loadActiveNotification(applicationContext);
        loadSelectedNotificationTime(applicationContext);
        loadConstraintNotificationTimeStart(applicationContext);
        loadConstraintNotificationTimeEnd(applicationContext);
    }

    private static void loadConstraintNotificationTimeEnd(Context applicationContext) {
        SharedPreferencesRepository.getInstance().loadConstraintNotificationTimeEnd(applicationContext);
    }

    private static void loadConstraintNotificationTimeStart(Context applicationContext) {
        SharedPreferencesRepository.getInstance().loadConstraintNotificationTimeStart(applicationContext);
    }

    private static void loadSelectedNotificationTime(Context applicationContext) {
        SharedPreferencesRepository.getInstance().loadSelectedNotificationTime(applicationContext);
    }

    private static void loadActiveNotification(Context applicationContext) {
        SharedPreferencesRepository.getInstance().loadActiveNotification(applicationContext);
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
        ResourcesRepository.getInstance().loadDefaultTypes(applicationContext);
    }

    private static void loadWaterAmount(Context applicationContext) {
        SharedPreferencesRepository.getInstance().loadWaterAmount(applicationContext);
    }
}
