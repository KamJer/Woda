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

@Database(entities = {Water.class, WaterDay.class, Type.class}, version = 3)
@TypeConverters(Converter.class)
public abstract class WaterDatabase extends RoomDatabase {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//          creating type table
            database.execSQL("CREATE TABLE IF NOT EXISTS type (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "type TEXT)");
//          inserting default data in to it
            database.execSQL("INSERT INTO type (type) VALUES ('Water'), ('Coffee'), ('Tea'), ('Soda')");
//          changing old water table name
            database.execSQL("ALTER TABLE water RENAME TO water_old");
//          creating new altered water table
            database.execSQL("CREATE TABLE IF NOT EXISTS water (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "waterDrank INTEGER," +
                    "waterToDrink INTEGER," +
                    "date TEXT," +
                    "typeId INTEGER NOT NULL," +
                    "FOREIGN KEY (typeId) REFERENCES type(id) ON DELETE CASCADE" +
                    ")");
//          inserting old data to new table and setting drink type to 1 (since types are created here "Water" will always have id 1)
            database.execSQL("INSERT INTO water (waterDrank, waterToDrink, date, typeId) SELECT waterDrank, waterToDrink, date, 1 FROM water_old");
//          deleting old table
            database.execSQL("DROP TABLE water_old");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//          creating waterDay table
            database.execSQL("CREATE TABLE IF NOT EXISTS water_day (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "date TEXT," +
                    "waterToDrink INTEGER" +
                    ")");
//          changing old water table name
            database.execSQL("ALTER TABLE water RENAME TO water_old");
//          creating new altered water table
            database.execSQL("CREATE TABLE IF NOT EXISTS water (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "waterDrank INTEGER," +
                    "typeId INTEGER NOT NULL," +
                    "waterDayId INTEGER NOT NULL," +
                    "FOREIGN KEY (typeId) REFERENCES type(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (waterDayId) REFERENCES water_day(id) ON DELETE CASCADE" +
                    ")");
//          inserting old data to new table and setting drink type to 1 (since types are created here "Water" will always have id 1)
            database.execSQL("INSERT INTO water_day (waterToDrink, date) SELECT waterToDrink, date FROM water_old");
            database.execSQL("INSERT INTO water (waterDrank, typeId, waterDayId) " +
                    "SELECT water_old.waterDrank, water_old.typeId, water_day.id " +
                    "FROM water_old " +
                    "JOIN water_day ON water_old.date = water_day.date;");
//          deleting old table
            database.execSQL("DROP TABLE water_old");
        }
    };

    public abstract WaterDAO waterDAO();
}
