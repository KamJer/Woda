package com.kamjer.woda;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.kamjer.woda.repository.SharedPreferencesRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;

public class SharedPreferencesRepositoryUnitTest {

    @Mock
    private Context mockContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private SharedPreferences.Editor mockEditor;

    private SharedPreferencesRepository sharedPreferencesRepository;
    @Mock
    private MutableLiveData<Integer> waterAmountToDrinkMutableLiveData;
    @Mock
    private MutableLiveData<Boolean> notificationsActiveLiveData;
    @Mock
    private MutableLiveData<LocalTime> selectedNotificationsTimeLiveData;
    @Mock
    private MutableLiveData<LocalTime> constraintNotificationTimeStartLiveData;
    @Mock
    private MutableLiveData<LocalTime> constraintNotificationTimeEndLiveData;

    @Mock
    private MutableLiveData<Integer> hourNotificationPeriodLiveData;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockContext.getString(R.string.shared_preferences)).thenReturn("shared_preferences");


        sharedPreferencesRepository = new SharedPreferencesRepository(
                waterAmountToDrinkMutableLiveData,
                notificationsActiveLiveData,
                selectedNotificationsTimeLiveData,
                constraintNotificationTimeStartLiveData,
                constraintNotificationTimeEndLiveData,
                hourNotificationPeriodLiveData);
    }

    @Test
    public void testLoadWaterAmount() {
        when(mockSharedPreferences.getInt(eq(SharedPreferencesRepository.WATER_AMOUNT_TO_DRINK_NAME), anyInt())).thenReturn(2000);

        sharedPreferencesRepository.loadWaterAmount(mockContext);

        verify(waterAmountToDrinkMutableLiveData).setValue(2000);
    }

    @Test
    public void testSetWaterAmountToDrink() {
        when(mockEditor.putInt(eq(SharedPreferencesRepository.WATER_AMOUNT_TO_DRINK_NAME), anyInt())).thenReturn(mockEditor);

        sharedPreferencesRepository.setWaterAmountToDrink(mockContext, 2500);

        verify(mockEditor).putInt(SharedPreferencesRepository.WATER_AMOUNT_TO_DRINK_NAME, 2500);
        verify(mockEditor).apply();
        verify(waterAmountToDrinkMutableLiveData).setValue(2500);
    }

    @Test
    public void testLoadActiveNotification() {
        when(mockSharedPreferences.getBoolean(eq("isNotificationsActive"), anyBoolean())).thenReturn(true);

        sharedPreferencesRepository.loadActiveNotification(mockContext);

        verify(notificationsActiveLiveData).setValue(true);
    }

    @Test
    public void testSetNotificationsActive() {
        when(mockEditor.putBoolean(eq(SharedPreferencesRepository.NOTIFICATIONS_ACTIVE_NAME), anyBoolean())).thenReturn(mockEditor);

        sharedPreferencesRepository.setNotificationsActive(mockContext, true);

        verify(mockEditor).putBoolean(SharedPreferencesRepository.NOTIFICATIONS_ACTIVE_NAME, true);
        verify(mockEditor).apply();
        verify(notificationsActiveLiveData).setValue(true);
    }

    @Test
    public void testLoadSelectedNotificationTime() {
        LocalTime expectedTime = LocalTime.of(10, 0);
        when(mockSharedPreferences.getString(eq(SharedPreferencesRepository.SELECTED_NOTIFICATIONS_TIME_NAME), anyString())).thenReturn(expectedTime.toString());

        sharedPreferencesRepository.loadSelectedNotificationTime(mockContext);

        verify(selectedNotificationsTimeLiveData).setValue(expectedTime);
    }

    @Test
    public void testSetSelectedNotificationsTime() {
        LocalTime newTime = LocalTime.of(9, 30);
        when(mockEditor.putString(eq(SharedPreferencesRepository.SELECTED_NOTIFICATIONS_TIME_NAME), anyString())).thenReturn(mockEditor);

        sharedPreferencesRepository.setSelectedNotificationsTime(mockContext, newTime);

        verify(mockEditor).putString(SharedPreferencesRepository.SELECTED_NOTIFICATIONS_TIME_NAME, newTime.toString());
        verify(mockEditor).apply();

        verify(selectedNotificationsTimeLiveData).setValue(newTime);

    }

    @Test
    public void testLoadConstraintNotificationTimeStart() {
        LocalTime expectedTime = LocalTime.of(22, 0);
        when(mockSharedPreferences.getString(eq(SharedPreferencesRepository.CONSTRAINT_NOTIFICATIONS_TIME_START_NAME), anyString())).thenReturn(expectedTime.toString());

        sharedPreferencesRepository.loadConstraintNotificationTimeStart(mockContext);

        verify(constraintNotificationTimeStartLiveData).setValue(expectedTime);
    }

    @Test
    public void testSetConstraintNotificationTimeStart() {
        LocalTime newTime = LocalTime.of(21, 0);
        when(mockEditor.putString(eq(SharedPreferencesRepository.CONSTRAINT_NOTIFICATIONS_TIME_START_NAME), anyString())).thenReturn(mockEditor);

        sharedPreferencesRepository.setConstraintNotificationTimeStart(mockContext, newTime);

        verify(mockEditor).putString(SharedPreferencesRepository.CONSTRAINT_NOTIFICATIONS_TIME_START_NAME, newTime.toString());
        verify(mockEditor).apply();

        verify(constraintNotificationTimeStartLiveData).setValue(newTime);

    }

    @Test
    public void testLoadConstraintNotificationTimeEnd() {
        LocalTime expectedTime = LocalTime.of(8, 0);
        when(mockSharedPreferences.getString(eq(SharedPreferencesRepository.CONSTRAINT_NOTIFICATIONS_TIME_END_NAME), anyString())).thenReturn(expectedTime.toString());

        sharedPreferencesRepository.loadConstraintNotificationTimeEnd(mockContext);

        verify(constraintNotificationTimeEndLiveData).setValue(expectedTime);

    }

    @Test
    public void testSetConstraintNotificationTimeEnd() {
        LocalTime newTime = LocalTime.of(7, 0);
        when(mockEditor.putString(eq(SharedPreferencesRepository.CONSTRAINT_NOTIFICATIONS_TIME_END_NAME), anyString())).thenReturn(mockEditor);

        sharedPreferencesRepository.setConstraintNotificationTimeEnd(mockContext, newTime);

        verify(mockEditor).putString(SharedPreferencesRepository.CONSTRAINT_NOTIFICATIONS_TIME_END_NAME, newTime.toString());
        verify(mockEditor).apply();

        verify(constraintNotificationTimeEndLiveData).setValue(newTime);

    }

    @Test
    public void testLoadHourNotificationPeriod() {
        when(mockSharedPreferences.getInt(eq(SharedPreferencesRepository.HOUR_NOTIFICATION_PERIOD_NAME), anyInt())).thenReturn(2);

        sharedPreferencesRepository.loadHourNotificationPeriod(mockContext);

        verify(hourNotificationPeriodLiveData).setValue(2);

    }

    @Test
    public void testSetHourNotificationPeriod() {
        when(mockEditor.putInt(eq(SharedPreferencesRepository.HOUR_NOTIFICATION_PERIOD_NAME), anyInt())).thenReturn(mockEditor);

        sharedPreferencesRepository.setHourNotificationPeriod(mockContext, 3);

        verify(mockEditor).putInt(SharedPreferencesRepository.HOUR_NOTIFICATION_PERIOD_NAME, 3);
        verify(mockEditor).apply();

        verify(hourNotificationPeriodLiveData).setValue(3);
    }
}
