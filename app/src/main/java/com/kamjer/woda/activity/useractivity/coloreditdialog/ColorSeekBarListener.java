package com.kamjer.woda.activity.useractivity.coloreditdialog;

import android.graphics.Color;
import android.widget.SeekBar;

import androidx.lifecycle.MutableLiveData;

import java.util.Optional;

public class ColorSeekBarListener implements SeekBar.OnSeekBarChangeListener {

    public enum Chanel {
        RED,
        GREEN,
        BLUE
    }
    private MutableLiveData<Integer> rgb;
    private Chanel chanel;

    public ColorSeekBarListener(MutableLiveData<Integer> rgb, Chanel chanel) {
        this.rgb = rgb;
        this.chanel = chanel;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int color = Optional.ofNullable(rgb.getValue()).orElse(Color.BLACK);
            switch (chanel) {
                case RED: {
                    color = Color.rgb(progress, Color.green(color), Color.blue(color));
                    break;
                }
                case GREEN: {
                    color = Color.rgb(Color.red(color), progress, Color.blue(color));
                    break;
                }
                case BLUE: {
                    color = Color.rgb(Color.red(color), Color.green(color), progress);
                    break;
                }
            }
            rgb.setValue(color);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
