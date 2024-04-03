package com.kamjer.woda.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "type")
public class Type implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String type;

    private Integer color;

    public Type() {
    }

    @Ignore
    public Type (String type, int color) {
        this.type = type;
        this.color = color;
    }

    @Ignore
    public Type(long id, String type, int color) {
        this.id = id;
        this.type = type;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Type)) {
            return false;
        }
        return obj.hashCode() == this.hashCode();
    }
}
