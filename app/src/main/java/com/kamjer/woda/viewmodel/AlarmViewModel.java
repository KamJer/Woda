package com.kamjer.woda.viewmodel;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kamjer.woda.repository.SharedPreferencesRepository;

import java.time.LocalTime;

public class AlarmViewModel extends ViewModel {

    public void setSelectedNotificationsTimeObserver(LifecycleOwner owner, Observer<LocalTime> observer){
        SharedPreferencesRepository.getInstance().getSelectedNotificationsTimeLiveData().observe(owner, observer);
    }
    public void isNotificationsActiveObserver(LifecycleOwner owner, Observer<Boolean> observer){
        SharedPreferencesRepository.getInstance().getNotificationsActiveLiveData().observe(owner, observer);
    }
    public void setConstraintNotificationsTimeStartObserver(LifecycleOwner owner, Observer<LocalTime> observer){
        SharedPreferencesRepository.getInstance().getConstraintNotificationTimeStartLiveData().observe(owner, observer);
    }
    public void setConstraintNotificationsTimeEndObserver(LifecycleOwner owner, Observer<LocalTime> observer){
        SharedPreferencesRepository.getInstance().getConstraintNotificationTimeEndLiveData().observe(owner, observer);
    }
    public void setHourNotificationPeriodObserver(LifecycleOwner owner, Observer<Integer> observer) {
        SharedPreferencesRepository.getInstance().getHourNotificationPeriodLiveData().observe(owner, observer);
    }

    public LocalTime getSelectedNotificationsTime() {
        return SharedPreferencesRepository.getInstance().getSelectedNotificationsTime();
    }

    public LocalTime getConstraintNotificationTimeStart() {
        return SharedPreferencesRepository.getInstance().getConstraintNotificationTimeStart();
    }

    public LocalTime getConstraintNotificationTimeEnd() {
        return SharedPreferencesRepository.getInstance().getConstraintNotificationTimeEnd();
    }

    public boolean isNotificationsActive() {
        return SharedPreferencesRepository.getInstance().isNotificationsActive();
    }

    public Integer getHourNotificationPeriod() {
        return SharedPreferencesRepository.getInstance().getHourNotificationPeriod();
    }

    public void setSelectedNotificationsTime(Context applicationContext, LocalTime selectedTime) {
        SharedPreferencesRepository.getInstance().setSelectedNotificationsTime(applicationContext, selectedTime);
    }

    public void setConstraintNotificationTimeStart(Context applicationContext, LocalTime timeConstraintStart) {
        SharedPreferencesRepository.getInstance().setConstraintNotificationTimeStart(applicationContext, timeConstraintStart);
    }

    public void setConstraintNotificationTimeEnd(Context applicationContext, LocalTime timeConstraintEnd) {
        SharedPreferencesRepository.getInstance().setConstraintNotificationTimeEnd(applicationContext, timeConstraintEnd);
    }

    public void setNotificationsActive(Context applicationContext, boolean isChecked) {
        SharedPreferencesRepository.getInstance().setNotificationsActive(applicationContext, isChecked);
    }

    public void setHourNotificationPeriod(Integer hourNotificationPeriod) {
        SharedPreferencesRepository.getInstance().setHourNotificationPeriod(hourNotificationPeriod);
    }
}
