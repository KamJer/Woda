package com.kamjer.woda.repository;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.kamjer.woda.database.WaterDataValidation;
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
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterDataRepository {

    private static WaterDataRepository instance;

    public static final String DATABASE_NAME = "water";

    private WaterDatabase waterDatabase;
    private WaterDAO waterDAO;

    private final MediatorLiveData<WaterDayWithWaters> waters = new MediatorLiveData<>();

    private LiveData<List<WaterDayWithWaters>> allWaterDayWithWatersLiveData;

    private final MediatorLiveData<HashMap<Long, Type>> waterTypes = new MediatorLiveData<>();

    public void createWaterDatabase(Context context) {
//        if database does not exists already create a new one
        if (waterDatabase == null) {
            this.waterDatabase = Room.databaseBuilder(context, WaterDatabase.class, WaterDataRepository.DATABASE_NAME)
                    .build();
            this.waterDAO = waterDatabase.waterDAO();
        }
        allWaterDayWithWatersLiveData = (getWaterDAO().getAllWaterDayWithWaters());
    }

    public void getAllTypes() {
        waterTypes.addSource(getWaterDAO().getAllTypes(), types -> {
            waterTypes.setValue((HashMap<Long, Type>) types.stream().collect(Collectors.toMap(
                    type -> ((Type) type).getId(),
                    o -> o)));

            getWaterDAO().insertTypes(WaterDataRepository.containsDefaultTypes(ResourcesRepository
                            .getInstance()
                            .getDefaultDrinksTypes(),
                    getWaterTypes().values()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        });
    }

    public WaterDAO getWaterDAO() {
        return waterDAO;
    }

    public static WaterDataRepository getInstance() {
        if (instance == null) {
            instance = new WaterDataRepository();
        }
        return instance;
    }

    public void loadWaterDayWithWatersByDate(LocalDate date) {
        LiveData<WaterDayWithWaters> waterDayWithWatersLiveData = getWaterDAO().getWaterDayWithWatersByDate(date.toString());
        this.waters.addSource(waterDayWithWatersLiveData, waterDayWithWaters -> {
            waters.setValue(Optional.ofNullable(waterDayWithWaters).map(waterDayWithWaters1 -> {
                waterDayWithWaters1.getWaterDay().setInserted(true);
                return waterDayWithWaters1;
            }).orElse(new WaterDayWithWaters(date, SharedPreferencesRepository.getInstance().getWaterAmountToDrink())));
//            we need to remove it so there is no issue with to many sources being loaded to the livedata
            waters.removeSource(waterDayWithWatersLiveData);
        });
    }

    public Disposable insertWater(Water water, Consumer<Long> action) {
        return getWaterDAO()
                .insertWater(water)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, RxJavaPlugins::onError);
    }

    public Disposable insertWaterDay(WaterDay waterDay, Action onSuccess) {
        return getWaterDAO().insertWaterDay(waterDay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                );
    }

    public Disposable deleteWater(Water water, Action action) {
        return getWaterDAO()
                .deleteWater(water)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, RxJavaPlugins::onError);
    }

    public void setWaterDayWithWatersValue(WaterDayWithWaters waters) {
        this.waters.setValue(waters);
    }


    public void addWaterInDay(Water water) {
//      getting waterDayWithWaters or creating it
        WaterDayWithWaters waterDayWithWaters = getWaterDayWithWatersValue();
//      getting waterDay from WaterDayWithWaters or creating it
        WaterDay waterDay = waterDayWithWaters.getWaterDay();
//      getting waters from WaterDayWithWaters or creating it
        List<Water> waters = waterDayWithWaters.getWaters();
        water.setWaterDayId(waterDay.getDate());
        if (!waters.contains(water)) {
            waters.add(water);
        }
        setWaterDayWithWatersValue(waterDayWithWaters);
    }

    public void removeWaterInDay(Water water) {
        WaterDayWithWaters waterDayWithWaters = getWaterDayWithWatersValue();
        waterDayWithWaters.getWaters().remove(water);
        this.setWaterDayWithWatersValue(waterDayWithWaters);
    }

    public MutableLiveData<WaterDayWithWaters> getWaterDayWithWatersLivaData() {
        return waters;
    }

    public WaterDayWithWaters getWaterDayWithWatersValue() {
        return Optional.ofNullable(waters.getValue())
                .orElse(new WaterDayWithWaters(LocalDate.now(), SharedPreferencesRepository.DEFAULT_WATER_AMOUNT_TO_DRINK));
    }

    public Disposable insertType(Type type, Consumer<Long> onSuccess) {
        return getWaterDAO()
                .insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, RxJavaPlugins::onError);
    }

    public Disposable updateType(Type type) {
        return getWaterDAO()
                .updateType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, RxJavaPlugins::onError);
    }

    public Disposable removeType(Type type) throws IllegalArgumentException{
        if (WaterDataValidation.validateTypeToRemove(type)) {
            throw new IllegalArgumentException(ResourcesRepository.getInstance().getDeleteTypeDefaultMessageException());
        }
        return getWaterDAO()
                .deleteType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {}, RxJavaPlugins::onError);

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
     * Sets in an active WaterDay new water amount to drink and updates WaterDay
     * @param waterAmountToDrink - new goal
     */
    public Disposable setWaterAmountToDrink(int waterAmountToDrink) {
        WaterDayWithWaters waterDayWithWatersToUpdate = getWaterDayWithWatersValue();
        waterDayWithWatersToUpdate.getWaterDay().setWaterToDrink(waterAmountToDrink);
        setWaterDayWithWatersValue(waterDayWithWatersToUpdate);
        return getWaterDAO().updateWaterDay(waterDayWithWatersToUpdate.getWaterDay()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public LiveData<List<WaterDayWithWaters>> getAllWaterDayWithWatersLiveData() {
        return allWaterDayWithWatersLiveData;
    }

    public WaterDatabase getWaterDatabase() {
        return waterDatabase;
    }
}
