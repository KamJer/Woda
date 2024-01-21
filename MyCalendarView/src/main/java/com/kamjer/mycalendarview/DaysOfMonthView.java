package com.kamjer.mycalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DaysOfMonthView extends RecyclerView {

    private LocalDate selectedDate;
    private CalendarViewAdapter calendarAdapter;

    private SelectedDataChangedListener selectedDataChangedListener;

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
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarViewAdapter(selectedDataChangedListener, this, selectedDate, daysInMonth, getContext());
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
        int endTemp = 7 - endDayOfWeek;

//                                  days in a month
        int maxDaysCount = yearMonth.getMonth().maxLength() +
//      the number of days in a week before month begin
                            startDayOfWeek +
//      the number of days in a week after month ends
                            endTemp - 1;

        for (int i = 1; i <= maxDaysCount; i++) {
//            if (i <= startDayOfWeek || i > daysInMonth + startDayOfWeek) {
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
        calendarAdapter.setSelectedDataChangedListener(selectedDataChangedListener);
    }

    private String monthFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        return date.format(formatter);
    }

    private String yearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
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
        LocalDate localDateTemp = this.selectedDate;
        this.selectedDate = selectedDate;
        calendarAdapter.setSelectedDate(selectedDate);

        if (!selectedDate.getMonth().equals(localDateTemp.getMonth())) {
            setMonthView();
        }
    }

    public CalendarViewAdapter getCalendarAdapter() {
        return calendarAdapter;
    }

    public static int getPositionByDate(DaysOfMonthView daysOfMonthView, LocalDate targetDate) {
        CalendarViewAdapter adapter = daysOfMonthView.getCalendarAdapter() ;

        if (adapter == null) {
            return RecyclerView.NO_POSITION;
        }

        int itemCount = adapter.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder viewHolder = daysOfMonthView.findViewHolderForAdapterPosition(i);

            if (viewHolder instanceof CalendarViewHolder) {
                CalendarViewHolder yourViewHolder = (CalendarViewHolder) viewHolder;
                LocalDate dateInHolder = yourViewHolder.getDate();

                if (targetDate.equals(dateInHolder)) {
                    return i;
                }
            }
        }

        return RecyclerView.NO_POSITION;
    }

}
