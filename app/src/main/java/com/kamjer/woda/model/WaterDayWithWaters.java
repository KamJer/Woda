package com.kamjer.woda.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WaterDayWithWaters implements Serializable {
    @Embedded
    private WaterDay waterDay;

    @Relation(
            parentColumn = "date",
            entityColumn = "waterDayId"
    )
    private List<Water> waters;

    public WaterDayWithWaters(LocalDate date, int waterToDrink) {
        waterDay = new WaterDay(date, waterToDrink, false);
        waters = new ArrayList<>();
    }

    public WaterDayWithWaters(WaterDay waterDay, List<Water> waters) {
        this.waterDay = waterDay;
        this.waters = waters;
    }

    public WaterDay getWaterDay() {
        return waterDay;
    }

    public void setWaterDay(WaterDay waterDay) {
        this.waterDay = waterDay;
    }

    public List<Water> getWaters() {
        return waters;
    }

    public void setWaters(List<Water> waters) {
        this.waters = waters;
    }

    public Integer getWaterDaySum() {
        return waters.stream().mapToInt(Water::getWaterDrank).sum();
    }
}
