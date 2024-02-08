package com.kamjer.woda.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaterViewModel extends ViewModel {

    public static final int DEFAULT_WATER_TO_DRINK = 1500;
    public static final int DEFAULT_WATER_DRANK_IN_ONE_GO = 250;


    public void createDataBase(Context context) {
        WaterDataRepository.getInstance().createWaterDatabase(context);
        Disposable disposable = WaterDataRepository.getInstance().getWaterDAO().getAllTypes().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(types -> {
                    WaterDataRepository.getInstance().setWaterTypes(types.stream().collect(Collectors.toMap(
                            Type::getType,
                            type -> type)));
//                  if this map is empty it means something went wrong and values that are supposed to be in a database are not, fetching default values and inserting them there to be sure they are there
                    if (WaterDataRepository.getInstance().getWaterTypes().isEmpty()) {
                        Arrays.stream(WaterDataRepository.DEFAULT_DRINKS_TYPES).
                                forEach(s -> this.insertType(new Type(s)));
                    }
                });
    }

    public void loadWaterAmount(AppCompatActivity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        int test = sharedPref.getInt(activity.getString(R.string.water_amount_to_drink), DEFAULT_WATER_TO_DRINK);
        WaterDataRepository.getInstance().setWaterAmountToDrink(test);
    }

    public Disposable loadWaterById(long id, Consumer<Water> onSuccess) {
        return WaterDataRepository.getInstance().getWaterDAO().getWaterById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError
                );
    }

    public void loadWaterByDate(LocalDate date) {
        setActiveDate(date);
        Disposable waterByDateDisposable =  WaterDataRepository.getInstance().getWaterDAO().getWaterByDate(date.toString()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::setWaterValue,
                        RxJavaPlugins::onError
                );
    }

    public Disposable loadWaterAll(Consumer<List<Water>> onSuccess) {
        return WaterDataRepository.getInstance().getWaterDAO().getAllWaters().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError);
    }

    public Disposable loadWatersFromMonth(LocalDate date, Consumer<List<Water>> onSuccess) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedDate = date.format(formatter);
        return WaterDataRepository.getInstance().getWaterDAO().getWatersFromMonth(formattedDate).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onSuccess,
                        RxJavaPlugins::onError);
    }

//    private void setWaterWhenNotFound(LocalDate date) {
////      getting water amount to drink
//        int waterAmountToDrink = getWaterAmountToDrink();
//        Map<String, Type> types = WaterDataRepository.getInstance().getWaterTypes();
////      creating water with default type (for now)
////      TODO:temporary
//        Water waterCreated = new Water(date, waterAmountToDrink, 0, );
////      inserting that water to database and inside of it adding to liveData
//        insertWater(waterCreated);
//    }

    public void insertWater(Water water) {
        Disposable waterDisposable   = WaterDataRepository.getInstance().getWaterDAO().insertWater(water).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(() ->
//                      adds water to the list in livedata
                        addWater(water),
                        RxJavaPlugins::onError
                );
    }

    public void insertType(Type type) {
        Disposable disposable = WaterDataRepository.getInstance().getWaterDAO().insertType(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((typeId) -> {
                            type.setId(typeId);
                            putType(type);
                        },
                    RxJavaPlugins::onError
                );
    }

    private void putType(Type type) {
        WaterDataRepository.getInstance().putType(type);
    }

    public MutableLiveData<List<Water>> getWater() {
        return WaterDataRepository.getInstance().getWaters();
    }

    public void addWater(Water water) {
        WaterDataRepository.getInstance().addWaterValue(water);
    }

    public void setWaterValue(List<Water> waters) {
        WaterDataRepository.getInstance().getWaters().setValue(waters);
    }

    public List<Water> getWaterValue() {
        return Optional.ofNullable(WaterDataRepository.getInstance().getWaters().getValue()).orElseGet(ArrayList::new);
    }

    public int getWaterAmountToDrink() {
        return Optional.ofNullable(WaterDataRepository.getInstance().getWaterAmountToDrink()).orElseGet(() -> DEFAULT_WATER_TO_DRINK);
    }

    public void setWaterAmountToDrink(int waterAmountToDrink) {
//        TODO:fix it
        WaterDataRepository.getInstance().setWaterAmountToDrink(waterAmountToDrink);
        WaterDataRepository.getInstance().setWaters(getWaterValue().stream()
                .peek(water -> water.setWaterToDrink(waterAmountToDrink))
                .collect(Collectors.toList()));
    }

    public void deleteWater(Water water) {
        WaterDataRepository.getInstance().deleteWater(water);
    }

    public Map<String, Type> getWaterTypes() {
        return WaterDataRepository.getInstance().getWaterTypes();
    }

    public void setActiveDate(LocalDate date) {
        WaterDataRepository.getInstance().setActiveDate(date);
    }

    public LocalDate getActiveDate() {
        return WaterDataRepository.getInstance().getActiveDate();
    }
}
