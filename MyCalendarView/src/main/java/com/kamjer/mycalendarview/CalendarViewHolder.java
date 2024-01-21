package com.kamjer.mycalendarview;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class CalendarViewHolder extends RecyclerView.ViewHolder{

    private LocalDate date;
    private TextView dateCell;

    private CalendarViewAdapter adapter;
    private SelectedDataChangedListener selectedDataChangedListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarViewAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        dateCell = itemView.findViewById(R.id.day);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
//        TODO: temporary, fix it, so that no null are passed here and that you set a formatter
        if (date == null) {
            dateCell.setText("");
        } else {
            dateCell.setText(String.valueOf(date.toString().split("-")[2]));
        }
    }

    @NotNull
    public TextView getDateCell() {
        return dateCell;
    }

    public void setSelectedDataChangedListener(SelectedDataChangedListener selectedDataChangedListener) {
        this.selectedDataChangedListener = selectedDataChangedListener;
    }
}
