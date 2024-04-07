package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.SqlRepository;
import com.kamjer.woda.repository.WaterDataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends ViewModel {

    private final CompositeDisposable disposable = new CompositeDisposable();

    public int getWaterAmountToDrink() {
        return SharedPreferencesRepository.getInstance().getWaterAmountToDrink();
    }
    public void setWaterAmountToDrink(Context applicationContext, int waterAmountToDrink) {
        SharedPreferencesRepository.getInstance().setWaterAmountToDrink(applicationContext, waterAmountToDrink);
    }

    public void setWaterAmountToDrinkObserver(LifecycleOwner owner, Observer<Integer> observer) {
        SharedPreferencesRepository.getInstance().getWaterAmountToDrinkLiveData().observe(owner, observer);
    }

    /**
     * Custom sql transaction that inserts passed waters to the database,
     * sums all waters in a database and after all of that deletes passed type
     * @param waters list of waters to insert
     * @param type type to delete
     */
    public void insertWatersSumWatersDeleteType(List<Water> waters, Type type) {
        List<String> sql = new ArrayList<>();
//        preparing sql statements
        waters.forEach(water -> sql.add(SqlRepository.getInstance().getSqlInsertIntoWaterValues(water)));
        sql.add(SqlRepository.getInstance().getSqlDropTable());
        sql.add(SqlRepository.getInstance().getSqlCreateTempTable());
        sql.add(SqlRepository.getInstance().getSqlDeleteFromWater());
        sql.add(SqlRepository.getInstance().getSqlInsertIntoWaterFromTemp());
        sql.add(SqlRepository.getInstance().getSqlDropTable());
        sql.add(SqlRepository.getInstance().getSqlDeleteTypeWhereId(type.getId()));
        disposable.add(Completable.fromAction(() -> WaterDataRepository.getInstance().getWaterDatabase().customQuery(sql))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> WaterDataRepository.getInstance().removeWaterType(type.getId()),
                        RxJavaPlugins::onError
                ));
    }

    public void removeWaters(List<Water>waters) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().deleteWaters(waters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            waters.forEach(water -> {
                                if (WaterDataRepository.getInstance().getWaterDayValue().getWaters().contains(water)) {
                                    WaterDataRepository.getInstance().removeWaterValue(water);
                                }
                            });
                        },
                        RxJavaPlugins::onError));
    }

    private void putType(Type type) {
        WaterDataRepository.getInstance().putType(type);
    }

    public void updateType(Type type) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO()
                .updateType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    putType(type);
                },
                        RxJavaPlugins::onError));
    }

    public void insertType(Type type) {
        insertType(type, aLong -> {
            type.setId(aLong);
            putType(type);
        });
    }

    public void insertType(Type type, Consumer<Long> onSuccess) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public void removeType(Type type) {
        removeType(type, () -> WaterDataRepository.getInstance().removeWaterType(type.getId()));
    }

    public void removeType(Type type, Action action) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().deleteType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action));
    }

    /**
     * Returns new HashMap of Types, changing it does not effect original Map
     * @return new map of Types
     */
    public HashMap<Long, Type> getTypes() {
        return WaterDataRepository.getInstance().getWaterTypes();
    }

    public void setTypesObserver(LifecycleOwner owner, Observer<HashMap<Long, Type>> observer) {
        WaterDataRepository.getInstance().setTypesLiveDataObserver(owner, observer);
    }

    public void loadWatersByType(Type type, Consumer<List<Water>> onSuccess) {
        disposable.add(WaterDataRepository.getInstance().getWaterDAO().getWaterByType(type.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                ));
    }

    public void clearDisposable() {
        disposable.clear();
    }
}
