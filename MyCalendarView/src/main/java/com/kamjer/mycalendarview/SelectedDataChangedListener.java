package com.kamjer.mycalendarview;

import android.view.View;

import java.time.LocalDate;

@FunctionalInterface
public interface SelectedDataChangedListener {
    void dataChangedAction(View view, LocalDate localDate);
}
