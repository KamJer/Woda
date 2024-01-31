package com.kamjer.mycalendarview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private static final int SELECTION_DATE_COLOR = Color.BLUE;
    private static final int OUT_OF_MOTH_DATE_COLOR = Color.GRAY;
    private final ArrayList<LocalDate> daysOfMonth;
    private final DaysOfMonthView daysOfMonthView;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private LayoutInflater mInflater;
    private SelectedDataChangedListener selectedDataChangedListener;
    private CustomHolderBehavior customHolderBehavior;
    private Constructor<? extends CalendarViewHolder> calendarViewHolderConstructor;
    private int layoutId;

    public CalendarViewAdapter(Constructor<? extends CalendarViewHolder> calendarViewHolderConstructor, int layoutId, SelectedDataChangedListener selectedDataChangedListener, CustomHolderBehavior customHolderBehavior, DaysOfMonthView daysOfMonthView, ArrayList<LocalDate> daysOfMonth, Context context) {
        this.calendarViewHolderConstructor = calendarViewHolderConstructor;
        this.layoutId = layoutId;
        this.selectedDataChangedListener = selectedDataChangedListener;
        this.customHolderBehavior = customHolderBehavior;
        this.daysOfMonth = daysOfMonth;
        this.daysOfMonthView = daysOfMonthView;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = mInflater.inflate(layoutId, parent, false);
            return calendarViewHolderConstructor.newInstance(view);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder,int position) {
//      highlighting selected date if it is selected
        if (daysOfMonth.get(position) != null) {
            if (daysOfMonth.get(position).equals(daysOfMonthView.getSelectedDate())) {
                holder.itemView.setBackgroundColor(SELECTION_DATE_COLOR);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
//      setting date to the holder
        holder.setDate(daysOfMonth.get(position));
//      setting listener
        holder.itemView.setOnClickListener(v -> actionOnClickDate(v, holder, daysOfMonthView.getSelectedDate(), position));
//          all of extra bindings for a holder
        holder.bind(daysOfMonthView, this, position);
//      checking if date is in a shown month (if it is not make id gray and opaque,
//      if it is explicitly make it black and not opaque,
//      internal processing of recyclerView demands it to be set every binding)
        if (!daysOfMonth.get(position).getMonth().equals(daysOfMonthView.getShownMonth())) {
            holder.getDateCell().setTextColor(OUT_OF_MOTH_DATE_COLOR);
        } else {
            holder.getDateCell().setTextColor(Color.BLACK);
        }
//      checking if a holder is a selected date
        if (holder.getDate().equals(daysOfMonthView.getSelectedDate())) {
            setSelectedPosition(position);
        }
//      checking if there is any custom behavior for a holder, if there is fire it
        if (customHolderBehavior != null) {
            customHolderBehavior.customize(holder);
        }
    }

    /**
     * Sets position of a selected holder of selected date
     * @param selectedPosition position of a holder with selected date
     */
    private void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    /**
     * Returns position of a holder with selected date
     * @return position of a holder with selected date
     */
    private int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Sets behavior for action of clicking on a date
     * @param v - itemView of a holder
     * @param holder that is clicked
     * @param selectedDate in a calendar
     * @param position of a holder in an adapter
     */
    private void actionOnClickDate(View v, CalendarViewHolder holder, LocalDate selectedDate, int position) {
//      setting background for new selected date
        holder.itemView.setBackgroundColor(SELECTION_DATE_COLOR);
//      if selected date is the same as this holder (aka this holder was clicked) set new selected position
        if (!selectedDate.equals(holder.getDate())) {
            notifyItemChanged(getSelectedPosition());
            setSelectedPosition(position);
//            inform parent there is a new selected date
            this.daysOfMonthView.setSelectedDate(holder.getDate());
        }
//       if selectedDataChangedListener is null d fire
        if (selectedDataChangedListener != null) {
            selectedDataChangedListener.dataChangedAction(holder.itemView, holder.getDate());
        }
    }

    /**
     * Sets behavior for change of selected date
     * @param selectedDataChangedListener
     */
    public void setSelectedDataChangedListener(SelectedDataChangedListener selectedDataChangedListener) {
        this.selectedDataChangedListener = selectedDataChangedListener;
    }

    /**
     * Sets behavior for custom looks and actions for a date
     * @param customHolderBehavior
     */
    public void setCustomHolderBehavior(CustomHolderBehavior customHolderBehavior) {
        this.customHolderBehavior = customHolderBehavior;
//      this line is necessary if not new custom behavior does not fire
        this.notifyDataSetChanged();
    }

    public void setCalendarViewHolderConstructor(Constructor<CalendarViewHolder> calendarViewHolderConstructor) {
        this.calendarViewHolderConstructor = calendarViewHolderConstructor;
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }
}
