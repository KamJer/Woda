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

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;

//main activity view model
public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;

    private final CompositeDisposable disposable = new CompositeDisposable();

//    DATABASE OPERATIONS
    public void loadWaterDayWithWatersByDate(LocalDate date) {
        WaterDataRepository.getInstance().loadWaterDayWithWatersByDate(date);
    }

    public void insertWaterDay(WaterDay waterDay, Action onSuccess) {
        disposable.add(WaterDataRepository.getInstance().insertWaterDay(waterDay, onSuccess));
    }

    public void insertWater(Water water) {
        disposable.add(WaterDataRepository.getInstance().insertWater(water, aLong -> {
            water.setId(aLong);
            addWaterInDay(water);
        }));
    }

    public void setWaterDayWithWatersObserver(LifecycleOwner owner, Observer<WaterDayWithWaters> observer) {
        WaterDataRepository.getInstance().getWaterDayWithWatersLivaData().observe(owner, observer);
    }

    public void addWaterInDay(Water water) {
        WaterDataRepository.getInstance().addWaterInDay(water);
    }

    public void removeWaterInDay(Water water) {
        WaterDataRepository.getInstance().removeWaterInDay(water);
    }

    public WaterDayWithWaters getWaterDayWithWatersValue() {
        return WaterDataRepository.getInstance().getWaterDayWithWatersValue();
    }

    public void deleteWater(Water water) {
        disposable.add(WaterDataRepository.getInstance().deleteWater(water, () -> removeWaterInDay(water)));
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
