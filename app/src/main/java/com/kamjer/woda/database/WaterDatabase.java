package com.kamjer.woda.database;

import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.kamjer.woda.converters.Converter;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.dao.WaterDAO;

@androidx.room.Database(entities = {Water.class}, version = 1, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class WaterDatabase extends RoomDatabase {
    public abstract WaterDAO waterDAO();
}
