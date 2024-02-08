package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;

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

    private Integer waterAmountToDrink;

    private LocalDate activeDate = LocalDate.now();

    private MutableLiveData<List<Water>> waters = new MutableLiveData<>();

    private HashMap<String, Type> waterTypes = new HashMap<>();
    private WaterDataRepository() {
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

    public void setWaters(List<Water> waters) {
        this.waters.setValue(waters);
    }

    public MutableLiveData<List<Water>> getWaters() {
        return waters;
    }

    public void deleteWater(Water water) {
        waterDAO.deleteWater(water);
    }

    public void addWaterValue(Water water) {
        List<Water> waters = Optional.ofNullable(this.waters.getValue()).orElseGet(ArrayList::new);
        waters.add(water);
        this.waters.setValue(waters);
    }

    public WaterDatabase getWaterDatabase() {
        return waterDatabase;
    }

    public WaterDAO getWaterDAO() {
        return waterDAO;
    }

    public Integer getWaterAmountToDrink() {
        return waterAmountToDrink;
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
        this.waterAmountToDrink = waterAmountToDrink;
    }

    public Map<String, Type> getWaterTypes() {
        return waterTypes;
    }

    public void setWaterTypes(Map<String, Type> waterTypes) {
        this.waterTypes = (HashMap<String, Type>) waterTypes;
    }

    public LocalDate getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(LocalDate activeDate) {
        this.activeDate = activeDate;
    }
}
