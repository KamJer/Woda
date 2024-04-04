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
public abstract class WaterDAO {

//  Water table methods
//  fetching

    @Transaction
    @Query("SELECT * FROM water WHERE typeId = :typeId")
    public abstract Flowable<List<Water>> getWaterByType(long typeId);

    @Query("SELECT * FROM water WHERE id = :waterId")
    public abstract Single<Water> getWaterById(long waterId);

//  inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<Long> insertWater(Water water);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<List<Long>> insertWaters(List<Water> waters);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertWatersAndDeleteType(List<Water> waters, Type type);

    @Transaction
    public void sumWaters(List<Water> waters, Type type) {
        insertWater(waters.get(0));
        deleteType(type);
    }

//  deletes
    @Delete
    public abstract Completable deleteWater(Water water);

    @Delete
    public abstract Completable deleteWaters(List<Water> waters);

//    Type table methods

    @Transaction
    @Query("SELECT * FROM type")
    public abstract Flowable<List<Type>> getAllTypes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<Long> insertType(Type type);

    @Delete
    public abstract Completable deleteType(Type type);

//    WaterDay
//    fetching
    @Query("SELECT * FROM water_day WHERE date = :date")
    public abstract Single<WaterDay> getWaterDayByDate(String date);

//    insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<Long> insertWaterDay(WaterDay waterDay);

//    WaterDayWithWater
    @Query("SELECT * FROM water_day WHERE date = :date")
    public abstract Maybe<WaterDayWithWaters> getWaterDayWitWatersByDate(String date);

    @Transaction
    @Query("SELECT * FROM water_day")
    public abstract Flowable<List<WaterDayWithWaters>> getAllWaterDayWitWatersByDate();


}
