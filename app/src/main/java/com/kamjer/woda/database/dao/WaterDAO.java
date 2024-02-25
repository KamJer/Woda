package com.kamjer.woda.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface WaterDAO {

//  Water table methods
//  fetching
    @Query("SELECT * FROM water")
    Flowable<List<Water>> getAllWaters();

    @Query("SELECT * FROM water WHERE id = :waterId")
    Single<Water> getWaterById(long waterId);

    @Query("SELECT * FROM water INNER JOIN water_day ON water.waterDayId = water_day.id WHERE date = :date")
    Flowable<List<Water>> getWaterByDate(String date);

//  inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insertWater(Water water);

//  deletes
    @Delete
    Completable deleteWater(Water water);

//    Type table methods
    @Query("SELECT * FROM type")
    Flowable<List<Type>> getAllTypes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insertType(Type type);

    @Delete
    Completable deleteType(Type type);

//    WaterDay
//    fetching
    @Query("SELECT * FROM water_day WHERE date = :date")
    Single<WaterDay> getWaterDayByDate(String date);

//    insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insertWaterDay(WaterDay waterDay);

//    WaterDayWithWater
    @Transaction
    @Query("SELECT * FROM water_day WHERE date = :date")
    Maybe<WaterDayWithWaters> getWaterDayWitWatersByDate(String date);

    @Query("SELECT * FROM water_day")
    Flowable<List<WaterDayWithWaters>> getAllWaterDayWitWatersByDate();
}
