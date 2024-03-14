package com.kamjer.woda.activity.calendaractivity.viewHolder;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.kamjer.mycalendarview.CalendarViewAdapter;
import com.kamjer.mycalendarview.CalendarViewHolder;
import com.kamjer.mycalendarview.DaysOfMonthView;
import com.kamjer.woda.R;

public class WaterCalendarViewHolder extends CalendarViewHolder {

    protected View circle;
    
    public WaterCalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        circle = itemView.findViewById(R.id.circle);
    }

    @Override
    public void bind(DaysOfMonthView daysOfMonthView, CalendarViewAdapter adapter, int position) {
        super.bind(daysOfMonthView, adapter, position);

    }

    public View getCircle() {
        return circle;
    }
}
