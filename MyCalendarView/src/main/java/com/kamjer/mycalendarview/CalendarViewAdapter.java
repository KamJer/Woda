package com.kamjer.mycalendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class CalendarViewAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> daysOfMonth;
    private LocalDate selectedDate;
    private int position = RecyclerView.NO_POSITION;
    private int prPosition = RecyclerView.NO_POSITION;
    private LayoutInflater mInflater;
    private SelectedDataChangedListener selectedDataChangedListener;
    private DaysOfMonthView daysOfMonthView;

    public CalendarViewAdapter(SelectedDataChangedListener selectedDataChangedListener, DaysOfMonthView daysOfMonthView, LocalDate selectedDate, ArrayList<LocalDate> daysOfMonth, Context context) {
        this.daysOfMonth = daysOfMonth;
        this.selectedDate = selectedDate;
        this.daysOfMonthView = daysOfMonthView;
        this.selectedDataChangedListener = selectedDataChangedListener;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.calendar_cell, parent, false);
        return new CalendarViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder,int position) {
        if (daysOfMonth.get(position) != null) {
            if (daysOfMonth.get(position).equals(selectedDate)) {
                holder.itemView.setBackgroundColor(Color.BLUE);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        holder.setDate(daysOfMonth.get(position));
        holder.setSelectedDataChangedListener(selectedDataChangedListener);
        holder.itemView.setOnClickListener(v -> actionOnClickDate(v, holder, selectedDate, position));

        if (holder.getDate().equals(selectedDate)) {
            setPosition(position);
        }
    }
    
    private void setPosition(int position) {
        this.position = position;
    }

    private int getPosition() {
        return position;
    }

    private void actionOnClickDate(View v, CalendarViewHolder holder, LocalDate selectedDate, int position) {
        holder.itemView.setBackgroundColor(Color.BLUE);
        if (!selectedDate.equals(holder.getDate())) {
            notifyItemChanged(getPosition());
            setPosition(position);
            setSelectedDate(holder.getDate());
            if (selectedDataChangedListener != null) {
                selectedDataChangedListener.dataChangedAction(holder.itemView, holder.getDate());
            }
        }
    }

    public void setSelectedDataChangedListener(SelectedDataChangedListener selectedDataChangedListener) {
        this.selectedDataChangedListener = selectedDataChangedListener;
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }
}
