package com.kamjer.woda.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kamjer.woda.model.Water;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface WaterDAO {

    @Query("SELECT * FROM water")
    Flowable<List<Water>> getAll();

    @Query("SELECT * FROM water WHERE strftime('%Y-%m', date ==:date)")
    Flowable<List<Water>> getFromMonth(String date);

    @Query("SELECT * FROM water WHERE id == :waterId")
    Single<Water> getWaterById(long waterId);

    @Query("SELECT * FROM water WHERE date == :date")
    Maybe<Water> getWaterByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertWater(Water water);

    @Delete
    Completable deleteWater(Water water);



}
