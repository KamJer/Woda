package com.kamjer.woda;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.kamjer.woda.database.WaterDatabase;
import com.kamjer.woda.database.dao.WaterDAO;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.model.WaterDay;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.repository.SqlRepository;
import com.kamjer.woda.repository.WaterDataRepository;
import com.kamjer.woda.viewmodel.UserViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserViewModelUnitTest {

    UserViewModel userViewModel;

    SharedPreferencesRepository sharedPreferencesRepository;
    WaterDataRepository waterDataRepository;
    SqlRepository sqlRepository;

    CompositeDisposable disposable;

    int waterAmountToDrink;

    List<Water> waters;
    Type type;
    WaterDay waterDay;

    WaterDAO waterDAO;
    @Before
    public void setup() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        //        preparing data for tests
        type = new Type("Water", Color.BLACK);
        waterDay = new WaterDay(LocalDate.now(), 1500, true);waters = new ArrayList<>();
        waters.add(new Water(1200, type, waterDay));
        waters.add(new Water(1500, type, waterDay));

        sharedPreferencesRepository = mock(SharedPreferencesRepository.class);
        waterDataRepository = mock(WaterDataRepository.class);
        disposable = mock(CompositeDisposable.class);
        sqlRepository = mock(SqlRepository.class);
        waterDAO = mock(WaterDAO.class);

        userViewModel = new UserViewModel(disposable,
                waterDataRepository,
                sharedPreferencesRepository,
                sqlRepository);

        waterAmountToDrink = 1500;

        when(waterDataRepository.getWaterDAO()).thenReturn(waterDAO);
    }

    @Test
    public void getWaterAmountToDrink_returns1500() {
            Mockito.when(sharedPreferencesRepository.getWaterAmountToDrink()).thenReturn(waterAmountToDrink);

            int waterAmountToDrinkTest = userViewModel.getWaterAmountToDrink();
            assertEquals(waterAmountToDrinkTest, waterAmountToDrink);
    }

    @Test
    public void setWaterAmountToDrink_setWaterAmountToDrinkIdCalled() {
            AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);

            userViewModel.setWaterAmountToDrink(activity, waterAmountToDrink);
            verify(sharedPreferencesRepository, Mockito.times(1)).setWaterAmountToDrink(any(AppCompatActivity.class), anyInt());
            verify(waterDataRepository, Mockito.times(1)).setWaterAmountToDrink(waterAmountToDrink);
    }

    @Test
    public void setWaterAmountToDrinkObserver_isSetWaterAmountToDrinkObserverCalled() {
        AppCompatActivity activity = Mockito.mock(AppCompatActivity.class);
        Observer<Integer> integerObserver = integer -> {};

        userViewModel.setWaterAmountToDrinkObserver(activity, integerObserver);
        verify(sharedPreferencesRepository, Mockito.times(1)).setWaterAmountToDrinkMutableLiveDataObserver(any(AppCompatActivity.class), any());
    }

    @Test
    public void insertWatersSumWatersDeleteType_isCustomQueryCalled() {

        WaterDayWithWaters waterDayWithWaters = new WaterDayWithWaters(waterDay, waters);

//        setting up mocks
        WaterDatabase waterDatabase = mock(WaterDatabase.class);
        doNothing().when(waterDatabase).customQuery(any());
        when(sqlRepository.getSqlInsertIntoWaterValues(any())).thenReturn("test");
        when(sqlRepository.getSqlDropTempTable()).thenReturn("test");
        when(sqlRepository.getSqlCreateTempTable()).thenReturn("test");
        when(sqlRepository.getSqlDeleteFromWater()).thenReturn("test");
        when(sqlRepository.getSqlInsertIntoWaterFromTemp()).thenReturn("test");
        when(sqlRepository.getSqlDropTempTable()).thenReturn("test");
        when(sqlRepository.getSqlDeleteTypeWhereId(anyLong())).thenReturn("test");

        when(waterDataRepository.getWaterDayWithWatersValue()).thenReturn(waterDayWithWaters);
        when(waterDataRepository.getWaterDatabase()).thenReturn(waterDatabase);

//        performing test
        userViewModel.insertWatersSumWatersDeleteType(waters, type);
//        verifying results
        verify(waterDataRepository).getWaterDatabase();
    }

    @Test
    public void removeWaters_isSuccessful() {
        Type type = new Type("Water", Color.BLACK);
        WaterDay waterDay = new WaterDay(LocalDate.now(), 1500, true);
        List<Water> waters = new ArrayList<>();
        waters.add(new Water(1200, type, waterDay));
        waters.add(new Water(1500, type, waterDay));

        userViewModel.removeWaters(waters);

        verify(waterDataRepository).deleteWaters(any(), any());
    }

    @Test
    public void updateType_isUpdateTypeCalled() {
        userViewModel.updateType(type);
        verify(waterDataRepository, times(1)).updateType(type);
    }

    @Test
    public void insertType_isInsertTypeCalled() {
        userViewModel.insertType(type);
        verify(waterDataRepository, times(1)).insertType(any(), any());
    }

    @Test
    public void removeType_isRemoveTypeCalled() {
        userViewModel.removeType(type);
        verify(waterDataRepository, times(1)).removeType(type);
    }

    @Test
    public void getTypes_returnsHashMapOfTypes() {
        HashMap<Long, Type> typesToTest = new HashMap<>();

        when(waterDataRepository.getWaterTypes()).thenReturn(typesToTest);
        HashMap<Long, Type> map = userViewModel.getTypes();

        assertEquals(typesToTest, map);
    }

    @Test
    public void setTypesObserver_isSetTypesLiveDataObserverCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Observer<HashMap<Long, Type>> observer = longTypeHashMap -> {};
        userViewModel.setTypesObserver(activity, observer);
        verify(waterDataRepository).setTypesLiveDataObserver(any(), any());
    }

    @Test
    public void loadWatersByType_isGetWaterByTypeCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        LiveData<List<Water>> livedataMock = mock(LiveData.class);
        when(waterDAO.getWaterByType(anyLong())).thenReturn(livedataMock);
        doNothing().when(livedataMock).observe(any(), any());
        userViewModel.loadWatersByType(type, activity, waters1 -> {});
        verify(waterDAO).getWaterByType(anyLong());
    }

    @Test
    public void clearDisposable_isClearCalled() {
        userViewModel.clearDisposable();
        verify(disposable, times(1)).clear();
    }
}
