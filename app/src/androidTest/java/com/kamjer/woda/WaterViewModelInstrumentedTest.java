package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.viewmodel.WaterDataRepository;
import com.kamjer.woda.viewmodel.WaterViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@RunWith(MockitoJUnitRunner.class)
public class WaterViewModelInstrumentedTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    WaterViewModel waterViewModel;
    @Mock
    WaterDataRepository mockRepository;
    @Mock
    WaterDAO mockWaterDAO;
    @Mock
    AppCompatActivity mockActivity;
    @Mock
    Resources mockResources;
    @Mock
    SharedPreferences mockSharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        waterViewModel = new WaterViewModel();
        waterViewModel.setRepository(mockRepository);

        when(mockRepository.getWaterDAO()).thenReturn(mockWaterDAO);
    }

    @Test
    public void testLoadWaterAmount() {
        int expectedAmount = 1500;
//        mocking response from repository
        when(mockRepository.getWaterAmountToDrink()).thenReturn(1500);
//        mocking resources and activity behavior
        when(mockActivity.getResources()).thenReturn(mockResources);
        when(mockActivity.getString(R.string.shared_preferences)).thenReturn("test_key");
        when(mockActivity.getSharedPreferences("test_key", AppCompatActivity.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getInt("water_amount_to_drink", 1500)).thenReturn(expectedAmount);

        waterViewModel.loadWaterAmount(mockActivity);

        assertEquals(expectedAmount, waterViewModel.getWaterAmountToDrink());
    }

    @Test
    public void testLoadWaterById() {
        Water expectedWater = new Water();
        expectedWater.setId(1);
        expectedWater.setTypeId(1);

        when(mockWaterDAO.getWaterById(1)).thenReturn(Single.just(expectedWater));

        waterViewModel.loadWaterById(1, water -> assertEquals(expectedWater.getId(), water.getId()));
    }

    @Test
    public void testLoadWaterByDate() {
        LocalDate date = LocalDate.now();

        Water expectedWater = new Water();
        expectedWater.setId(1);
        expectedWater.setTypeId(1);
        List<Water> expectedWaters = new ArrayList<>();
        expectedWaters.add(expectedWater);

        when(mockWaterDAO.getWaterByDate(date.toString())).thenReturn(Flowable.just(expectedWaters));

        waterViewModel.loadWaterByDate(date, waters -> assertEquals(waters, expectedWaters));
        verify(mockWaterDAO).getWaterByDate(date.toString());
    }

    @Test
    public void testLoadWaterAll() {
        List<Water> expectedWaters = new ArrayList<>();
        Water water1 = new Water();
        Water water2 = new Water();
        expectedWaters.add(water1);
        expectedWaters.add(water2);

        when(mockWaterDAO.getAllWaters()).thenReturn(Flowable.just(expectedWaters));

        waterViewModel.loadWaterAll(waters -> assertEquals(expectedWaters, waters));
    }

    @Test
    public void testLoadWaterFromMonth() {
        LocalDate date = LocalDate.now();

        List<Water> expectedWaters = new ArrayList<>();
        Water water1 = new Water();
        Water water2 = new Water();
        expectedWaters.add(water1);
        expectedWaters.add(water2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedDate = date.format(formatter);

        when(mockWaterDAO.getWatersFromMonth(formattedDate)).
                thenReturn(Flowable.just(expectedWaters));

        waterViewModel.loadWatersFromMonth(date, waters -> assertEquals(expectedWaters, waters));
        verify(mockWaterDAO).getWatersFromMonth(formattedDate);
    }

    @Test
    public void testInsertWater() {
        List<Water> waters = new ArrayList<>();
        Water water1 = new Water();
        waters.add(water1);

        MutableLiveData<List<Water>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(waters);

        when(mockWaterDAO.insertWater(water1)).thenReturn(Completable.complete());
        when(mockRepository.getWaters()).thenReturn(mutableLiveData);

        waterViewModel.insertWater(water1, () -> waterViewModel.addWater(water1));
        assertTrue(waterViewModel.getWaterValue().contains(water1));
        verify(mockWaterDAO).insertWater(water1);
    }

    @Test
    public void testInsertType() {
        Type type = new Type();
        type.setId(1);
        type.setType("Water");

        Map<String, Type> expectedMap = new HashMap<>();
        expectedMap.put(type.getType(), type);

        when(mockWaterDAO.insertType(type)).thenReturn(Maybe.just(1L));
        when(mockRepository.getWaterTypes()).thenReturn(expectedMap);

        waterViewModel.insertType(type);

        assertTrue(waterViewModel.getWaterTypes().containsValue(type));
        verify(mockWaterDAO).insertType(type);
    }
}
