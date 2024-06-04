package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.SqlRepository;
import com.kamjer.woda.repository.WaterDataRepository;
import com.kamjer.woda.utils.exception.TypeToRemoveCanNotBeDefaultException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModel extends ViewModel {


    private final SqlRepository sqlRepository;
    private final WaterDataRepository waterDataRepository;
    private final SharedPreferencesRepository sharedPreferencesRepository;

    private LiveData<List<Water>> watersByTypeLiveData;

    private final CompositeDisposable disposable;

    public static final ViewModelInitializer<UserViewModel> initializer = new ViewModelInitializer<>(
            UserViewModel.class,
            creationExtras ->
                    new UserViewModel(new CompositeDisposable(),
                            WaterDataRepository.getInstance(),
                            SharedPreferencesRepository.getInstance(),
                            SqlRepository.getInstance())
    );

    public UserViewModel(CompositeDisposable compositeDisposable,
                         WaterDataRepository waterDataRepository,
                         SharedPreferencesRepository sharedPreferencesRepository,
                         SqlRepository sqlRepository) {
        this.disposable = compositeDisposable;
        this.waterDataRepository = waterDataRepository;
        this.sharedPreferencesRepository = sharedPreferencesRepository;
        this.sqlRepository = sqlRepository;
    }



    public int getWaterAmountToDrink() {
        return sharedPreferencesRepository.getWaterAmountToDrink();
    }
    public void setWaterAmountToDrink(Context applicationContext, int waterAmountToDrink) {
        sharedPreferencesRepository.setWaterAmountToDrink(applicationContext, waterAmountToDrink);
        disposable.add(waterDataRepository.setWaterAmountToDrink(waterAmountToDrink));
    }

    public void setWaterAmountToDrinkObserver(LifecycleOwner owner, Observer<Integer> observer) {
        sharedPreferencesRepository.setWaterAmountToDrinkMutableLiveDataObserver(owner, observer);
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
        waters.forEach(water -> sql.add(sqlRepository.getSqlInsertIntoWaterValues(water)));
        sql.add(sqlRepository.getSqlDropTempTable());
        sql.add(sqlRepository.getSqlCreateTempTable());
        sql.add(sqlRepository.getSqlDeleteFromWater());
        sql.add(sqlRepository.getSqlInsertIntoWaterFromTemp());
        sql.add(sqlRepository.getSqlDropTempTable());
        sql.add(sqlRepository.getSqlDeleteTypeWhereId(type.getId()));
        disposable.add(Completable.fromAction(() -> waterDataRepository.getWaterDatabase().customQuery(sql))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {
//                          notifying relevant live data data changed since this action happens outside context of room and will not happen automatically
                            waterDataRepository.getAllTypes();
                            waterDataRepository.loadWaterDayWithWatersByDate(waterDataRepository.getWaterDayWithWatersValue().getWaterDay().getDate());
                            waterDataRepository.loadAllWaterDayWithWaters();},
                        RxJavaPlugins::onError
                ));
    }

    public void removeWaters(List<Water>waters) {
        disposable.add(waterDataRepository.deleteWaters(waters, () -> waters.forEach(water -> {
            if (waterDataRepository.getWaterDayWithWatersValue().getWaters().contains(water)) {
                waterDataRepository.removeWaterInDay(water);
            }
        })));
    }

    public void updateType(Type type) {
        disposable.add(waterDataRepository.updateType(type));
    }
    public void insertType(Type type) {
        disposable.add(waterDataRepository.insertType(type, type::setId));
    }

    public void removeType(Type type) throws TypeToRemoveCanNotBeDefaultException {
        disposable.add(waterDataRepository.removeType(type));
    }

    /**
     * Returns new HashMap of Types, changing it does not effect original Map
     * @return new map of Types
     */
    public HashMap<Long, Type> getTypes() {
        return waterDataRepository.getWaterTypes();
    }

    public void setTypesObserver(LifecycleOwner owner, Observer<HashMap<Long, Type>> observer) {
        waterDataRepository.setTypesLiveDataObserver(owner, observer);
    }

    public void loadWatersByType(Type type, LifecycleOwner owner,  Observer<List<Water>> onSuccess) {
        watersByTypeLiveData = waterDataRepository.getWaterDAO().getWaterByType(type.getId());
        watersByTypeLiveData.observe(owner, onSuccess);
    }

    public void setWatersByTypeLiveDataObserver(LifecycleOwner owner, Observer<List<Water>> observer) {
        watersByTypeLiveData.observe(owner, observer);
    }

    public void clearDisposable() {
        disposable.clear();
    }
}
