package com.kamjer.woda.model;

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

    @Ignore
    public Type() {
    }

    @Ignore
    public Type (String type) {
        this.type = type;
    }

    public Type(long id, String type) {
        this.id = id;
        this.type = type;
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
}
