package com.kamjer.woda.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

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

//  inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<Long> insertWater(Water water);

//  deletes
    @Delete
    public abstract Completable deleteWater(Water water);

    @Delete
    public abstract Completable deleteWaters(List<Water> waters);

//  Type table methods
//  fetching
    @Transaction
    @Query("SELECT * FROM type")
    public abstract LiveData<List<Type>> getAllTypes();

//  inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<Long> insertType(Type type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Maybe<List<Long>> insertTypes(List<Type> types);

    @Update
    public abstract Completable updateType(Type type);

//  deletes
    @Delete
    public abstract Completable deleteType(Type type);

//  WaterDay table methods
//  insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Completable insertWaterDay(WaterDay waterDay);

    @Update
    public abstract Completable updateWaterDay(WaterDay waterDay);

//  WaterDayWithWater
//  fetching
    @Query("SELECT * FROM water_day WHERE date = :date")
    public abstract LiveData<WaterDayWithWaters> getWaterDayWithWatersByDate(String date);

    @Transaction
    @Query("SELECT * FROM water_day")
    public abstract LiveData<List<WaterDayWithWaters>> getAllWaterDayWithWaters();

}
