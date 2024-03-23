package com.kamjer.woda.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.R;
import com.kamjer.woda.activity.alarmactivity.AlarmActivity;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_TO_DRINK = 1500;
    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;

    private static final String WATER_AMOUNT_TO_DRINK_NAME = "waterAmountToDrink";
    private static final String NOTIFICATIONS_ACTIVE_NAME = "isNotificationsActive";
    private static final String SELECTED_NOTIFICATIONS_TIME_NAME = "selectedNotificationsTime";
    private static final String CONSTRAINT_NOTIFICATIONS_TIME_START_NAME = "constraintNotificationsTimeStart";
    private static final String CONSTRAINT_NOTIFICATIONS_TIME_END_NAME = "constraintNotificationsTimeEnd";

    private WaterDataRepository repository;

    private final CompositeDisposable disposable = new CompositeDisposable();


    public void initialize(Context applicationContext) {
//      creating database;
        createDataBase(applicationContext);
//      loading preferences
        loadWaterAmount(applicationContext);
        loadActiveNotification(applicationContext);
        loadSelectedNotificationTime(applicationContext);
        loadConstraintNotificationTimeStart(applicationContext);
        loadConstraintNotificationTimeEnd(applicationContext);
    }

    private void createDataBase(Context context) {
        getRepository().createWaterDatabase(context);
        getRepository().loadDefaultTypes(context);

        disposable.add(getRepository().getWaterDAO().getAllTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(types -> {
                    getRepository().setWaterTypes(types.stream().collect(Collectors.toMap(
                            Type::getId,
                            type -> type)));
//                  fetching default values
//                  check if all default types are in a database and if some are not insert them
                    WaterDataRepository.containsDefaultTypes(getRepository().getDefaultDrinksTypes(), getRepository().getWaterTypes()).forEach(this::insertType);
                }));
    }

    private void loadWaterAmount(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int waterToDrink = sharedPref.getInt(WATER_AMOUNT_TO_DRINK_NAME, DEFAULT_WATER_TO_DRINK);
        getRepository().setWaterAmountToDrink(waterToDrink);
    }

    public void setWaterAmount(Context context, int waterAmountToDrink) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(WATER_AMOUNT_TO_DRINK_NAME, waterAmountToDrink);
        editor.apply();
        setWaterAmountToDrink(waterAmountToDrink);
    }

    private void loadActiveNotification(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        boolean notificationsActive = sharedPref.getBoolean(NOTIFICATIONS_ACTIVE_NAME, false);
        getRepository().setNotificationsActive(notificationsActive);
    }



    public boolean isNotificationsActive() {
        return getRepository().isNotificationsActive();
    }

    public void setNotificationsActive(Context context, boolean notificationsActive) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(NOTIFICATIONS_ACTIVE_NAME, notificationsActive);
        editor.apply();
        getRepository().setNotificationsActive(notificationsActive);
    }

    private void loadSelectedNotificationTime(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        LocalTime selectedNotificationTime = LocalTime.parse(sharedPref.getString(SELECTED_NOTIFICATIONS_TIME_NAME, LocalTime.now().toString()));
        getRepository().setSelectedNotificationsTime(selectedNotificationTime);
    }

    public void setSelectedNotificationsTime(Context context, LocalTime selectedNotificationsTime) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SELECTED_NOTIFICATIONS_TIME_NAME, selectedNotificationsTime.toString());
        editor.apply();
        getRepository().setSelectedNotificationsTime(selectedNotificationsTime);
    }

    public LocalTime getSelectedNotificationsTime() {
        return getRepository().getSelectedNotificationsTime();
    }

    private void loadConstraintNotificationTimeStart(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        LocalTime constraintNotificationsTimeStart = LocalTime.parse(sharedPref.getString(CONSTRAINT_NOTIFICATIONS_TIME_START_NAME, AlarmActivity.TIME_CONSTRAINT_START_DEFAULT.toString()));
        getRepository().setConstraintNotificationTimeStart(constraintNotificationsTimeStart);
    }

    public void setConstraintNotificationTimeStart(Context context, LocalTime constraintNotificationTimeStart) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CONSTRAINT_NOTIFICATIONS_TIME_START_NAME, constraintNotificationTimeStart.toString());
        editor.apply();
        this.getRepository().setConstraintNotificationTimeStart(constraintNotificationTimeStart);
    }

    public LocalTime getConstraintNotificationTimeStart() {
        return getRepository().getConstraintNotificationTimeStart();
    }

    public void setConstraintNotificationTimeEnd(Context context, LocalTime constraintNotificationTimeEnd) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CONSTRAINT_NOTIFICATIONS_TIME_END_NAME, constraintNotificationTimeEnd.toString());
        editor.apply();
        this.getRepository().setConstraintNotificationTimeEnd(constraintNotificationTimeEnd);
    }

    private void loadConstraintNotificationTimeEnd(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        LocalTime constraintNotificationsTimeEnd = LocalTime.parse(sharedPref.getString(WaterViewModel.CONSTRAINT_NOTIFICATIONS_TIME_END_NAME, AlarmActivity.TIME_CONSTRAINT_END_DEFAULT.toString()));
        getRepository().setConstraintNotificationTimeEnd(constraintNotificationsTimeEnd);
    }

    public LocalTime getConstraintNotificationTimeEnd() {
        return getRepository().getConstraintNotificationTimeEnd();
    }

    public void loadWaterById(long id, Consumer<Water> onSuccess) {
        disposable.add(getRepository().getWaterDAO().getWaterById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public void loadWaterDayWithWatersByDate(LocalDate date) {
        loadWaterDayWithWatersByDate(
                date,
                waterDayWithWaters -> {
                    getRepository().setWatersValue(waterDayWithWaters);
//                    water day is loaded from database by definition is already inserted
                    waterDayWithWaters.getWaterDay().setInserted(true);
                },
                () -> {
                    WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, getWaterAmountToDrink());
                    setWaterValue(waterDayWithWaters);
                });
    }

    public void loadWaterDayWithWatersByDate(LocalDate date, Consumer<WaterDayWithWaters> onSuccess, Action onComplete) {
        disposable.add(getRepository().getWaterDAO().getWaterDayWitWatersByDate(date.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError,
                        onComplete
                ));
    }
    public void loadWatersByDate(LocalDate date, Consumer<List<Water>> onSuccess) {
        disposable.add(getRepository().getWaterDAO().getWaterByDate(date.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, RxJavaPlugins::onError));
    }

    public void loadWaterDayByDate(LocalDate date, Consumer<WaterDay> onSuccess) {
        disposable.add(getRepository().getWaterDAO().getWaterDayByDate(date.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, RxJavaPlugins::onError));
    }

    public void loadAllWaterDayWithWaters(Consumer<List<WaterDayWithWaters>> onSuccess) {
        disposable.add((getRepository().getWaterDAO().getAllWaterDayWitWatersByDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public void loadWaterAll(Consumer<List<Water>> onSuccess) {
        disposable.add(getRepository().getWaterDAO().getAllWaters().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError));
    }

    public void insertWaterDay(WaterDay waterDay) {
        insertWaterDay(waterDay, aLong -> {
//            setting id to waterDay
            waterDay.setId(aLong);
//            setting waterDay to inserted
            waterDay.setInserted(true);
        });
    }

    public void insertWaterDay(WaterDay waterDay, Consumer<Long> onSuccess) {
        disposable.add(getRepository().getWaterDAO().insertWaterDay(waterDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));

    }

    public void insertWater(Water water) {
        insertWater(water, aLong -> {
            water.setId(aLong);
            addWaterInDay(water);
        });
    }
    
    public void insertWater(Water water, Consumer<Long> onSuccess) {
        disposable.add(getRepository().getWaterDAO().insertWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public void insertType(Type type) {
        insertType(type, aLong -> {
                type.setId(aLong);
                putType(type);
            },
                () -> {

                });
    }

    public void insertType(Type type, Consumer<Long> onSuccess, Action action) {
        disposable.add(getRepository().getWaterDAO().insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError,
                        action
                ));
    }

    public void removeType(Type type) {
        removeType(type, () -> getRepository().getWaterTypes().remove(type.getId()));
    }

    public void removeType(Type type, Action action) {
        disposable.add(getRepository().getWaterDAO().deleteType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action));
    }

    private void putType(Type type) {
        getRepository().putType(type);
    }

    public MutableLiveData<WaterDayWithWaters> getWater() {
        return getRepository().getWaters();
    }

    public void addWaterInDay(Water water) {
        getRepository().addWaterValue(water);
    }

    public void removeWaterInDay(Water water) {
        getRepository().removeWaterValue(water);
    }

    public void setWatersInDay(List<Water> watersInDay) {
        WaterDayWithWaters waterDay = getRepository().getWaterDayValue();
        waterDay.setWaters(watersInDay);
        getRepository().getWaters().setValue(waterDay);
    }

    public void setWaterValue(WaterDayWithWaters waters) {
        getRepository().getWaters().setValue(waters);
    }

    public WaterDayWithWaters getWaterValue() {
        return getRepository().getWaterDayValue();
    }

    public int getWaterAmountToDrink() {
        return getRepository().getWaterAmountToDrink();
    }

    public void setWaterAmountToDrink(int waterToDrink) {
        getRepository().setWaterAmountToDrink(waterToDrink);
        insertWaterDay(getRepository().getWaterDayValue().getWaterDay());
    }

    /**
     * Inserts waters from active day to the database (updates them if already there)
     */
    public void insertWatersInADay() {
        getRepository().getWaterDayValue()
                .getWaters()
                .forEach(this::insertWater);
    }

    public void deleteWater(Water water) {
        deleteWater(water, () -> removeWaterInDay(water));
    }

    public void deleteWater(Water water, Action action) {
        disposable.add(getRepository().getWaterDAO().deleteWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        action,
                        RxJavaPlugins.getErrorHandler()
                ));
    }

    public Map<Long, Type> getWaterTypes() {
        return getRepository().getWaterTypes();
    }

    public LocalDate getActiveDate() {
        return getWaterValue().getWaterDay().getDate();
    }

    public WaterDataRepository getRepository() {
        if (repository == null ) {
            repository = WaterDataRepository.getInstance();
        }
        return repository;
    }

    public void setRepository(WaterDataRepository repository) {
        this.repository = repository;
    }

    public void clearDisposable() {
        disposable.clear();
    }

}
