package com.kamjer.mycalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.time.LocalDate;

public class MyCalendarView extends RelativeLayout {

    private TextView textViewYear;

    private TextView textViewMonth;

    private Button buttonPreviousMonth;
    private Button buttonNextMonth;

    private DaysOfMonthView daysOfMonthView;

    private SelectedDataChangedListener previousMonthChangeListener;
    private SelectedDataChangedListener nextMonthChangeListener;

    public MyCalendarView(Context context) {
        super(context);
        init();
    }

    public MyCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_view, this, true);

        textViewMonth = findViewById(R.id.textViewMonth);
        textViewYear = findViewById(R.id.textViewYear);

        buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        buttonNextMonth = findViewById(R.id.buttonNextMonth);

        daysOfMonthView = findViewById(R.id.daysOfMonth);

        buttonPreviousMonth.setOnClickListener(this::previousMonthAction);
        buttonNextMonth.setOnClickListener(this::nextMonthAction);

        textViewMonth.setText(daysOfMonthView.getSelectedDate().getMonth().toString());
        textViewYear.setText(String.valueOf(daysOfMonthView.getSelectedDate().getYear()));
    }

    public void setSelectedDateChangedListener(SelectedDataChangedListener selectedDataChangedListener) {
        daysOfMonthView.setSelectedDateChangedListener(selectedDataChangedListener);
    }

    public void previousMonthAction(View view) {
        daysOfMonthView.previousMonthAction();
        setSelectedDate(daysOfMonthView.getSelectedDate());
        textViewMonth.setText(daysOfMonthView.getSelectedDate().getMonth().toString());
        textViewYear.setText(String.valueOf(daysOfMonthView.getSelectedDate().getYear()));
        if (previousMonthChangeListener != null) {
            previousMonthChangeListener.dataChangedAction(daysOfMonthView, getSelectedDate());
        }
    }

    public void nextMonthAction(View view) {
        daysOfMonthView.nextMonthAction();
        setSelectedDate(daysOfMonthView.getSelectedDate());
        textViewMonth.setText(daysOfMonthView.getSelectedDate().getMonth().toString());
        textViewYear.setText(String.valueOf(daysOfMonthView.getSelectedDate().getYear()));
        if (nextMonthChangeListener != null) {
            nextMonthChangeListener.dataChangedAction(daysOfMonthView, getSelectedDate());
        }
    }

    public LocalDate getSelectedDate() {
        return daysOfMonthView.getSelectedDate();
    }

    public void setSelectedDate(LocalDate selectedDate) {
        daysOfMonthView.setSelectedDate(selectedDate);
    }

    public DaysOfMonthView getDaysOfMonthView() {
        return daysOfMonthView;
    }

    public SelectedDataChangedListener getPreviousMonthChangeListener() {
        return previousMonthChangeListener;
    }

    public void setPreviousMonthChangeListener(SelectedDataChangedListener previousMonthChangeListener) {
        this.previousMonthChangeListener = previousMonthChangeListener;
    }

    public SelectedDataChangedListener getNextMonthChangeListener() {
        return nextMonthChangeListener;
    }

    public void setNextMonthChangeListener(SelectedDataChangedListener nextMonthChangeListener) {
        this.nextMonthChangeListener = nextMonthChangeListener;
    }
}
