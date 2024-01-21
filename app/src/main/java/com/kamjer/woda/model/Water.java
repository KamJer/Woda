package com.kamjer.woda.model;

import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(tableName = "water")
public class Water implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private int waterToDrink;
    private int waterDrank;
    private LocalDate date;

    public Water() {
        this.waterToDrink = 0;
        this.waterDrank = 0;
    }

    public Water(LocalDate date, int waterToDrink, int waterDrank) {
        this.waterToDrink = waterToDrink;
        this.waterDrank = waterDrank;
        this.date = date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getWaterToDrink() {
        return waterToDrink;
    }

    public void setWaterToDrink(int waterToDrink) {
        this.waterToDrink = waterToDrink;
    }

    public int getWaterDrank() {
        return waterDrank;
    }

    public void setWaterDrank(int waterDrank) {
        this.waterDrank = waterDrank;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
