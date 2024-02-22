package com.kamjer.woda.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.SerialDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_TO_DRINK = 1500;
    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;

    private WaterDataRepository repository;

    private final SerialDisposable disposable = new SerialDisposable();

    public void createDataBase(Context context) {
        getRepository().createWaterDatabase(context);
        Disposable disposable = getRepository().getWaterDAO().getAllTypes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(types -> {
                    WaterDataRepository.getInstance().setWaterTypes(types.stream().collect(Collectors.toMap(
                            Type::getType,
                            type -> type)));
//                  if this map is empty it means something went wrong and values that are supposed to be in a database are not,
//                  fetching default values and inserting them there to be sure they are there
                    if (WaterDataRepository.getInstance().getWaterTypes().isEmpty()) {
                        Arrays.stream(WaterDataRepository.DEFAULT_DRINKS_TYPES).
                                forEach(s -> this.insertType(new Type(s)));
                    }
                });
    }

    public void loadWaterAmount(AppCompatActivity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int test = sharedPref.getInt(activity.getString(R.string.water_amount_to_drink), DEFAULT_WATER_TO_DRINK);
        getWaterValue().setWaterAmountToDrink(test);
    }

    public void loadWaterById(long id, Consumer<Water> onSuccess) {
        disposable.set(getRepository().getWaterDAO().getWaterById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public void loadWaterByDate(LocalDate date) {
        loadWaterByDate(date, waters -> {
            WaterDay waterDay = new WaterDay();
            waterDay.setDate(date);
            waterDay.setWater(waters);
//            TODO: save waterDay to the database in a new Table where water
//            if there are already waters in this day in a database get water amount from them
            if (waters.size()>0) {
                waterDay.setWaterAmountToDrink(waters.get(0).getWaterToDrink());
            } else {
                waterDay.setWaterAmountToDrink(getWaterAmountToDrink());
            }
            setWaterValue(waterDay);
        });
    }
    public void loadWaterByDate(LocalDate date, Consumer<List<Water>> onSuccess) {
        disposable.set(getRepository().getWaterDAO().getWaterByDate(date.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, RxJavaPlugins::onError));
    }

    public Disposable loadWaterAll(Consumer<List<Water>> onSuccess) {
        return getRepository().getWaterDAO().getAllWaters().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError);
    }

    public Disposable loadWatersFromMonth(LocalDate date, Consumer<List<Water>> onSuccess) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedDate = date.format(formatter);
        return getRepository().getWaterDAO().getWatersFromMonth(formattedDate).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError);
    }

    public void insertWater(Water water) {
        insertWater(water, aLong -> {
            water.setId(aLong);
            addWaterInDay(water);
        });
    }
    
    public void insertWater(Water water, Consumer<Long> onSuccess) {
        disposable.set(getRepository().getWaterDAO().insertWater(water).subscribeOn(Schedulers.io())
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
        });
    }

    public void insertType(Type type, Consumer<Long> onSuccess) {
        disposable.set(getRepository().getWaterDAO().insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    private void putType(Type type) {
        getRepository().putType(type);
    }

    public MutableLiveData<WaterDay> getWater() {
        return getRepository().getWaters();
    }

    public void addWaterInDay(Water water) {
        getRepository().addWaterValue(water);
    }

    public void removeWaterInDay(Water water) {
        getRepository().removeWaterValue(water);
    }

    public void setWatersInDay(List<Water> watersInDay) {
        WaterDay waterDay = getRepository().getWaterDayValue();
        waterDay.setWater(watersInDay);
        getRepository().getWaters().setValue(waterDay);
    }

    public void setWaterValue(WaterDay waters) {
        getRepository().getWaters().setValue(waters);
    }

    public WaterDay getWaterValue() {
        return Optional.ofNullable(getRepository().getWaters().getValue()).orElseGet(WaterDay::new);
    }

    public int getWaterAmountToDrink() {
        return Optional.ofNullable(getRepository().getWaterDayValue().getWaterAmountToDrink()).orElse(DEFAULT_WATER_TO_DRINK);
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
        getRepository().getWaterDayValue().setWaterAmountToDrink(waterAmountToDrink);
        insertWatersInADay();
    }

    /**
     * Inserts waters from active day to the database (updates them if already there)
     */
    public void insertWatersInADay() {
        getRepository().getWaterDayValue()
                .getWater()
                .forEach(water -> {
                    insertWater(water);
                });
    }

    public void deleteWater(Water water) {
        deleteWater(water, () -> removeWaterInDay(water));
    }

    public void deleteWater(Water water, Action action) {
        disposable.set(getRepository().getWaterDAO().deleteWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        action,
                        RxJavaPlugins.getErrorHandler()
                ));
    }

    public Map<String, Type> getWaterTypes() {
        return getRepository().getWaterTypes();
    }

    public LocalDate getActiveDate() {
        return getWaterValue().getDate();
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
}
