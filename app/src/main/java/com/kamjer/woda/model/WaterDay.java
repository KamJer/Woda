package com.kamjer.woda.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(tableName = "water_day")
public class WaterDay implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private LocalDate date;

    private Integer waterToDrink;

    @Ignore
    private boolean inserted;

    public WaterDay() {
        this.inserted = false;
    }

    public WaterDay(LocalDate date, int waterAmountToDrink, boolean inserted) {
        this.date = date;
        this.waterToDrink = waterAmountToDrink;
        this.inserted = inserted;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getWaterToDrink() {
        return waterToDrink;
    }

    public void setWaterToDrink(Integer waterToDrink) {
        this.waterToDrink = waterToDrink;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }
}
