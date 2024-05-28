package com.kamjer.woda;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class DatabaseInstrumentedTest {
    Type type;
    WaterDay waterDay;

    Water water1;
    Water water2;

    private WaterDAO dao;
    private WaterDatabase db;

    @Before
    public void createDb() {

        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, WaterDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.waterDAO();

        type = new Type(1, "Water", Color.BLUE);
        dao.insertType(type).subscribe(aLong -> type.setId(aLong));

        waterDay = new WaterDay(LocalDate.now(), 1000, true);
        dao.insertWaterDay(waterDay).subscribe();

        water1 = new Water(1500, type, waterDay);
        water2 = new Water(1200, type, waterDay);

        dao.insertWater(water1).subscribe(aLong -> water1.setId(aLong));
        dao.insertWater(water2).subscribe(aLong -> water2.setId(aLong));
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void getWaterByType_isSuccessful() throws InterruptedException {
        LiveData<List<Water>> watersListLiveData = dao.getWaterByType(type.getId());
        assertFalse(getOrAwaitValue(watersListLiveData).isEmpty());
    }

    @Test
    public void insertWater_isSuccessful() throws InterruptedException {
        Water waterToInsert = new Water();
        waterToInsert.setWaterDayId(LocalDate.now());
        waterToInsert.setTypeId(type.getId());
        dao.insertWater(waterToInsert).subscribe(waterToInsert::setId);

        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = dao.getWaterDayWithWatersByDate(LocalDate.now().toString());
        assertTrue(getOrAwaitValue(waterDayWithWatersLiveData).getWaters().contains(waterToInsert));
    }

    @Test
    public void removeWater_isSuccessful() throws InterruptedException {
        dao.deleteWater(water1).subscribe();

        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = dao.getWaterDayWithWatersByDate(LocalDate.now().toString());
        assertFalse(getOrAwaitValue(waterDayWithWatersLiveData).getWaters().contains(water1));
        assertEquals(getOrAwaitValue(waterDayWithWatersLiveData).getWaters().size(), 1);
    }

    @Test
    public void removeWater_isFailed() throws InterruptedException {
        Water waterToRemove = new Water();
        waterToRemove.setWaterDayId(waterDay.getDate());
        waterToRemove.setTypeId(type.getId());
        dao.deleteWater(waterToRemove).subscribe();

        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = dao.getWaterDayWithWatersByDate(LocalDate.now().toString());
        assertTrue(getOrAwaitValue(waterDayWithWatersLiveData).getWaters().contains(water1));
        assertEquals(getOrAwaitValue(waterDayWithWatersLiveData).getWaters().size(), 2);
    }

    @Test
    public void removeWaters_isSuccessful() throws InterruptedException {
        List<Water> waters = new ArrayList<>();
        waters.add(water1);
        waters.add(water2);

        dao.deleteWaters(waters).subscribe();

        LiveData<List<WaterDayWithWaters>> waterDayWithWatersLiveData = dao.getAllWaterDayWithWaters();
        List<Water> allWaters = getOrAwaitValue(waterDayWithWatersLiveData)
                .stream()
                .flatMap(waterDayWithWaters -> {
                    return waterDayWithWaters.getWaters().stream();
                })
                .collect(Collectors.toList());
        waters.forEach(water -> {
            assertFalse(allWaters.contains(water));
        });
    }

    @Test
    public void getAllType_isSuccessful() throws InterruptedException {
        List<Type> allTypes = new ArrayList<>();
        allTypes.add(type);

        LiveData<List<Type>> allTypesFromDataBaseListLiveData = dao.getAllTypes();
        List<Type> allTypesFromDataBase = getOrAwaitValue(allTypesFromDataBaseListLiveData);
        allTypes.forEach(type1 -> {
            assertTrue(allTypesFromDataBase.contains(type1));
        });
    }

    @Test
    public void insertType_isSuccessful() throws InterruptedException {
        Type typeToInsert = new Type();
        typeToInsert.setType("Coffee");
        typeToInsert.setColor(Color.BLACK);
        dao.insertType(typeToInsert).subscribe();

        LiveData<List<Type>> types = dao.getAllTypes();
        assertTrue(getOrAwaitValue(types).contains(typeToInsert));
    }

    @Test
    public void updateType_isSuccessful() throws InterruptedException {
        type.setColor(Color.BLACK);
        dao.updateType(type).subscribe();

        LiveData<List<Type>> allTypesLiveData = dao.getAllTypes();
        assertEquals(getOrAwaitValue(allTypesLiveData).get(0).getColor(), type.getColor());
    }

    @Test
    public void removeType_isSuccessful() throws InterruptedException {
        Type typeToRemove = new Type();
        typeToRemove.setId(1);
        dao.deleteType(typeToRemove).subscribe();

        LiveData<List<Type>> types = dao.getAllTypes();

        assertFalse(getOrAwaitValue(types).contains(typeToRemove));
    }

    @Test
    public void insertWaterDay_isSuccessful() throws InterruptedException {
        WaterDay waterDayToAdd = new WaterDay();
        waterDayToAdd.setDate(LocalDate.now().plusDays(1));
        waterDayToAdd.setWaterToDrink(1000);
        waterDayToAdd.setInserted(false);

        dao.insertWaterDay(waterDayToAdd).subscribe();

        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = dao.getWaterDayWithWatersByDate(LocalDate.now().plusDays(1).toString());

        assertNotNull(getOrAwaitValue(waterDayWithWatersLiveData));
        assertEquals(getOrAwaitValue(waterDayWithWatersLiveData).getWaterDay(), waterDayToAdd);
    }

    @Test
    public void updateWaterDay_isSuccessful() throws InterruptedException {
        int newWaterAmountToDrink = 1500;
        WaterDay waterDayToUpdate = new WaterDay(LocalDate.now(), newWaterAmountToDrink, true);
        dao.updateWaterDay(waterDayToUpdate).subscribe();
        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = dao.getWaterDayWithWatersByDate(LocalDate.now().toString());
        assertEquals(newWaterAmountToDrink, getOrAwaitValue(waterDayWithWatersLiveData).getWaterDay().getWaterToDrink().intValue());
    }

    @Test
    public void getWaterDayWithWatersByDate_isSuccessful() throws InterruptedException {
        List<Water> waters = new ArrayList<>();
        waters.add(water1);
        waters.add(water2);
        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = dao.getWaterDayWithWatersByDate(LocalDate.now().toString());
        WaterDayWithWaters waterDayWithWaters = getOrAwaitValue(waterDayWithWatersLiveData);
        assertEquals(waterDayWithWaters.getWaterDay(), waterDay);
        List<Water> waterListFromDataBase = waterDayWithWaters.getWaters();
        waters.forEach(water -> assertTrue(waterListFromDataBase.contains(water)));
    }

    @Test
    public void getAllWaterDayWithWaters_isSuccessful() throws InterruptedException {
        List<Water> waters = new ArrayList<>();
        waters.add(water1);
        waters.add(water2);
        List<WaterDayWithWaters> waterDayWithWatersList = new ArrayList<>();
        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(waterDay, waters);
        waterDayWithWatersList.add(waterDayWithWaters);

        LiveData<List<WaterDayWithWaters>> allWaterDayWithWatersListLiveData = dao.getAllWaterDayWithWaters();
        List<WaterDayWithWaters> waterDayWithWatersListFromDataBase = getOrAwaitValue(allWaterDayWithWatersListLiveData);

        waterDayWithWatersListFromDataBase.forEach(waterDayWithWaters1 -> {
            List<Water> watersFromDataBase = waterDayWithWaters1.getWaters();
            assertTrue(waterDayWithWatersList.contains(waterDayWithWaters1));
            watersFromDataBase.forEach(water -> {
                assertTrue(waters.contains(water));
            });
        });
    }

    /**
     * Utility method for testing liveData
     * @param liveData to process
     * @return object from liveData
     * @param <T> type of a returned object
     * @throws InterruptedException if there is an issue with threads
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Observer<T> observer = new Observer<T>() {
                @Override
                public void onChanged(@Nullable T o) {
                    data[0] = o;
                    latch.countDown();
                    liveData.removeObserver(this);
                }
            };
            liveData.observeForever(observer);
        });
        latch.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) data[0];
    }
}
