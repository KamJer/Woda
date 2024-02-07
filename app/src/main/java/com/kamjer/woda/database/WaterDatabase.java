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
import com.kamjer.woda.viewmodel.WaterDataRepository;

import java.util.Arrays;

@androidx.room.Database(entities = {Water.class, Type.class}, version = 2)
@TypeConverters(Converter.class)
public abstract class WaterDatabase extends RoomDatabase {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("INSERT INTO type (type) VALUES ('Water'), ('Coffee'), ('Tea'), ('Soda')");
            database.execSQL("ALTER TABLE water RENAME TO water_old");
            database.execSQL("CREATE TABLE IF NOT EXISTS water (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "waterToDrink INTEGER," +
                    "waterDrank INTEGER," +
                    "date TEXT," +
                    "typeId INTEGER," +
                    "FOREIGN KEY (typeId) REFERENCES Type(id) ON DELETE CASCADE" +
                    ")");
            database.execSQL("INSERT INTO water SELECT * FROM water_old");
            database.execSQL("DROP TABLE water_old");
        }
    };

    public abstract WaterDAO waterDAO();
}
