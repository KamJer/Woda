package com.kamjer.woda.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WaterDay {
    private LocalDate date;

    private Integer waterAmountToDrink;

    private List<Water> water = new ArrayList<>();

    public WaterDay() {
    }

    public WaterDay(LocalDate date, int waterAmountToDrink, List<Water> water) {
        this.date = date;
        this.waterAmountToDrink = waterAmountToDrink;
        this.water = water;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Water> getWater() {
        return water;
    }

    public void setWater(List<Water> water) {
        this.water = water;
    }

    public Integer getWaterAmountToDrink() {
        return waterAmountToDrink;
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
        this.waterAmountToDrink = waterAmountToDrink;

        water.forEach(water -> water.setWaterToDrink(waterAmountToDrink));
    }
}
