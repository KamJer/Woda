package com.kamjer.mycalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DaysOfMonthView extends RecyclerView {

    private Month shownMonth;
    private LocalDate selectedDate;
    private CalendarViewAdapter calendarAdapter;
    private CustomHolderBehavior customHolderBehavior;
    private Constructor<? extends CalendarViewHolder> calendarViewHolderConstructor;
    private SelectedDataChangedListener selectedDataChangedListener;
    private int layoutId;

    public DaysOfMonthView(@NonNull Context context) {
        super(context);
        init();
    }

    public DaysOfMonthView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DaysOfMonthView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        selectedDate = LocalDate.now();
        setMonthView();
    }

    public void setMonthView() {
        shownMonth = selectedDate.getMonth();
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

        if (calendarViewHolderConstructor == null) {
            try {
                calendarViewHolderConstructor = CalendarViewHolder.class.getConstructor(View.class);
                layoutId = R.layout.calendar_cell;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        calendarAdapter = new CalendarViewAdapter(calendarViewHolderConstructor, layoutId, selectedDataChangedListener, customHolderBehavior, this, daysInMonth, getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        setLayoutManager(layoutManager);
        setAdapter(calendarAdapter);
    }

    public ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = date.withDayOfMonth(1);
        LocalDate lastOfMonth = date.withDayOfMonth(yearMonth.lengthOfMonth());
        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int endDayOfWeek = lastOfMonth.getDayOfWeek().getValue();
        int endTemp = 7 - endDayOfWeek - 1;



//                                  days in a month
        int maxDaysCount = yearMonth.getMonth().length(Year.isLeap(yearMonth.getYear())) +
//      the number of days in a week before month begin
                            startDayOfWeek +
//      the number of days in a week after month ends
                            endTemp;

        for (int i = 1; i <= maxDaysCount; i++) {
            if (i < startDayOfWeek) {
                daysInMonthArray.add(firstOfMonth.minusDays(startDayOfWeek - i));
            } else if (i > daysInMonth + startDayOfWeek) {
                daysInMonthArray.add(firstOfMonth.plusDays(i - startDayOfWeek));
            }else {
                daysInMonthArray.add(firstOfMonth.plusDays(i - startDayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    public void setSelectedDateChangedListener(SelectedDataChangedListener selectedDataChangedListener) {
        this.selectedDataChangedListener = selectedDataChangedListener;
        this.calendarAdapter.setSelectedDataChangedListener(selectedDataChangedListener);
    }

    public void setCustomHolderBehavior(CustomHolderBehavior customHolderBehavior) {
        this.customHolderBehavior = customHolderBehavior;
        this.calendarAdapter.setCustomHolderBehavior(customHolderBehavior);
    }

    public void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public CalendarViewAdapter getCalendarAdapter() {
        return calendarAdapter;
    }

    public Month getShownMonth() {
        return shownMonth;
    }

    public Constructor<? extends CalendarViewHolder> getCalendarViewHolderConstructor() {
        return calendarViewHolderConstructor;
    }

    public void setCalendarViewHolderConstructor(Constructor<? extends CalendarViewHolder> calendarViewHolderConstructor, int layoutId) {
//        setting new holder for a calendar
        this.calendarViewHolderConstructor = calendarViewHolderConstructor;
        this.layoutId = layoutId;
//      setting new month view with new holder
        setMonthView();
    }
}
