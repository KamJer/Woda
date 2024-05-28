package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.kamjer.woda.repository.SharedPreferencesRepository;
import com.kamjer.woda.viewmodel.AlarmViewModel;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;

public class AlarmViewModelUnitTest {

    AlarmViewModel alarmViewModel;

    SharedPreferencesRepository sharedPreferencesRepository;

    MutableLiveData<LocalTime> selectedNotificationsTimeLiveDataMock;
    MutableLiveData<Boolean> notificationsActiveLiveDataMock;
    MutableLiveData<Integer> hourNotificationPeriodLiveDataMock;
    MutableLiveData<LocalTime> constraintNotificationTimeStartLiveDataMock;
    MutableLiveData<LocalTime> constraintNotificationTimeEndLiveDataMock;

    @Before
    public void setup() {
        selectedNotificationsTimeLiveDataMock = mock(MutableLiveData.class);
        notificationsActiveLiveDataMock = mock(MutableLiveData.class);
        constraintNotificationTimeStartLiveDataMock = mock(MutableLiveData.class);
        constraintNotificationTimeEndLiveDataMock = mock(MutableLiveData.class);
        hourNotificationPeriodLiveDataMock = mock(MutableLiveData.class);

        sharedPreferencesRepository = mock(SharedPreferencesRepository.class);
        alarmViewModel = new AlarmViewModel(sharedPreferencesRepository);

        doNothing().when(selectedNotificationsTimeLiveDataMock).observe(any(), any());
        doNothing().when(notificationsActiveLiveDataMock).observe(any(), any());
        doNothing().when(constraintNotificationTimeStartLiveDataMock).observe(any(), any());
        doNothing().when(constraintNotificationTimeEndLiveDataMock).observe(any(), any());
        doNothing().when(hourNotificationPeriodLiveDataMock).observe(any(), any());

        when(sharedPreferencesRepository.getSelectedNotificationsTimeLiveData()).thenReturn(selectedNotificationsTimeLiveDataMock);
        when(sharedPreferencesRepository.getNotificationsActiveLiveData()).thenReturn(notificationsActiveLiveDataMock);
        when(sharedPreferencesRepository.getConstraintNotificationTimeStartLiveData()).thenReturn(constraintNotificationTimeStartLiveDataMock);
        when(sharedPreferencesRepository.getConstraintNotificationTimeEndLiveData()).thenReturn(constraintNotificationTimeEndLiveDataMock);
        when(sharedPreferencesRepository.getHourNotificationPeriodLiveData()).thenReturn(hourNotificationPeriodLiveDataMock);
    }

