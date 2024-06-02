package com.kamjer.woda.database;

import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kamjer.woda.database.converters.Converter;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.WaterDay;

import androidx.room.Database;

import java.util.List;

@Database(entities = {Water.class, WaterDay.class, Type.class}, version = 1)
@TypeConverters(Converter.class)
public abstract class WaterDatabase extends RoomDatabase {
    public abstract WaterDAO waterDAO();

    /**
     * Performs custom Query on a database not necessarily suporrted by Room
     *
     * @param sql list of sql commends to perform
     */
    public void customQuery(List<String> sql) {
        SupportSQLiteDatabase database = this.getOpenHelper().getReadableDatabase();
        database.beginTransaction();
        try {
            sql.forEach(database::execSQL);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }
}
