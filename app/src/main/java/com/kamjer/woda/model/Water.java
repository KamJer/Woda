package com.kamjer.woda.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(tableName = "water",
        foreignKeys = {@ForeignKey(
                entity = Type.class,
                parentColumns = "id",
                childColumns = "typeId",
                onDelete = ForeignKey.CASCADE)})
public class Water implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private Integer waterToDrink;
    private Integer waterDrank;
    private LocalDate date;
    private long typeId;

    public Water() {
        this.waterToDrink = 0;
        this.waterDrank = 0;
    }

    public Water(LocalDate date, int waterToDrink, int waterDrank, Type type) {
        this.waterToDrink = waterToDrink;
        this.waterDrank = waterDrank;
        this.date = date;
        this.typeId = type.getId();
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

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }
}
