package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.kamjer.woda.R;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WaterDataRepository {

    private static WaterDataRepository instance;

    public static final String DATABASE_NAME = "water";

    private WaterDatabase waterDatabase;
    private WaterDAO waterDAO;
    private final MutableLiveData<WaterDayWithWaters> waters = new MutableLiveData<>();

    private final MutableLiveData<HashMap<Long, Type>> waterTypes = new MutableLiveData<>();

    private Type[] defaultDrinksTypes;
    private boolean notificationsActive;
    private LocalTime selectedNotificationsTime;
    private LocalTime constraintNotificationTimeStart;
    private LocalTime constraintNotificationTimeEnd;

    public void loadDefaultTypes(Context context) {
        String[] defaultDrinksTypesText = context.getResources().getStringArray(R.array.default_types);
        int[] defaultDrinksTypesColor = context.getResources().getIntArray(R.array.types_default_color);
        defaultDrinksTypes = new Type[defaultDrinksTypesText.length];
        for (int i = 0; i < defaultDrinksTypesText.length; i++) {
            defaultDrinksTypes[i] = new Type(defaultDrinksTypesText[i], defaultDrinksTypesColor[i]);
        }
    }

    public void createWaterDatabase(Context context) {
//        if database does not exists already create a new one
        if (waterDatabase == null) {
            this.waterDatabase = Room.databaseBuilder(context, WaterDatabase.class, WaterDataRepository.DATABASE_NAME)
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
        WaterDayWithWaters waterDayWithWaters = Optional.ofNullable(this.waters.getValue())
                .orElseGet(() -> new WaterDayWithWaters(LocalDate.now(), WaterViewModel.DEFAULT_WATER_TO_DRINK));
        waterDayWithWaters.getWaters().remove(water);
        this.waters.setValue(waterDayWithWaters);
    }

    public void setWatersInADayValue(List<Water> waters) {
        WaterDayWithWaters waterDayValue = getWaterDayValue();
        waterDayValue.setWaters(waters);
        setWatersValue(waterDayValue);
    }

    public WaterDAO getWaterDAO() {
        return waterDAO;
    }

    public WaterDayWithWaters getWaterDayValue() {
        return Optional.ofNullable(waters.getValue())
                .orElse(new WaterDayWithWaters(LocalDate.now(), WaterViewModel.DEFAULT_WATER_TO_DRINK));
    }

    /**
     * Removes a type, filters all of waters in a active day and removes them from that day
     * @param id of a type to remove
     */
    public void removeWaterType(long id) {
        HashMap<Long, Type> waterTypes = getWaterTypes();
        waterTypes.remove(id);
        this.waterTypes.setValue(waterTypes);
//      checking if there are any waters with passed type and deleting them from a list and setting to a day
        List<Water> watersFiltered = getWaterDayValue()
                .getWaters()
                .stream()
                .filter(water -> water.getTypeId() != id)
                .collect(Collectors.toList());
        setWatersInADayValue(watersFiltered);
    }

    /**
     * Puts type in live data
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
     * Removes observers from a type liveData
     * @param owner owner of a observer to set
     */
    public void removeTypeLiveDataObserver(LifecycleOwner owner) {
        waterTypes.removeObservers(owner);
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
     * @param typeArray - types to check
     * @param typeMap - map of types to compare to
     * @return list of Types not contained in a map, if all types from an array are contained in a map list length is 0
     */
    public static List<Type> containsDefaultTypes(Type[] typeArray, Map<Long, Type> typeMap) {
        List<Type> test = new ArrayList<>();
//      loop through default drink types and check if it is in a database if it is not, add not found type to the list
        for (Type type : typeArray) {
            if (!typeMap.containsValue(type)) {
                test.add(type);
            }
        }
//      if types in a database does not contain default ones return those types that are not in a database,
//      if database contains all of a default types list will have length of 0
        return test;
    }

    /**
     * Returns default types of water
     * @return default types loaded on start of an app, if there are no default types loaded, returns empty array of types
     */
    public Type[] getDefaultDrinksTypes() {
        return Optional.ofNullable(defaultDrinksTypes).orElse(new Type[0]);
    }

    /**
     * Finds all of used types in a passed list of waters
     * @param waters list of waters to check
     * @return list of maps
     */
    public List<Type> getUsedTypes(List<Water> waters) {
        return waters.stream().map(water -> getWaterTypes().get(water.getTypeId())).collect(Collectors.toList());
    }

    /**
     * Returns the amount of water to drink to achieve set goal, returns value from active waterDay
     * @return value of water to drink
     */
    public int getWaterAmountToDrink() {
        return getWaterDayValue().getWaterDay().getWaterToDrink();
    }

    /**
     * Sets in an active WaterDay new water amount to drink
     * @param waterAmountToDrink - new goal
     */
    public void setWaterAmountToDrink(int waterAmountToDrink) {
        WaterDayWithWaters waterDayWithWatersToUpdate = getWaterDayValue();
        waterDayWithWatersToUpdate.getWaterDay().setWaterToDrink(waterAmountToDrink);
        waters.setValue(waterDayWithWatersToUpdate);
    }

    public void setNotificationsActive(boolean isNotificationsActive) {
        this.notificationsActive = isNotificationsActive;
    }

    public boolean isNotificationsActive() {
        return notificationsActive;
    }

    public LocalTime getSelectedNotificationsTime() {
        return selectedNotificationsTime;
    }

    public void setSelectedNotificationsTime(LocalTime selectedNotificationsTime) {
        this.selectedNotificationsTime = selectedNotificationsTime;
    }

    public LocalTime getConstraintNotificationTimeStart() {
        return constraintNotificationTimeStart;
    }

    public void setConstraintNotificationTimeStart(LocalTime constraintNotificationTimeStart) {
        this.constraintNotificationTimeStart = constraintNotificationTimeStart;
    }

    public LocalTime getConstraintNotificationTimeEnd() {
        return constraintNotificationTimeEnd;
    }

    public void setConstraintNotificationTimeEnd(LocalTime constraintNotificationTimeEnd) {
        this.constraintNotificationTimeEnd = constraintNotificationTimeEnd;
    }

    public WaterDatabase getWaterDatabase() {
        return waterDatabase;
    }
}
