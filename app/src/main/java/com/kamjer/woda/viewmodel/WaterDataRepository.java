package com.kamjer.woda.viewmodel;

import android.content.Context;
import android.graphics.Path;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;

import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WaterDataRepository {

    private static WaterDataRepository instance;

    public static final String DATABASE_NAME = "water";
    public static final String[] DEFAULT_DRINKS_TYPES = new String[] {"Water", "Coffee", "Tea", "Soda"};

    private WaterDatabase waterDatabase;
    private WaterDAO waterDAO;
    private MutableLiveData<WaterDayWithWaters> waters = new MutableLiveData<>();

    private HashMap<String, Type> waterTypes = new HashMap<>();
    public WaterDataRepository() {
    }

    public void createWaterDatabase(Context context) {
//        if database does not exists already create a new one
        if (waterDatabase == null) {
            this.waterDatabase = Room.databaseBuilder(context, WaterDatabase.class, WaterDataRepository.DATABASE_NAME)
                    .addMigrations(WaterDatabase.MIGRATION_1_2)
                    .addMigrations(WaterDatabase.MIGRATION_2_3)
                    .build();
            this.waterDAO = waterDatabase.waterDAO();
        }
    }

    public static WaterDataRepository getInstance() {
        if (instance == null) {
            instance = new WaterDataRepository();
        }
        return instance;
    }

    public void putType(Type type) {
        waterTypes.put(type.getType(), type);
    }

    public void setWatersValue(WaterDayWithWaters waters) {
        this.waters.setValue(waters);
    }

    public MutableLiveData<WaterDayWithWaters> getWaters() {
        return waters;
    }

    public void addWaterValue(Water water) {
//      getting waterDayWithWaters or creating it
        WaterDayWithWaters waterDayWithWaters = Optional.ofNullable(this.waters.getValue()).orElseGet(() -> new WaterDayWithWaters(LocalDate.now(), 1500));
//      getting waterDay from WaterDayWithWaters or creating it
        WaterDay waterDay = waterDayWithWaters.getWaterDay();
//      getting waters from WaterDayWithWaters or creating it
        List<Water> waters = waterDayWithWaters.getWaters();
        water.setWaterDayId(waterDay.getId());
        if (!waters.contains(water)) {
            waters.add(water);
        }
        this.waters.setValue(waterDayWithWaters);
    }

    public void removeWaterValue(Water water) {
        WaterDayWithWaters waterDayWithWaters = Optional.ofNullable(this.waters.getValue()).orElseGet(() -> new WaterDayWithWaters(LocalDate.now(), WaterViewModel.DEFAULT_WATER_TO_DRINK));
        waterDayWithWaters.getWaters().remove(water);
        this.waters.setValue(waterDayWithWaters);
    }

    public WaterDatabase getWaterDatabase() {
        return waterDatabase;
    }

    public void setWaterDatabase(WaterDatabase waterDatabase) {
        this.waterDatabase = waterDatabase;
    }

    public void setWaterDAO(WaterDAO waterDAO) {
        this.waterDAO = waterDAO;
    }

    public WaterDAO getWaterDAO() {
        return waterDAO;
    }

    public void setWaterDayValue(WaterDayWithWaters waterDayWithWaters) {
        waters.setValue(waterDayWithWaters);
    }

    public WaterDayWithWaters getWaterDayValue() {
        return Optional.ofNullable(waters.getValue()).orElse(new WaterDayWithWaters(LocalDate.now(), WaterViewModel.DEFAULT_WATER_TO_DRINK));
    }
    public Map<String, Type> getWaterTypes() {
        return waterTypes;
    }

    public void setWaterTypes(Map<String, Type> waterTypes) {
        this.waterTypes = (HashMap<String, Type>) waterTypes;
    }

    public int getWaterToDrink() {
        return getWaterDayValue().getWaterDay().getWaterToDrink();
    }

    public void setWaterToDrink(int waterToDrink) {
        getWaterDayValue().getWaterDay().setWaterToDrink(waterToDrink);
    }
}
