package com.kamjer.mycalendarview;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CalendarViewHolder extends RecyclerView.ViewHolder{

    protected LocalDate date;
    protected TextView dateCell;

    public CalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        dateCell = itemView.findViewById(R.id.day);
    }

    /**
     * This method is here so custom implementation of it is possible
     * @param daysOfMonthView
     * @param adapter
     * @param position
     */
    public void bind(DaysOfMonthView daysOfMonthView, CalendarViewAdapter adapter, int position) {

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        dateCell.setText(date.format(formatter));
    }

    @NotNull
    public TextView getDateCell() {
        return dateCell;
    }
}
