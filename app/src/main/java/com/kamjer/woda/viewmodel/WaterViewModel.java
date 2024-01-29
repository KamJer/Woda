package com.kamjer.woda.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Water;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.disposables.Disposable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_TO_DRINK = 1500;
    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;


    public void createDataBase(Context context) {
        WaterDataRepository.getInstance().createWaterDatabase(context);
    }
    
    public void loadWaterAmount(AppCompatActivity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int test = sharedPref.getInt(activity.getString(R.string.water_amount_to_drink), DEFAULT_WATER_TO_DRINK);
        WaterDataRepository.getInstance().setWaterAmountToDrink(test);
    }

    public void loadWaterById(long id) {
        Disposable waterByIdDisposable = WaterDataRepository.getInstance().getWaterDAO().getWaterById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setWater,
                        RxJavaPlugins::onError
                );
    }

    public void loadWaterByDate(LocalDate date) {
        Disposable waterByDateDisposable =  WaterDataRepository.getInstance().getWaterDAO().getWaterByDate(date.toString()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setWater,
                        RxJavaPlugins::onError,
                        () -> setWaterWhenNotFound(date)
                );
    }

    public Disposable loadWaterAll(Consumer<List<Water>> onSuccess) {
        return WaterDataRepository.getInstance().getWaterDAO().getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError);
    }

    public Disposable loadWatersFromMonth(LocalDate date) {
        return WaterDataRepository.getInstance().getWaterDAO().getFromMonth(date.toString()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        waters -> {

                        },
                        RxJavaPlugins::onError);
    }

    public Disposable loadWatersFromMonth(LocalDate date, Consumer<List<Water>> onSuccess) {
        return WaterDataRepository.getInstance().getWaterDAO().getFromMonth(date.toString()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError);
    }

    private void setWaterWhenNotFound(LocalDate date) {
//      getting water amount to drink
        int waterAmountToDrink = getWaterAmountToDrink();
//        creating water
        Water waterCreated = new Water(date, waterAmountToDrink, 0);
//        setting that water to the liveData
        WaterDataRepository.getInstance().getWater().setValue(waterCreated);
//        inserting that water to database
        insertWater(waterCreated);
    }

    public void insertWater(Water water) {
        Disposable waterDisposable   = WaterDataRepository.getInstance().getWaterDAO().insertWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(() ->
                        setWater(water),
                        RxJavaPlugins::onError
                );
    }

    public MutableLiveData<List<Water>> getWatersForMonth() {
        return WaterDataRepository.getInstance().getWatersForMonth();
    }

    public List<Water> getWatersForMonthValue() {
        return Optional.ofNullable(WaterDataRepository.getInstance().getWatersForMonth().getValue()).orElseGet(ArrayList::new);
    }

    public void setWaterForMonthValue(List<Water> waters) {
        WaterDataRepository.getInstance().getWatersForMonth().setValue(waters);
    }


    public MutableLiveData<Water> getWater() {
        return WaterDataRepository.getInstance().getWater();
    }

    public void setWater(Water water) {
        WaterDataRepository.getInstance().getWater().setValue(water);
    }

    public Water getWaterValue() {
        return Optional.ofNullable(WaterDataRepository.getInstance().getWater().getValue()).orElseGet(() -> {
            setWaterWhenNotFound(LocalDate.now());
            return WaterDataRepository.getInstance().getWater().getValue();
        });
    }

    public int getWaterAmountToDrink() {
        return Optional.ofNullable(WaterDataRepository.getInstance().getWaterAmountToDrink()).orElseGet(() -> DEFAULT_WATER_TO_DRINK);
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
        WaterDataRepository.getInstance().setWaterAmountToDrink(waterAmountToDrink);
        Water waterToUpdate = getWaterValue();
        waterToUpdate.setWaterToDrink(waterAmountToDrink);
        setWater(waterToUpdate);
        insertWater(waterToUpdate);
    }
}
