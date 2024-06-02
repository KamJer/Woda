package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.repository.ResourcesRepository;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.WaterDataRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterRepositoryUnitTest {
    @Mock
    private WaterDAO waterDAO;

    @Mock
    private WaterDatabase waterDatabase;
    @Mock
    private MediatorLiveData<WaterDayWithWaters> waterDayWithWatersMediatorLiveData;
    @Mock
    private LiveData<List<WaterDayWithWaters>> allWaterDayWithWatersLiveData;
    @Mock
    private MediatorLiveData<HashMap<Long, Type>> waterTypes;
    @Mock
    private WaterDataRepository waterDataRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(waterDatabase.waterDAO()).thenReturn(waterDAO);
        waterDataRepository = new WaterDataRepository(waterDatabase,
                waterDayWithWatersMediatorLiveData,
                allWaterDayWithWatersLiveData,
                waterTypes);

        doNothing().when(waterDayWithWatersMediatorLiveData).observe(any(), any());
        doNothing().when(allWaterDayWithWatersLiveData).observe(any(), any());
        doNothing().when(waterTypes).observe(any(), any());

        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @Test
    public void testDeleteWaters() {
        Water water1 = new Water();
        Water water2 = new Water();
        List<Water> waters = new ArrayList<>();
        waters.add(water1);
        waters.add(water2);

        when(waterDAO.deleteWaters(waters)).thenReturn(io.reactivex.rxjava3.core.Completable.complete());
        waterDataRepository.deleteWaters(waters, () -> {});
        verify(waterDAO, times(1)).deleteWaters(waters);
    }

    @Test
    public void testGetWaterTypes() {
        HashMap<Long, Type> typesMap = new HashMap<>();
        Type type = new Type();
        type.setType("test");
        type.setId(1L);
        typesMap.put(1L, type);

        when(waterTypes.getValue()).thenReturn(typesMap);
        HashMap<Long, Type> result = waterDataRepository.getWaterTypes();
        assertEquals(typesMap, result);
    }

    @Test
    public void testGetInstance() {
        WaterDataRepository instance1 = WaterDataRepository.getInstance();
        WaterDataRepository instance2 = WaterDataRepository.getInstance();

        assertEquals(instance1, instance2);
    }

    @Test
    public void testSetWaterDayWithWatersValueUpdatesMediatorLiveData() {
        LocalDate date = LocalDate.now();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, 2000);

        waterDataRepository.setWaterDayWithWatersValue(waterDayWithWaters);
        verify(waterDayWithWatersMediatorLiveData, times(1)).setValue(waterDayWithWaters);
    }

    @Test
    public void testSetAllWaterDayWithWatersObserverUpdatesLiveData() {
        LifecycleOwner owner = mock(LifecycleOwner.class);
        Observer<List<WaterDayWithWaters>> observer = mock(Observer.class);

        waterDataRepository.setAllWaterDayWithWatersObserver(owner, observer);
        verify(allWaterDayWithWatersLiveData, times(1)).observe(owner, observer);
    }

    @Test
    public void testAddWaterInDay() {
        LocalDate date = LocalDate.now();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, 2000);
        when(waterDayWithWatersMediatorLiveData.getValue()).thenReturn(waterDayWithWaters);

        Water water = new Water();
        water.setWaterDayId(date);

        waterDataRepository.addWaterInDay(water);

        assertEquals(1, waterDayWithWaters.getWaters().size());
        assertEquals(water, waterDayWithWaters.getWaters().get(0));
    }

    @Test
    public void testRemoveWaterInDay() {
        LocalDate date = LocalDate.now();
        Water water = new Water();
        water.setWaterDayId(date);

        List<Water> waters = new ArrayList<>();
        waters.add(water);

        WaterDay waterDay = new WaterDay(date, 1500, true);

        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(waterDay, waters);
        when(waterDayWithWatersMediatorLiveData.getValue()).thenReturn(waterDayWithWaters);

        waterDataRepository.removeWaterInDay(water);

        assertEquals(0, waterDayWithWaters.getWaters().size());
    }

    @Test
    public void testSetWaterDayWithWatersValue() {
        LocalDate date = LocalDate.now();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, 2000);
        when(waterDayWithWatersMediatorLiveData.getValue()).thenReturn(waterDayWithWaters);
        waterDataRepository.setWaterDayWithWatersValue(waterDayWithWaters);
        assertEquals(waterDayWithWaters, waterDataRepository.getWaterDayWithWatersLivaData().getValue());
    }

    @Test
    public void testGetWaterDayWithWatersValue() {
        LocalDate date = LocalDate.now();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, 2000);
        when(waterDayWithWatersMediatorLiveData.getValue()).thenReturn(waterDayWithWaters);
        assertEquals(waterDayWithWaters, waterDataRepository.getWaterDayWithWatersValue());
    }

    @Test
    public void testInsertType() {
        Type type = new Type();
        when(waterDAO.insertType(type)).thenReturn(io.reactivex.rxjava3.core.Maybe.just(1L));
        waterDataRepository.insertType(type, id -> assertEquals(1L, (long) id));
        verify(waterDAO, times(1)).insertType(type);
    }

    @Test
    public void testUpdateType() {
        Type type = new Type();
        when(waterDAO.updateType(type)).thenReturn(io.reactivex.rxjava3.core.Completable.complete());
        waterDataRepository.updateType(type);
        verify(waterDAO, times(1)).updateType(type);
    }

    @Test
    public void testRemoveType() {
        Type type = new Type();
        type.setId(1L);
        when(waterDAO.deleteType(type)).thenReturn(io.reactivex.rxjava3.core.Completable.complete());
        waterDataRepository.removeType(type);
        verify(waterDAO, times(1)).deleteType(type);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTypeThrowsException() {
        Type type = new Type();
        type.setId(1L);
        when(waterDAO.deleteType(type)).thenReturn(io.reactivex.rxjava3.core.Completable.complete());
        doThrow(new IllegalArgumentException()).when(waterDAO).deleteType(type);
        waterDataRepository.removeType(type);
    }

    @Test
    public void testContainsDefaultTypes() {
        List<Type> defaultTypes = new ArrayList<>();
        Type type = new Type();
        type.setType("test");
        defaultTypes.add(type);

        List<Type> typesToCheck = new ArrayList<>();
        List<Type> result = WaterDataRepository.containsDefaultTypes(defaultTypes, typesToCheck);

        assertEquals(1, result.size());
        assertEquals(type, result.get(0));
    }

    @Test
    public void testGetUsedTypes() {
        Type type = new Type();
        type.setType("test");
        type.setId(1L);

        Water water = new Water();
        water.setTypeId(1L);

        HashMap<Long, Type> waterTypes = new HashMap<>();
        waterTypes.put(1L, type);

        List<Water> waters = new ArrayList<>();
        waters.add(water);

        List<Type> result = WaterDataRepository.getUsedTypes(waterTypes, waters);

        assertEquals(1, result.size());
        assertEquals(type, result.get(0));
    }

    @Test
    public void testGetAllTypes() {
        waterDataRepository.getAllTypes();
        verify(waterDAO, times(1)).getAllTypes();
    }

    @Test
    public void testLoadWaterDayWithWatersByDate() {
        LocalDate date = LocalDate.now();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, 2000);
        MutableLiveData<WaterDayWithWaters> waterDayWithWatersLiveData = new MutableLiveData<>(waterDayWithWaters);
        when(waterDAO.getWaterDayWithWatersByDate(date.toString())).thenReturn(waterDayWithWatersLiveData);
        when(waterDayWithWatersMediatorLiveData.getValue()).thenReturn(waterDayWithWaters);
        waterDataRepository.loadWaterDayWithWatersByDate(date);
        assertEquals(waterDayWithWaters, waterDataRepository.getWaterDayWithWatersLivaData().getValue());
    }

    @Test
    public void testInsertWater() {
        Water water = new Water();
        when(waterDAO.insertWater(water)).thenReturn(io.reactivex.rxjava3.core.Maybe.just(1L));
        waterDataRepository.insertWater(water, id -> assertEquals(1L, (long) id));
        verify(waterDAO, times(1)).insertWater(water);
    }

    @Test
    public void testInsertWaterDay() {
        WaterDay waterDay = new WaterDay();
        when(waterDAO.insertWaterDay(waterDay)).thenReturn(io.reactivex.rxjava3.core.Completable.complete());
        waterDataRepository.insertWaterDay(waterDay, () -> {});
        verify(waterDAO, times(1)).insertWaterDay(waterDay);
    }

    @Test
    public void testDeleteWater() {
        Water water = new Water();
        when(waterDAO.deleteWater(water)).thenReturn(io.reactivex.rxjava3.core.Completable.complete());
        waterDataRepository.deleteWater(water, () -> {});
        verify(waterDAO, times(1)).deleteWater(water);
    }

    @Test
    public void testSetTypesLiveDataObserver() {
        LifecycleOwner owner = mock(LifecycleOwner.class);
        Observer<HashMap<Long, Type>> observer = mock(Observer.class);
        waterDataRepository.setTypesLiveDataObserver(owner, observer);
        verify(waterTypes, times(1)).observe(owner, observer);
    }

    @Test
    public void testGetWaterAmountToDrink() {
        int amount = waterDataRepository.getWaterAmountToDrink();
        assertEquals(SharedPreferencesRepository.DEFAULT_WATER_AMOUNT_TO_DRINK, amount);
    }

    @Test
    public void testSetWaterAmountToDrink() {
        int newAmount = 2000;
        LocalDate date = LocalDate.now();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(date, 1500);
        when(waterDAO.updateWaterDay(new WaterDay())).thenReturn(Completable.complete());
        when(waterDayWithWatersMediatorLiveData.getValue()).thenReturn(waterDayWithWaters);
        waterDataRepository.setWaterAmountToDrink(newAmount);
        assertEquals(newAmount, waterDataRepository.getWaterAmountToDrink());
    }

    @Test
    public void testGetAllWaterDayWithWatersLiveData() {
        assertNotNull(waterDataRepository.getAllWaterDayWithWatersLiveData());
    }

    @Test
    public void testSetAllWaterDayWithWatersObserver() {
        LifecycleOwner owner = mock(LifecycleOwner.class);
        Observer<List<WaterDayWithWaters>> observer = mock(Observer.class);
        waterDataRepository.setAllWaterDayWithWatersObserver(owner, observer);
        verify(allWaterDayWithWatersLiveData, times(1)).observe(owner, observer);
    }

    @Test
    public void testGetAllWaterDayWithWatersLiveDataValue() {
        assertNotNull(waterDataRepository.getAllWaterDayWithWatersLiveDataValue());
    }

    @Test
    public void testLoadAllWaterDayWithWaters() {
        when(waterDAO.getAllWaterDayWithWaters()).thenReturn(new MutableLiveData<>());
        waterDataRepository.loadAllWaterDayWithWaters();
        assertNotNull(waterDataRepository.getAllWaterDayWithWatersLiveData());
    }

}
