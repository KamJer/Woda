package com.kamjer.woda.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

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

    private final CompositeDisposable disposable;

    private final WaterDataRepository waterDataRepository;

    public static final ViewModelInitializer<WaterViewModel> initializer = new ViewModelInitializer<>(
            WaterViewModel.class,
            creationExtras ->
                    new WaterViewModel(new CompositeDisposable(),
                            WaterDataRepository.getInstance())
    );

    public WaterViewModel(CompositeDisposable compositeDisposable, WaterDataRepository waterDataRepository) {
        this.disposable = compositeDisposable;
        this.waterDataRepository = waterDataRepository;
    }

    //    DATABASE OPERATIONS
    public void loadWaterDayWithWatersByDate(LocalDate date) {
        waterDataRepository.loadWaterDayWithWatersByDate(date);
    }

    public void insertWaterDay(WaterDay waterDay, Action onSuccess) {
        disposable.add(waterDataRepository.insertWaterDay(waterDay, onSuccess));
    }

    public void insertWater(Water water) {
        disposable.add(waterDataRepository.insertWater(water, aLong -> {
            water.setId(aLong);
            addWaterInDay(water);
        }));
    }

    public void setWaterDayWithWatersObserver(LifecycleOwner owner, Observer<WaterDayWithWaters> observer) {
        waterDataRepository.getWaterDayWithWatersLivaData().observe(owner, observer);
    }

    public void addWaterInDay(Water water) {
        waterDataRepository.addWaterInDay(water);
    }

    public void removeWaterInDay(Water water) {
        waterDataRepository.removeWaterInDay(water);
    }

    public WaterDayWithWaters getWaterDayWithWatersValue() {
        return waterDataRepository.getWaterDayWithWatersValue();
    }

    public void deleteWater(Water water) {
        disposable.add(waterDataRepository.deleteWater(water, () -> removeWaterInDay(water)));
    }

    public LocalDate getActiveDate() {
        return getWaterDayWithWatersValue().getWaterDay().getDate();
    }

    public void clearDisposable() {
        disposable.clear();
    }

    public void setAllWaterDayWithWatersObserver(LifecycleOwner owner, Observer<List<WaterDayWithWaters>> observer) {
        waterDataRepository.setAllWaterDayWithWatersObserver(owner, observer);
    }

    public void setAllTypesObserver(LifecycleOwner owner, Observer<HashMap<Long, Type>> observer) {
        waterDataRepository.setTypesLiveDataObserver(owner, observer);
    }
}