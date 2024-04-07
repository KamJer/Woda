package com.kamjer.woda.repository;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterDataRepository {

    private static WaterDataRepository instance;

    public static final String DATABASE_NAME = "water";

    private WaterDatabase waterDatabase;
    private WaterDAO waterDAO;

    private final MediatorLiveData<WaterDayWithWaters> waters = new MediatorLiveData<>();

    private LiveData<List<WaterDayWithWaters>> allWaterDayWithWatersLiveData;

    private final MutableLiveData<HashMap<Long, Type>> waterTypes = new MutableLiveData<>();

    private final CompositeDisposable serialDisposable = new CompositeDisposable();

    public void createWaterDatabase(Context context) {
//        if database does not exists already create a new one
        if (waterDatabase == null) {
            this.waterDatabase = Room.databaseBuilder(context, WaterDatabase.class, WaterDataRepository.DATABASE_NAME)
                    .build();
            this.waterDAO = waterDatabase.waterDAO();
        }

        serialDisposable.add(getWaterDAO().getAllTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(types -> {
//                  add fetched types to the liveData
                    types.forEach(this::putType);
//                  check if fetched data contains default types, if it does not add it to the database and put in liveDatas
                    WaterDataRepository.containsDefaultTypes(ResourcesRepository
                                            .getInstance()
                                            .getDefaultDrinksTypes(),
                                    getWaterTypes().values())
                            .forEach(this::insertType);
                }));

        allWaterDayWithWatersLiveData = (getWaterDAO().getAllWaterDayWithWaters());
    }

    public static WaterDataRepository getInstance() {
        if (instance == null) {
            instance = new WaterDataRepository();
        }
        return instance;
    }

    public void getWaterDayWithWatersByDate(LocalDate date) {
        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = getWaterDAO().getWaterDayWithWatersByDate(date.toString());
        this.waters.addSource(waterDayWithWatersLiveData, waterDayWithWaters -> {
            waters.setValue(Optional.ofNullable(waterDayWithWaters).map(waterDayWithWaters1 -> {
                waterDayWithWaters1.getWaterDay().setInserted(true);
                return waterDayWithWaters1;
            }).orElse(new WaterDayWithWaters(date, SharedPreferencesRepository.getInstance().getWaterAmountToDrink())));
            waters.removeSource(waterDayWithWatersLiveData);
        });
    }

    public void setWaterDayWithWatersValue(WaterDayWithWaters waters) {
        this.waters.setValue(waters);
    }

    public MutableLiveData<WaterDayWithWaters> getWaterDayWithWatersLivaData() {
        return waters;
    }

    public void addWaterValue(Water water) {
//      getting waterDayWithWaters or creating it
        WaterDayWithWaters waterDayWithWaters = Optional.ofNullable(this.waters.getValue()).orElseGet(() -> new WaterDayWithWaters(LocalDate.now(), 1500));
//      getting waterDay from WaterDayWithWaters or creating it
        WaterDay waterDay = waterDayWithWaters.getWaterDay();
//      getting waters from WaterDayWithWaters or creating it
        List<Water> waters = waterDayWithWaters.getWaters();
        water.setWaterDayId(waterDay.getDate());
        if (!waters.contains(water)) {
            waters.add(water);
        }
        this.waters.setValue(waterDayWithWaters);
    }

    public void removeWater(Water water) {
        WaterDayWithWaters waterDayWithWaters = getWaterDayWithWatersValue();
        waterDayWithWaters.getWaters().remove(water);
        this.setWaterDayWithWatersValue(waterDayWithWaters);
    }

    public void setWatersInADayValue(List<Water> waters) {
        WaterDayWithWaters waterDayValue = getWaterDayWithWatersValue();
        waterDayValue.setWaters(waters);
        setWaterDayWithWatersValue(waterDayValue);
    }

    public WaterDAO getWaterDAO() {
        return waterDAO;
    }

    public WaterDayWithWaters getWaterDayWithWatersValue() {
        return Optional.ofNullable(waters.getValue())
                .orElse(new WaterDayWithWaters(LocalDate.now(), SharedPreferencesRepository.DEFAULT_WATER_AMOUNT_TO_DRINK));
    }

    public void insertType(Type type) {
        serialDisposable.add(getWaterDAO()
                .insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    type.setId(aLong);
                    putType(type);
                }));
    }

    public void updateType(Type type) {
        serialDisposable.add(getWaterDAO()
                .updateType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    /**
     * Removes a type, filters all of waters in a active day and removes them from that day
     * @param id of a type to remove
     */
    public void removeWaterType(long id) {
        HashMap<Long, Type> waterTypes = getWaterTypes();
        waterTypes.remove(id);
        this.waterTypes.setValue(waterTypes);
    }

    /**
     * Puts type in live data, does not insert nor update it into database
     * @param type to put in a live data
     */
    public void putType(Type type) {
        HashMap<Long, Type> waterTypes = getWaterTypes();
        waterTypes.put(type.getId(), type);
        this.waterTypes.setValue(waterTypes);
    }

    /**
     * Sets observer on a type liveData
     * @param owner owner of a observer to set
     * @param observer observer to set
     */
    public void setTypesLiveDataObserver(LifecycleOwner owner, Observer<HashMap<Long, Type>> observer) {
        waterTypes.observe(owner, observer);
    }

    /**
     * Returns new HashMap of Types, changing it does not effect original Map
     * @return new map of Types
     */
    public HashMap<Long, Type> getWaterTypes() {
        return new HashMap<>(Optional.ofNullable(waterTypes.getValue()).orElseGet(HashMap::new));
    }

    /**
     * Sets passed map to liveData
     * @param waterTypes map of types to set
     */
    public void setWaterTypes(HashMap<Long, Type> waterTypes) {
        this.waterTypes.setValue(waterTypes);
    }

    /**
     * Checks if map of types contains all of types in an array
     * if types are not contained in a map they are added to a list and returned
     * @param defaultTypes - types to check
     * @param typesToCheck - map of types to compare to
     * @return list of Types not contained in a map, if all types from an array are contained in a map list length is 0
     */
    public static List<Type> containsDefaultTypes(List<Type> defaultTypes, Collection<Type> typesToCheck) {
        List<Type> test = new ArrayList<>();
//      loop through default drink types and check if it is in a database if it is not, add not found type to the list
        for (Type type : defaultTypes) {
            if (!typesToCheck.contains(type)) {
                test.add(type);
            }
        }
//      if types in a database does not contain default ones return those types that are not in a database,
//      if database contains all of a default types list will have length of 0
        return test;
    }

    public static boolean test(List<Type> defaultTypes, Type typeToCheck) {
        return defaultTypes.contains(typeToCheck);
    }

    /**
     * Finds all of used types in a passed list of waters
     * @param waters list of waters to check
     * @return list of maps
     */
    public static List<Type> getUsedTypes(Map<Long, Type> waterTypes, List<Water> waters) {
        return waters.stream().map(water -> waterTypes.get(water.getTypeId())).collect(Collectors.toList());
    }

    /**
     * Returns the amount of water to drink to achieve set goal, returns value from active waterDay
     * @return value of water to drink
     */
    public int getWaterAmountToDrink() {
        return getWaterDayWithWatersValue().getWaterDay().getWaterToDrink();
    }

    /**
     * Sets in an active WaterDay new water amount to drink
     * @param waterAmountToDrink - new goal
     */
    public void setWaterAmountToDrink(int waterAmountToDrink) {
        WaterDayWithWaters waterDayWithWatersToUpdate = getWaterDayWithWatersValue();
        waterDayWithWatersToUpdate.getWaterDay().setWaterToDrink(waterAmountToDrink);
        setWaterDayWithWatersValue(waterDayWithWatersToUpdate);
    }

    public LiveData<List<WaterDayWithWaters>> getAllWaterDayWithWatersLiveData() {
        return allWaterDayWithWatersLiveData;
    }

    public WaterDatabase getWaterDatabase() {
        return waterDatabase;
    }

    public void clearDisposable() {
        serialDisposable.clear();
    }
}
