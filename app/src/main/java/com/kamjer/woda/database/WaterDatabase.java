package com.kamjer.woda.database;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kamjer.woda.database.converters.Converter;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.WaterDay;
import androidx.room.Database;

@Database(entities = {Water.class, WaterDay.class, Type.class}, version = 1)
@TypeConverters(Converter.class)
public abstract class WaterDatabase extends RoomDatabase {

    public abstract WaterDAO waterDAO();
}