    @Test
    public void setSelectedNotificationsTimeObserver_isGetSelectedNotificationsTimeLiveDataCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Observer<LocalTime> localTimeObserver = time -> {};
        alarmViewModel.setSelectedNotificationsTimeObserver(activity, localTimeObserver);
        verify(sharedPreferencesRepository, times(1)).getSelectedNotificationsTimeLiveData();
    }

    @Test
    public void isNotificationsActiveObserver_isGetNotificationsActiveLiveDataCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Observer<Boolean> booleanObserver = active -> {};
        alarmViewModel.isNotificationsActiveObserver(activity, booleanObserver);
        verify(sharedPreferencesRepository, times(1)).getNotificationsActiveLiveData();
    }

    @Test
    public void setConstraintNotificationsTimeStartObserver_isGetConstraintNotificationTimeStartLiveDataCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Observer<LocalTime> localTimeObserver = time -> {};
        alarmViewModel.setConstraintNotificationsTimeStartObserver(activity, localTimeObserver);
        verify(sharedPreferencesRepository, times(1)).getConstraintNotificationTimeStartLiveData();
    }

    @Test
    public void setConstraintNotificationsTimeEndObserver_isGetConstraintNotificationTimeEndLiveDataCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Observer<LocalTime> localTimeObserver = time -> {};
        alarmViewModel.setConstraintNotificationsTimeEndObserver(activity, localTimeObserver);
        verify(sharedPreferencesRepository, times(1)).getConstraintNotificationTimeEndLiveData();
    }

    @Test
    public void setHourNotificationPeriodObserver_isGetHourNotificationPeriodLiveDataCalled() {
        AppCompatActivity activity = mock(AppCompatActivity.class);
        Observer<Integer> integerObserver = period -> {};
        alarmViewModel.setHourNotificationPeriodObserver(activity, integerObserver);
        verify(sharedPreferencesRepository, times(1)).getHourNotificationPeriodLiveData();
    }

    @Test
    public void getSelectedNotificationsTime_isGetSelectedNotificationsTimeCalled() {
        LocalTime expectedTime = LocalTime.of(10, 0);
        when(sharedPreferencesRepository.getSelectedNotificationsTime()).thenReturn(expectedTime);
        LocalTime result = alarmViewModel.getSelectedNotificationsTime();
        assertEquals(expectedTime, result);
        verify(sharedPreferencesRepository, times(1)).getSelectedNotificationsTime();
    }

    @Test
    public void getConstraintNotificationTimeStart_isGetConstraintNotificationTimeStartCalled() {
        LocalTime expectedTime = LocalTime.of(8, 0);
        when(sharedPreferencesRepository.getConstraintNotificationTimeStart()).thenReturn(expectedTime);
        LocalTime result = alarmViewModel.getConstraintNotificationTimeStart();
        assertEquals(expectedTime, result);
        verify(sharedPreferencesRepository, times(1)).getConstraintNotificationTimeStart();
    }

    @Test
    public void getConstraintNotificationTimeEnd_isGetConstraintNotificationTimeEndCalled() {
        LocalTime expectedTime = LocalTime.of(20, 0);
        when(sharedPreferencesRepository.getConstraintNotificationTimeEnd()).thenReturn(expectedTime);
        LocalTime result = alarmViewModel.getConstraintNotificationTimeEnd();
        assertEquals(expectedTime, result);
        verify(sharedPreferencesRepository, times(1)).getConstraintNotificationTimeEnd();
    }

    @Test
    public void isNotificationsActive_isNotificationsActiveCalled() {
        boolean expectedValue = true;
        when(sharedPreferencesRepository.isNotificationsActive()).thenReturn(expectedValue);
        boolean result = alarmViewModel.isNotificationsActive();
        assertEquals(expectedValue, result);
        verify(sharedPreferencesRepository, times(1)).isNotificationsActive();
    }

    @Test
    public void getHourNotificationPeriod_isGetHourNotificationPeriodCalled() {
        int expectedPeriod = 3;
        when(sharedPreferencesRepository.getHourNotificationPeriod()).thenReturn(expectedPeriod);
        int result = alarmViewModel.getHourNotificationPeriod();
        assertEquals(expectedPeriod, result);
        verify(sharedPreferencesRepository, times(1)).getHourNotificationPeriod();
    }

    @Test
    public void setSelectedNotificationsTime_isSetSelectedNotificationsTimeCalled() {
        Context applicationContext = mock(Context.class);
        LocalTime selectedTime = LocalTime.of(9, 30);
        alarmViewModel.setSelectedNotificationsTime(applicationContext, selectedTime);
        verify(sharedPreferencesRepository, times(1)).setSelectedNotificationsTime(applicationContext, selectedTime);
    }

    @Test
    public void setConstraintNotificationTimeStart_isSetConstraintNotificationTimeStartCalled() {
        Context applicationContext = mock(Context.class);
        LocalTime timeConstraintStart = LocalTime.of(8, 0);
        alarmViewModel.setConstraintNotificationTimeStart(applicationContext, timeConstraintStart);
        verify(sharedPreferencesRepository, times(1)).setConstraintNotificationTimeStart(applicationContext, timeConstraintStart);
    }

    @Test
    public void setConstraintNotificationTimeEnd_isSetConstraintNotificationTimeEndCalled() {
        Context applicationContext = mock(Context.class);
        LocalTime timeConstraintEnd = LocalTime.of(20, 0);
        alarmViewModel.setConstraintNotificationTimeEnd(applicationContext, timeConstraintEnd);
        verify(sharedPreferencesRepository, times(1)).setConstraintNotificationTimeEnd(applicationContext, timeConstraintEnd);
    }

    @Test
    public void setNotificationsActive_isSetNotificationsActiveCalled() {
        Context applicationContext = mock(Context.class);
        boolean isChecked = true;
        alarmViewModel.setNotificationsActive(applicationContext, isChecked);
        verify(sharedPreferencesRepository, times(1)).setNotificationsActive(applicationContext, isChecked);
    }

    @Test
    public void setHourNotificationPeriod_isSetHourNotificationPeriodCalled() {
        Context applicationContext = mock(Context.class);
        int hourNotificationPeriod = 3;
        alarmViewModel.setHourNotificationPeriod(applicationContext, hourNotificationPeriod);
        verify(sharedPreferencesRepository, times(1)).setHourNotificationPeriod(applicationContext, hourNotificationPeriod);
    }
}
