package com.kamjer.woda.activity.calendaractivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.mycalendarview.CalendarViewHolder;
import com.kamjer.mycalendarview.CalendarView;
import com.kamjer.mycalendarview.SelectedDataChangedListener;
import com.kamjer.woda.R;
import com.kamjer.woda.activity.calendaractivity.viewHolder.WaterCalendarViewHolder;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CalendarActivity extends AppCompatActivity {

    private WaterViewModel waterViewModel;
    private CalendarView calendarView;
    private List<WaterDayWithWaters> waters;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.calendar_activity_layout);

//      getting viewModel
        waterViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(WaterViewModel.class);

//      getting date from active water
        LocalDate defaultSelectedDate = waterViewModel.getActiveDate();

//      after finding data creating calendar and operating on it
        calendarView = findViewById(R.id.calendarViewSelectDate);


//      setting loaded selected date
        calendarView.setSelectedDate(defaultSelectedDate);
        calendarView.setCustomCalendarViewHolder(WaterCalendarViewHolder.class, R.layout.water_calendar_cell);

//      setting calendar listeners
        calendarView.setSelectedDateChangedListener(this::dataChangedAction);
        calendarView.setCustomHolderBehavior(holder -> colorDays(waters, holder));

        SelectedDataChangedListener selectedDataChangedListener = (view, localDate) -> fetchData();

        calendarView.setNextMonthChangeListener(selectedDataChangedListener);
        calendarView.setPreviousMonthChangeListener(selectedDataChangedListener);

//      fetching data from database
        fetchData();
    }

    private void fetchData() {
        waterViewModel.loadAllWaterDayWithWaters(waters -> {
            CalendarActivity.this.waters = waters;
//          after complete setup show days of month
            calendarView.showMonthView();
        });
    }

    /**
     * Colors dates in a calendar with a proper color corresponding to
     * water drank in a day (gradient from red when 0% water drank, green when 100%),
     * if water does not exist in a day paints that day red.
     * @param waters list of waters from specific dates
     * @param holder to modify
     */
    private void colorDays(List<WaterDayWithWaters> waters, CalendarViewHolder holder) {
        if (holder instanceof WaterCalendarViewHolder) {
            WaterCalendarViewHolder waterHolder = (WaterCalendarViewHolder) holder;

            findWaterByDate(waters, holder.getDate()).map(waterFound -> {
//                  painting elements for a found water
//                  normalizing water drank
                float normalizedWater = Math.min(1.0f, (float) waterFound.getWaterDaySum() / (float) waterFound.getWaterDay().getWaterToDrink());
//                  we want it to be half see through (a = 255/2)
                int a = 255;
//                  calculating green value
                int g = (int) (255 * normalizedWater);
//                  calculating red value
                int r = (int) (255 - g);

                Drawable circle = ResourcesCompat.getDrawable(CalendarActivity.this.getResources(), R.drawable.circle, null);
                if (circle != null) {
                    circle.setColorFilter(Color.argb(a, r, g, 0), PorterDuff.Mode.SRC_OUT);
                    waterHolder.getCircle().setBackground(circle);
                }
                return waterFound;
            }).orElseGet(() -> {
//                  we want it to be half see through
                int a = 255;
//                  painting day in red (no value found, no water in a database, no water drank)
                int r = 255;
                Drawable circle = ResourcesCompat.getDrawable(CalendarActivity.this.getResources(), R.drawable.circle, null);
                if (circle != null) {
                    circle.setColorFilter(Color.argb(a, r, 0, 0), PorterDuff.Mode.SRC_OUT);
                    waterHolder.getCircle().setBackground(circle);
                }
                return null;
            });
        }
    }

    /**
     * Action when data in a calendar changed
     * @param view itemView from a holder in a calendar that represents day
     * @param localDate new date
     */
    private void dataChangedAction(View view, LocalDate localDate) {
        waterViewModel.loadWaterDayWithWatersByDate(localDate);
        this.finish();
    }

    /**
     * Finds Water with specific date in a list, finds first one in a list(it should have only one anyway)
     * @param waters list of waters
     * @param date to fond
     * @return Optional of a found water
     */
    private Optional<WaterDayWithWaters> findWaterByDate(List<WaterDayWithWaters> waters, LocalDate date) {
        return waters.stream().filter(water -> water.getWaterDay().getDate().equals(date)).findFirst();
    }
}
