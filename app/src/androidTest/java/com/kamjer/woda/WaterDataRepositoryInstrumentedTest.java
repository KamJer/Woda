package com.kamjer.woda;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.viewmodel.WaterDataRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WaterDataRepositoryInstrumentedTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    WaterDAO mockWaterDAO;

    @Mock
    Observer<List<Water>> mockObserver;

    private WaterDatabase waterDatabase;
    private WaterDataRepository waterDataRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Context context = ApplicationProvider.getApplicationContext();
        waterDatabase = Room.inMemoryDatabaseBuilder(context, WaterDatabase.class).build();
        waterDataRepository = new WaterDataRepository();
        waterDataRepository.setWaterDatabase(waterDatabase);
        waterDataRepository.setWaterDAO(mockWaterDAO);
    }

    @Test
    public void testAddWaterValue() {
        LocalDate date = LocalDate.now();
        Water water = new Water();
        List<Water> waters = new ArrayList<>();
        waters.add(water);

        waterDataRepository.setWatersValue(date, waters);
        waterDataRepository.getWaters().observeForever(mockObserver);
        waterDataRepository.addWaterValue(water);

        verify(mockObserver, times(2)).onChanged(waters);
    }

    @Test
    public void testDeleteWater() {
        Water water = new Water();
        waterDataRepository.deleteWater(water);
        verify(mockWaterDAO).deleteWater(water);
    }

    @Test
    public void testSetActiveDate() {
        LocalDate date = LocalDate.now();
        waterDataRepository.setActiveDate(date);
        assertEquals(date, waterDataRepository.getActiveDate());
    }
}