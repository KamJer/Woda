package com.kamjer.woda.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.activity.calendaractivity.CalendarActivity;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;
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
        WaterDataRepository.getInstance().getWaterDayWithWatersByDate(date);
    }

    public void insertWaterDay(WaterDay waterDay, Action onSuccess) {
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

    public void setWaterDayWithWatersObserver(LifecycleOwner owner, Observer<WaterDayWithWaters> observer) {
        WaterDataRepository.getInstance().getWaterDayWithWatersLivaData().observe(owner, observer);
    }

    public void addWaterInDay(Water water) {
        WaterDataRepository.getInstance().addWaterValue(water);
    }

    public void removeWaterInDay(Water water) {
        WaterDataRepository.getInstance().removeWater(water);
    }

    public WaterDayWithWaters getWaterDayWithWatersValue() {
        return WaterDataRepository.getInstance().getWaterDayWithWatersValue();
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
        return getWaterDayWithWatersValue().getWaterDay().getDate();
    }

    public void clearDisposable() {
        disposable.clear();
    }

    public HashMap<Long, Type> getTypesValue() {
        return WaterDataRepository.getInstance().getWaterTypes();
    }

    public void setAllWaterDayWithWatersObserver(CalendarActivity owner, Observer<List<WaterDayWithWaters>> observer) {
        WaterDataRepository.getInstance().getAllWaterDayWithWatersLiveData().observe(owner, observer);
    }
}
