package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.kamjer.woda.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Water;

import java.util.Optional;

public class WaterDataRepository {

    private static WaterDataRepository instance;

    private static final String DATABASE_NAME = "water";

    private WaterDatabase waterDatabase;
    private WaterDAO waterDAO;

    private Integer waterAmountToDrink;

    private MutableLiveData<Water> water = new MutableLiveData<>();

    private WaterDataRepository() {
    }

    public void createWaterDatabase(Context context) {
//        if database does not exists already create a new one
        if (waterDatabase == null) {
            this.waterDatabase = Room.databaseBuilder(context, WaterDatabase.class, WaterDataRepository.DATABASE_NAME).build();
            this.waterDAO = waterDatabase.waterDAO();
        }
    }

    public static WaterDataRepository getInstance() {
        if (instance == null) {
            instance = new WaterDataRepository();
        }
        return instance;
    }

    public MutableLiveData<Water> getWater() {
        return water;
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
}
