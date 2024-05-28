package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.kamjer.woda.repository.SharedPreferencesRepository;

import java.time.LocalTime;

public class AlarmViewModel extends ViewModel {

    SharedPreferencesRepository sharedPreferencesRepository;

    public static final ViewModelInitializer<AlarmViewModel> initializer = new ViewModelInitializer<>(
            AlarmViewModel.class,
            creationExtras ->
                    new AlarmViewModel(SharedPreferencesRepository.getInstance())
    );

    public AlarmViewModel(SharedPreferencesRepository sharedPreferencesRepository) {
        this.sharedPreferencesRepository = sharedPreferencesRepository;
    }

    public void setSelectedNotificationsTimeObserver(LifecycleOwner owner, Observer<LocalTime> observer){
        sharedPreferencesRepository.getSelectedNotificationsTimeLiveData().observe(owner, observer);
    }

    public void isNotificationsActiveObserver(LifecycleOwner owner, Observer<Boolean> observer){
        sharedPreferencesRepository.getNotificationsActiveLiveData().observe(owner, observer);
    }

    public void setConstraintNotificationsTimeStartObserver(LifecycleOwner owner, Observer<LocalTime> observer){
        sharedPreferencesRepository.getConstraintNotificationTimeStartLiveData().observe(owner, observer);
    }

    public void setConstraintNotificationsTimeEndObserver(LifecycleOwner owner, Observer<LocalTime> observer){
        sharedPreferencesRepository.getConstraintNotificationTimeEndLiveData().observe(owner, observer);
    }

    public void setHourNotificationPeriodObserver(LifecycleOwner owner, Observer<Integer> observer) {
        sharedPreferencesRepository.getHourNotificationPeriodLiveData().observe(owner, observer);
    }

    public LocalTime getSelectedNotificationsTime() {
        return sharedPreferencesRepository.getSelectedNotificationsTime();
    }

    public LocalTime getConstraintNotificationTimeStart() {
        return sharedPreferencesRepository.getConstraintNotificationTimeStart();
    }

    public LocalTime getConstraintNotificationTimeEnd() {
        return sharedPreferencesRepository.getConstraintNotificationTimeEnd();
    }

    public boolean isNotificationsActive() {
        return sharedPreferencesRepository.isNotificationsActive();
    }

    public Integer getHourNotificationPeriod() {
        return sharedPreferencesRepository.getHourNotificationPeriod();
    }

    public void setSelectedNotificationsTime(Context applicationContext, LocalTime selectedTime) {
        sharedPreferencesRepository.setSelectedNotificationsTime(applicationContext, selectedTime);
    }

    public void setConstraintNotificationTimeStart(Context applicationContext, LocalTime timeConstraintStart) {
        sharedPreferencesRepository.setConstraintNotificationTimeStart(applicationContext, timeConstraintStart);
    }

    public void setConstraintNotificationTimeEnd(Context applicationContext, LocalTime timeConstraintEnd) {
        sharedPreferencesRepository.setConstraintNotificationTimeEnd(applicationContext, timeConstraintEnd);
    }

    public void setNotificationsActive(Context applicationContext, boolean isChecked) {
        sharedPreferencesRepository.setNotificationsActive(applicationContext, isChecked);
    }

    public void setHourNotificationPeriod(Context applicationContext, int hourNotificationPeriod) {
        sharedPreferencesRepository.setHourNotificationPeriod(applicationContext, hourNotificationPeriod);
    }
}
