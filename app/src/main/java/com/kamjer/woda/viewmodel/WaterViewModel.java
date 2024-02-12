package com.kamjer.woda.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_TO_DRINK = 1500;
    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;

    private WaterDataRepository repository;

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
        getRepository().setWaterAmountToDrink(test);
    }

    public Disposable loadWaterById(long id, Consumer<Water> onSuccess) {
        return getRepository().getWaterDAO().getWaterById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                );
    }

    public void loadWaterByDate(LocalDate date) {
        setActiveDate(date);
        Disposable waterByDateDisposable =  loadWaterByDate(date, this::setWaterValue);
    }
    public Disposable loadWaterByDate(LocalDate date, Consumer<List<Water>> onSuccess) {
        return getRepository().getWaterDAO().getWaterByDate(date.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, RxJavaPlugins::onError);
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
        Disposable waterDisposable = insertWater(water, () -> addWater(water));
    }
    
    public Disposable insertWater(Water water, Action onSuccess) {
        return getRepository().getWaterDAO().insertWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                );
    }

    public void insertType(Type type) {
        Disposable disposable = insertType(type, aLong -> {
            type.setId(aLong);
            putType(type);
        });
    }

    public Disposable insertType(Type type, Consumer<Long> onSuccess) {
        return getRepository().getWaterDAO().insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                );
    }

    private void putType(Type type) {
        getRepository().putType(type);
    }

    public MutableLiveData<List<Water>> getWater() {
        return getRepository().getWaters();
    }

    public void addWater(Water water) {
        getRepository().addWaterValue(water);
    }

    public void setWaterValue(List<Water> waters) {
        getRepository().getWaters().setValue(waters);
    }

    public List<Water> getWaterValue() {
        return Optional.ofNullable(getRepository().getWaters().getValue()).orElseGet(ArrayList::new);
    }

    public int getWaterAmountToDrink() {
        return Optional.ofNullable(getRepository().getWaterAmountToDrink()).orElse(DEFAULT_WATER_TO_DRINK);
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
        getRepository().setWaterAmountToDrink(waterAmountToDrink);
        getRepository().setWaters(getWaterValue().stream()
                .peek(water -> {
                    water.setWaterToDrink(waterAmountToDrink);
                    this.insertWater(water);
                })
                .collect(Collectors.toList()));
    }

    public void deleteWater(Water water) {
        getRepository().deleteWater(water);
    }

    public Map<String, Type> getWaterTypes() {
        return getRepository().getWaterTypes();
    }

    public void setActiveDate(LocalDate date) {
        WaterDataRepository.getInstance().setActiveDate(date);
    }

    public LocalDate getActiveDate() {
        return getRepository().getActiveDate();
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
