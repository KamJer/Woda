package com.kamjer.mycalendarview;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChangeMonthGestureListener implements GestureDetector.OnGestureListener{
    private CalendarView calendarView;

    public ChangeMonthGestureListener(CalendarView calendarView) {
        this.calendarView = calendarView;
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
        float direction = e1.getX() - e2.getX();
        if (direction > 0) {
            calendarView.nextMonthAction();
        } else {
            calendarView.previousMonthAction();
        }
        return true;
    }
}
