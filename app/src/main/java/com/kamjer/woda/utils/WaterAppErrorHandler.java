package com.kamjer.woda.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kamjer.woda.R;

import java.util.Optional;

import io.reactivex.rxjava3.functions.Consumer;

public class WaterAppErrorHandler implements Consumer<Throwable> {

    private final Context context;

    public WaterAppErrorHandler(Context context) {
        this.context = context;
    }

    @Override
    public void accept(Throwable e) {
        if (e != null) {
            String errorMessage = Optional.ofNullable(e.getMessage()).orElse("Error detected, source not known");
            Log.e(this.getClass().getName(), errorMessage);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
