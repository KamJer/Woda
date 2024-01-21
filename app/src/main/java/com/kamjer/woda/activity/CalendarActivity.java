package com.kamjer.woda.activity;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.mycalendarview.CalendarViewAdapter;
import com.kamjer.mycalendarview.CalendarViewHolder;
import com.kamjer.mycalendarview.DaysOfMonthView;
import com.kamjer.mycalendarview.MyCalendarView;
import com.kamjer.mycalendarview.SelectedDataChangedListener;
import com.kamjer.woda.R;
import com.kamjer.woda.model.Water;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CalendarActivity extends AppCompatActivity {

    private WaterViewModel waterViewModel;
    private MyCalendarView calendarView;
    private SelectedDataChangedListener selectedDataChangedListener;
    private List<Water> waters;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.calendar_activity_layout);

        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);

        calendarView = findViewById(R.id.calendarViewSelectDate);
        calendarView.setSelectedDateChangedListener(this::dataChangedAction);

        Water water = waterViewModel.getWaterValue();
        calendarView.setSelectedDate(water.getDate());

        selectedDataChangedListener = (view, localDate) ->
                waterViewModel.loadWatersFromMonth(localDate, waters -> {
                    CalendarActivity.this.waters = waters;
                    colorDays(waters);
                }
        );

        selectedDataChangedListener.dataChangedAction(calendarView.getDaysOfMonthView(), calendarView.getSelectedDate());

        calendarView.setNextMonthChangeListener(selectedDataChangedListener);
        calendarView.setPreviousMonthChangeListener(selectedDataChangedListener);
    }

    /**
     * Colors dates in a calendar with a proper color corresponding to
     * water drank in a day (gradient from red when 0% water drank, green when 100%),
     * if water does not exist in a day paint that day red.
     * @param waters list of water from specific dates
     */
    private void colorDays(List<Water> waters) {
//      getting view of days
        DaysOfMonthView daysView = calendarView.getDaysOfMonthView();
//      looping through days in a calendarView
        for (int i = 0; i < daysView.getCalendarAdapter().getItemCount(); i++) {
//          getting a day
            CalendarViewHolder day = (CalendarViewHolder) daysView.getChildViewHolder(daysView.getChildAt(i));
//          if a selected day is currently checked day do nothing
            if (!daysView.getSelectedDate().equals(day.getDate())) {
//              looking for a water for a day
                findWaterByDate(waters, day.getDate()).map(water -> {
//                  painting elements for a found water
//                  normalizing water drank
                    float normalizedWater = (float) water.getWaterDrank() / (float) water.getWaterToDrink();
//                  we want it to be half see through
                    int a = 255 / 2;
//                  calculating green value
                    int g = (int) (255 * normalizedWater);
//                  subtracting green value from red (if green is 100% aka water drank goal is achieved it will be equal to
//                  255 so red will be equal to 0, achieving completely green paint
//                  creates nice gradient
                    int r = 255 - g;
//                  paint day
                    day.getDateCell().setBackgroundColor(Color.argb(a, r, g, 0));
                    return water;
                }).orElseGet(() -> {
//                  we want it to be half see through
                    int a = 255 / 2;
//                  painting day in red (no value found, no water in a database, no water drank)
                    int r = 255;
                    day.getDateCell().setBackgroundColor(Color.argb(a, r, 0, 0));
                    return null;
                });
            }
            day.getDateCell().invalidate();
        }

    }

    /**
     * Action when data in a calendar changed
     * @param view itemView from a holder in a calendar that represents day
     * @param localDate new date
     */
    private void dataChangedAction(View view, LocalDate localDate) {
        waterViewModel.loadWaterByDate(localDate);
        this.finish();
    }

    /**
     * Finds Water with specific date in a list, finds first one in a list(it should have only one anyway)
     * @param waters list of waters
     * @param date to fond
     * @return Optional of a found water
     */
    private Optional<Water> findWaterByDate(List<Water> waters, LocalDate date) {
        return waters.stream().filter(water -> water.getDate().equals(date)).findFirst();
    }
}
