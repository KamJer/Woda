package com.kamjer.woda.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface WaterDAO {

//    Water table methods
    @Query("SELECT * FROM water")
    Flowable<List<Water>> getAllWaters();

    @Query("SELECT * FROM water WHERE strftime('%Y-%m', date) =:date")
    Flowable<List<Water>> getWatersFromMonth(String date);

    @Query("SELECT * FROM water WHERE id = :waterId")
    Single<Water> getWaterById(long waterId);

    @Query("SELECT * FROM water WHERE date = :date")
    Flowable<List<Water>> getWaterByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertWater(Water water);

    @Delete
    Completable deleteWater(Water water);

//    Type table methods
    @Query("SELECT * FROM type")
    Flowable<List<Type>> getAllTypes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> insertType(Type type);

    @Delete
    Completable deleteType(Type type);
}
