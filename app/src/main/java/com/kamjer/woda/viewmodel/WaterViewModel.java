package com.kamjer.woda.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.repository.ResourcesRepository;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.WaterDataRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

//main activity view model
public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;

    private final CompositeDisposable disposable = new CompositeDisposable();

//    DATABASE OPERATIONS
    public void loadWaterDayWithWatersByDate(LocalDate date) {
        loadWaterDayWithWatersByDate(
                date,
                waterDayWithWaters -> {
                    WaterDataRepository.getInstance().setWatersValue(waterDayWithWaters);
//                    water day is loaded from database by definition is already inserted
                    waterDayWithWaters.getWaterDay().setInserted(true);
                },
                () -> {
                    WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, SharedPreferencesRepository.getInstance().getWaterAmountToDrink());
                    setWaterValue(waterDayWithWaters);
                });
    }

    public void loadWaterDayWithWatersByDate(LocalDate date, Consumer<WaterDayWithWaters> onSuccess, Action onComplete) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().getWaterDayWitWatersByDate(date.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError,
                        onComplete
                ));
    }


    public void loadAllWaterDayWithWaters(Consumer<List<WaterDayWithWaters>> onSuccess) {
        disposable.add((WaterDataRepository.getInstance().getWaterDAO()
                .getAllWaterDayWitWatersByDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
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
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().insertWaterDay(waterDay)
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
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().insertWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public MutableLiveData<WaterDayWithWaters> getWater() {
        return WaterDataRepository.getInstance().getWaters();
    }

    public void addWaterInDay(Water water) {
        WaterDataRepository.getInstance().addWaterValue(water);
    }

    public void removeWaterInDay(Water water) {
        WaterDataRepository.getInstance().removeWaterValue(water);
    }

    public void setWaterValue(WaterDayWithWaters waters) {
        WaterDataRepository.getInstance().getWaters().setValue(waters);
    }

    public WaterDayWithWaters getWaterValue() {
        return WaterDataRepository.getInstance().getWaterDayValue();
    }


    public void deleteWater(Water water) {
        deleteWater(water, () -> removeWaterInDay(water));
    }

    public void deleteWater(Water water, Action action) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().deleteWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        action,
                        RxJavaPlugins.getErrorHandler()
                ));
    }

    public LocalDate getActiveDate() {
        return getWaterValue().getWaterDay().getDate();
    }

    public void clearDisposable() {
        disposable.clear();
    }

    public void setWaterDayWithWatersObserver(LifecycleOwner owner, Observer<WaterDayWithWaters> observer) {
        WaterDataRepository.getInstance().getWaters().observe(owner, observer);
    }

    public HashMap<Long, Type> getTypesValue() {
        return WaterDataRepository.getInstance().getWaterTypes();
    }
}
