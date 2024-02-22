package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;

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

//    private Integer waterAmountToDrink;

//    private LocalDate activeDate = LocalDate.now();

    private MutableLiveData<WaterDay> waters = new MutableLiveData<>();

    private HashMap<String, Type> waterTypes = new HashMap<>();
    public WaterDataRepository() {
    }

    public void createWaterDatabase(Context context) {
//        if database does not exists already create a new one
        if (waterDatabase == null) {
            this.waterDatabase = Room.databaseBuilder(context, WaterDatabase.class, WaterDataRepository.DATABASE_NAME)
                    .addMigrations(WaterDatabase.MIGRATION_1_2)
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

    public void setWatersValue(WaterDay waters) {
        this.waters.setValue(waters);
    }

    public MutableLiveData<WaterDay> getWaters() {
        return waters;
    }

    public void addWaterValue(Water water) {
//        creates new waterDay value if waterDay is null
        WaterDay waters = Optional.ofNullable(this.waters.getValue()).orElseGet(WaterDay::new);
        if (!waters.getWater().contains(water)) {
            waters.getWater().add(water);
        }
        this.waters.setValue(waters);
    }

    public void removeWaterValue(Water water) {
        WaterDay waters = Optional.ofNullable(this.waters.getValue()).orElseGet(WaterDay::new);
        waters.getWater().remove(water);
        this.waters.setValue(waters);
    }

    public void setActiveDate(LocalDate date) {
        WaterDay waters = Optional.ofNullable(this.waters.getValue()).orElseGet(WaterDay::new);
        waters.setDate(date);
        this.waters.setValue(waters);
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

    public void setWaterDayValue(WaterDay waterDay) {
        waters.setValue(waterDay);
    }

    public WaterDay getWaterDayValue() {
        return Optional.ofNullable(waters.getValue()).orElse(new WaterDay());
    }
    public Map<String, Type> getWaterTypes() {
        return waterTypes;
    }

    public void setWaterTypes(Map<String, Type> waterTypes) {
        this.waterTypes = (HashMap<String, Type>) waterTypes;
    }
}
