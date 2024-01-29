package com.kamjer.woda.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.mycalendarview.CalendarViewAdapter;
import com.kamjer.mycalendarview.CalendarViewHolder;
import com.kamjer.mycalendarview.CustomHolderBehavior;
import com.kamjer.mycalendarview.DaysOfMonthView;
import com.kamjer.mycalendarview.MyCalendarView;
import com.kamjer.mycalendarview.SelectedDataChangedListener;
import com.kamjer.woda.R;
import com.kamjer.woda.activity.calendaractivity.WaterCalendarViewHolder;
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
    private List<Water> waters;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.calendar_activity_layout);

//      getting viewModel
        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);

//      getting date from active water
        LocalDate defaultSelectedDate = waterViewModel.getWaterValue().getDate();

//      fetching data from database
        waterViewModel.loadWatersFromMonth(defaultSelectedDate, waters -> {
            CalendarActivity.this.waters = waters;

//          after finding data creating calendar and operating on it
            calendarView = findViewById(R.id.calendarViewSelectDate);

//          setting loaded selected date
            calendarView.setSelectedDate(defaultSelectedDate);
            calendarView.setCustomCalendarViewHolder(WaterCalendarViewHolder.class, R.layout.water_calendar_cell);


//          setting calendar listeners
            calendarView.setSelectedDateChangedListener(this::dataChangedAction);
            calendarView.setCustomHolderBehavior(holder -> colorDays(waters, holder));

            SelectedDataChangedListener selectedDataChangedListener = (view, localDate) ->
                    waterViewModel.loadWatersFromMonth(calendarView.getSelectedDate(), waters1 ->
                            CalendarActivity.this.waters = waters1);

            calendarView.setNextMonthChangeListener(selectedDataChangedListener);
            calendarView.setPreviousMonthChangeListener(selectedDataChangedListener);
        });


    }

    /**
     * Colors dates in a calendar with a proper color corresponding to
     * water drank in a day (gradient from red when 0% water drank, green when 100%),
     * if water does not exist in a day paint that day red.
     * @param waters list of water from specific dates
     * @param holder to modify
     */
    private void colorDays(List<Water> waters, CalendarViewHolder holder) {
        if (holder instanceof WaterCalendarViewHolder) {
            WaterCalendarViewHolder waterHolder = (WaterCalendarViewHolder) holder;

            findWaterByDate(waters, holder.getDate()).map(waterFound -> {
//                  painting elements for a found water
//                  normalizing water drank
                float normalizedWater = Math.min(1.0f, (float) waterFound.getWaterDrank() / (float) waterFound.getWaterToDrink());
//                  we want it to be half see through (a = 255/2)
                int a = 255;
//                  calculating green value
                int g = (int) (255 * normalizedWater);
//                  calculating red value
                int r = (int) (255 - g);

                Drawable circle = ResourcesCompat.getDrawable(CalendarActivity.this.getResources(), R.drawable.circle, null);
                circle.setColorFilter(Color.argb(a, r, g, 0), PorterDuff.Mode.SRC_IN);
                waterHolder.getCircle().setBackground(circle);
                return waterFound;
            }).orElseGet(() -> {
//                  we want it to be half see through
                int a = 255;
//                  painting day in red (no value found, no water in a database, no water drank)
                int r = 255;
                Drawable circle = ResourcesCompat.getDrawable(CalendarActivity.this.getResources(), R.drawable.circle, null);
                circle.setColorFilter(Color.argb(a, r, 0, 0), PorterDuff.Mode.SRC_IN);
                waterHolder.getCircle().setBackground(circle);
                return null;
            });
        }
    }

    private static int calculateGradient(int[] color1, int[] color2, float percent) {
        int r = interpolate(color1[0], color2[0], percent);
        int g = interpolate(color1[1], color2[1], percent);
        int b = interpolate(color1[2], color2[2], percent);

        return Color.argb(255, r, g, b);
    }

    private static int interpolate(float start, float end, float percent) {
        return (int) (start + Math.round((end - start) * percent));
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
