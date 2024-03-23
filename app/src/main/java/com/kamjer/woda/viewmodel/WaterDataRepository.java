package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
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
    private int waterAmountToDrink;
    private final MutableLiveData<WaterDayWithWaters> waters = new MutableLiveData<>();

    private HashMap<Long, Type> waterTypes = new HashMap<>();

    private Type[] defaultDrinksTypes;
    private boolean notificationsActive;
    private LocalTime selectedNotificationsTime;
    private LocalTime constraintNotificationTimeStart;
    private LocalTime constraintNotificationTimeEnd;

    public WaterDataRepository() {
    }

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

    public void putType(Type type) {
        waterTypes.put(type.getId(), type);
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

    public WaterDAO getWaterDAO() {
        return waterDAO;
    }

    public WaterDayWithWaters getWaterDayValue() {
        return Optional.ofNullable(waters.getValue()).orElse(new WaterDayWithWaters(LocalDate.now(), WaterViewModel.DEFAULT_WATER_TO_DRINK));
    }
    public Map<Long, Type> getWaterTypes() {
        return waterTypes;
    }

    public void setWaterTypes(Map<Long, Type> waterTypes) {
        this.waterTypes = (HashMap<Long, Type>) waterTypes;
    }

    public int getWaterAmountToDrink() {
        return waterAmountToDrink;
    }

    private void setWaterToDrinkWaterDay(int waterToDrink) {
        WaterDayWithWaters waterDayWithWatersToUpdate = getWaterDayValue();
        waterDayWithWatersToUpdate.getWaterDay().setWaterToDrink(waterToDrink);
        waters.setValue(waterDayWithWatersToUpdate);
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
        this.waterAmountToDrink = waterAmountToDrink;
        setWaterToDrinkWaterDay(waterAmountToDrink);
    }

    public static List<Type> containsDefaultTypes(Type[] typeArray, Map<Long, Type> typeMap) {
        List<Type> test = new ArrayList<>();
//      loop through default drink types and check if it is in a database if it is not, add not found type to the list
        for (Type type : typeArray) {
            if (!typeMap.containsValue(type)) {
                test.add(type);
            }
        }
//      if types in a database does not contain default ones return those types that are not in a database, if database contains all of a default types list will have length of 0
        return test;
    }

    public Type[] getDefaultDrinksTypes() {
        return defaultDrinksTypes;
    }

    public List<Type> getUsedTypes(List<Water> waters) {
        return waters.stream().map(water -> getWaterTypes().get(water.getTypeId())).collect(Collectors.toList());
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
}
