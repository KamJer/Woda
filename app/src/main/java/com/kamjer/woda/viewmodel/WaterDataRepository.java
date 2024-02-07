package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterDataRepository {

    private static WaterDataRepository instance;

    public static final String DATABASE_NAME = "water";
    public static final String[] DEFAULT_DRINKS_TYPES = new String[] {"Water", "Coffee", "Tea", "Soda"};

    private WaterDatabase waterDatabase;
    private WaterDAO waterDAO;

    private Integer waterAmountToDrink;

    private MutableLiveData<Water> water = new MutableLiveData<>();

    private List<Type> waterTypes = new ArrayList<>();
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
        waterTypes.add(type);
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

    public List<Type> getWaterTypes() {
        return waterTypes;
    }

    public void setWaterTypes(List<Type> waterTypes) {
        this.waterTypes = waterTypes;
    }
}
