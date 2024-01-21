package com.kamjer.mycalendarview;

import android.view.View;

import java.time.LocalDate;

public interface SelectedDataChangedListener {
    public void dataChangedAction(View view, LocalDate localDate);
}
