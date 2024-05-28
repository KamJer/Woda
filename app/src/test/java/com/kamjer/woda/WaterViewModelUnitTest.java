package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.repository.WaterDataRepository;
import com.kamjer.woda.viewmodel.WaterViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;

public class WaterViewModelUnitTest {

    WaterViewModel waterViewModel;

    WaterDataRepository waterDataRepository;

    Type type;
    WaterDay waterDay;

    List<WaterDayWithWaters> allWaterDayWithWaters;
    WaterDayWithWaters waterDayWithWaters;
    CompositeDisposable waterViewModelDisposable;
    @Before
    public void setup() {
        waterDataRepository = Mockito.mock(WaterDataRepository.class);
        waterViewModelDisposable = Mockito.mock(CompositeDisposable.class);

        waterViewModel = new WaterViewModel(waterViewModelDisposable, waterDataRepository);

        type = new Type("Water", Color.BLACK);

        waterDay = new WaterDay(LocalDate.now(), 1500, true);
        List<Water> waters = new ArrayList<>();
        waters.add(new Water(1200, type, waterDay));
        waters.add(new Water(1500, type, waterDay));
        waterDayWithWaters = new WaterDayWithWaters(waterDay, waters);

        allWaterDayWithWaters = new ArrayList<>();

        MutableLiveData<WaterDayWithWaters> waterDayWithWatersLiveData = new MutableLiveData<>(waterDayWithWaters);
        MutableLiveData<List<WaterDayWithWaters>> allWaterDayWithWatersLiveData = new MutableLiveData<>(allWaterDayWithWaters);

        Mockito.when(waterDataRepository.getWaterDayWithWatersLivaData()).thenReturn(waterDayWithWatersLiveData);
        Mockito.when(waterDataRepository.getWaterDayWithWatersValue()).thenReturn(waterDayWithWaters);
        Mockito.when(waterDataRepository.getAllWaterDayWithWatersLiveData()).thenReturn(allWaterDayWithWatersLiveData);
        Mockito.when(waterDataRepository.getAllWaterDayWithWatersLiveDataValue()).thenReturn(allWaterDayWithWaters);
    }

    @Test
    public void loadWaterDayWithWatersByDate_isLoadWaterDayWithWatersByDate() {
            waterViewModel.loadWaterDayWithWatersByDate(LocalDate.now());
            Mockito.verify(waterDataRepository, Mockito.times(1)).loadWaterDayWithWatersByDate(LocalDate.now());
    }

    @Test
    public void insertWaterDay_isInsertWaterDayCalled() {
        WaterDay waterDay = new WaterDay(LocalDate.now(), 1500, true);
        Action action = () -> {};
            waterViewModel.insertWaterDay(waterDay, action);
            Mockito.verify(waterDataRepository, Mockito.times(1)).insertWaterDay(waterDay, action);
    }

    @Test
    public void insertWater_isInsertWaterCalled() {
        Water water = new Water(1200, type, waterDay);
            waterViewModel.insertWater(water);
            Mockito.verify(waterDataRepository, Mockito.times(1)).insertWater(any(), any());
    }

    @Test
    public void addWaterInDay_isAddWaterInDayCalled() {
        Water water = new Water(1200, type, waterDay);
            waterViewModel.addWaterInDay(water);
            Mockito.verify(waterDataRepository, Mockito.times(1)).addWaterInDay(water);
    }

    @Test
    public void removeWaterInDay_isRemoveWaterInDayCalled() {
        Water water = new Water(1200, type, waterDay);
            waterViewModel.removeWaterInDay(water);
            Mockito.verify(waterDataRepository, Mockito.times(1)).removeWaterInDay(water);
    }

    @Test
    public void getWaterDayWithWatersValue_WaterDayWithWatersValueReturned() {
            WaterDayWithWaters waterDayWithWaters = waterViewModel.getWaterDayWithWatersValue();
            Mockito.verify(waterDataRepository, Mockito.times(1)).getWaterDayWithWatersValue();
            assertEquals(this.waterDayWithWaters, waterDayWithWaters);
    }

    @Test
    public void deleteWater_isDeleteWaterCalled() {
        Water water = new Water(1200, type, waterDay);
            waterViewModel.deleteWater(water);
            Mockito.verify(waterDataRepository, Mockito.times(1)).deleteWater(any(), any());
    }

    @Test
    public void getActiveDate_returnsCurrentDate() {
            LocalDate date = waterViewModel.getActiveDate();
            Mockito.verify(waterDataRepository, Mockito.times(1)).getWaterDayWithWatersValue();
            assertEquals(LocalDate.now(), date);
    }

    @Test
    public void setAllWaterDayWithWatersObserver_isSetAllWaterDayWithWatersObserverCalled() {
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);
            waterViewModel.setAllWaterDayWithWatersObserver(activity, waterDayWithWaters1 -> {});
            Mockito.verify(waterDataRepository, Mockito.times(1)).setAllWaterDayWithWatersObserver(any(), any());
    }

    @Test
    public void setAllTypesObserver_isSetTypesLiveDataObserverCalled() {
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);
            waterViewModel.setAllTypesObserver(activity, longTypeHashMap -> {});
            Mockito.verify(waterDataRepository, Mockito.times(1)).setTypesLiveDataObserver(any(), any());
    }
}
