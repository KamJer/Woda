package com.kamjer.mycalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;

public class CalendarView extends RelativeLayout {
    private TextView textViewYear;
    private TextView textViewMonth;
    private DaysOfMonthView daysOfMonthView;
    private SelectedDataChangedListener previousMonthChangeListener;
    private SelectedDataChangedListener nextMonthChangeListener;

    private TextView[] textDaysNames = new TextView[7];
    private GestureDetectorCompat gestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_view, this, true);


        GestureDetector.OnGestureListener gestureListener = new ChangeMonthGestureListener(this);
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);

        textViewMonth = findViewById(R.id.textViewMonth);
        textViewYear = findViewById(R.id.textViewYear);

        ImageButton buttonPreviousMonth = findViewById(R.id.buttonPreviousMonth);
        ImageButton buttonNextMonth = findViewById(R.id.buttonNextMonth);

        daysOfMonthView = findViewById(R.id.daysOfMonth);

        buttonPreviousMonth.setOnClickListener(v -> previousMonthAction());
        buttonNextMonth.setOnClickListener(v -> nextMonthAction());

        textDaysNames[0] = findViewById(R.id.textMonday);
        textDaysNames[1] = findViewById(R.id.textTuesday);
        textDaysNames[2] = findViewById(R.id.textWednesday);
        textDaysNames[3] = findViewById(R.id.textThursday);
        textDaysNames[4] = findViewById(R.id.textFriday);
        textDaysNames[5] = findViewById(R.id.textSaturday);
        textDaysNames[6] = findViewById(R.id.textSunday);

        String[] shortWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays();

        for (int i = 0; i < textDaysNames.length; i++) {
            if (i == textDaysNames.length - 1){
                textDaysNames[i].setText(shortWeekDays[1]);
            } else {
                textDaysNames[i].setText(shortWeekDays[i + 2]);
            }
        }

        setTextYearMonth();
    }


    public void setSelectedDateChangedListener(SelectedDataChangedListener selectedDataChangedListener) {
        daysOfMonthView.setSelectedDateChangedListener(selectedDataChangedListener);
    }

    public void setCustomHolderBehavior(CustomHolderBehavior customHolderBehavior) {
        this.daysOfMonthView.setCustomHolderBehavior(customHolderBehavior);
    }

    public void previousMonthAction() {
        daysOfMonthView.previousMonthAction();
        setSelectedDate(daysOfMonthView.getSelectedDate());
        setTextYearMonth();
        if (previousMonthChangeListener != null) {
            previousMonthChangeListener.dataChangedAction(daysOfMonthView, getSelectedDate());
        }
    }

        public void nextMonthAction() {
        daysOfMonthView.nextMonthAction();
        setSelectedDate(daysOfMonthView.getSelectedDate());
        setTextYearMonth();
        if (nextMonthChangeListener != null) {
            nextMonthChangeListener.dataChangedAction(daysOfMonthView, getSelectedDate());
        }
    }

    protected void setTextYearMonth() {
        textViewMonth.setText(daysOfMonthView.getSelectedDate().getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).toUpperCase());
        textViewYear.setText(String.valueOf(daysOfMonthView.getSelectedDate().getYear()));
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

    public void setCustomCalendarViewHolder(Class<? extends CalendarViewHolder> calendarViewHolderClass, int layoutId) {
        try {
            this.daysOfMonthView.setCalendarViewHolderConstructor(calendarViewHolderClass.getConstructor(View.class), layoutId);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMonthView() {
        daysOfMonthView.setMonthView();
    }
}
