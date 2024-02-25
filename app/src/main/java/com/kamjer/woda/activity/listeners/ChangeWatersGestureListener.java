package com.kamjer.woda.activity.listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kamjer.woda.MainActivity;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.time.LocalDate;

public class ChangeWatersGestureListener implements GestureDetector.OnGestureListener {

    private MainActivity mainActivity;
    private WaterViewModel waterViewModel;

    public ChangeWatersGestureListener(MainActivity mainActivity, WaterViewModel waterViewModel) {
        this.mainActivity = mainActivity;
        this.waterViewModel = waterViewModel;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        LocalDate currentDate = waterViewModel.getActiveDate();
        float direction = e1.getX() - e2.getX();
        if (direction > 0) {
            waterViewModel.loadWaterDayWithWatersByDate(currentDate.plusDays(1));
        } else {
            waterViewModel.loadWaterDayWithWatersByDate(currentDate.minusDays(1));
        }
        return true;
    }
}
