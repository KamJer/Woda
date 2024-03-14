package com.kamjer.woda.activity.useractivity.coloreditdialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.util.Optional;

public class ColorPickerDialog extends AppCompatActivity {

    private final MutableLiveData<Integer> color = new MutableLiveData<>();

    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;

    private ImageButton colorIndicator;

    private Type type;
    private int holderPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = new Type();
        type.setId(getIntent().getLongExtra("id", 0));
        type.setColor(getIntent().getIntExtra("color", Color.BLACK));
        type.setType(getIntent().getStringExtra("type"));
        holderPosition = getIntent().getIntExtra("position", 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.color_picker_dialog);

        redSeekBar = findViewById(R.id.redSeekBar);
        greenSeekBar = findViewById(R.id.greenSeekBar);
        blueSeekBar = findViewById(R.id.blueSeekBar);

        colorIndicator = findViewById(R.id.colorIndicatorImageView);
        colorIndicator.setOnClickListener(this::acceptAction);

        redSeekBar.setOnSeekBarChangeListener(new ColorSeekBarListener(color, ColorSeekBarListener.Chanel.RED));
        greenSeekBar.setOnSeekBarChangeListener(new ColorSeekBarListener(color, ColorSeekBarListener.Chanel.GREEN));
        blueSeekBar.setOnSeekBarChangeListener(new ColorSeekBarListener(color, ColorSeekBarListener.Chanel.BLUE));

        color.observe(this, this::colorObserver);
        color.setValue(type.getColor());
    }

    private void colorObserver(int rgb) {
//        loading all colors
        int red = Color.red(rgb);
        int green = Color.green(rgb);
        int blue = Color.blue(rgb);

        redSeekBar.setProgress(red);
        greenSeekBar.setProgress(green);
        blueSeekBar.setProgress(blue);

        type.setColor(rgb);

        Drawable buttonShape = ResourcesCompat.getDrawable(getResources(), R.drawable.color_type_button_shape, null);
        if (buttonShape != null) {
            buttonShape.setColorFilter(rgb, PorterDuff.Mode.SRC_IN);
            colorIndicator.setBackground(buttonShape);
        } else {
            Toast.makeText(this, getResources().getText(R.string.error_message_can_not_load), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private void acceptAction(View v) {
        int color = Optional.ofNullable(this.color.getValue()).orElse(Color.BLACK);
        getIntent().putExtra("id", type.getId());
        getIntent().putExtra("color", color);
        getIntent().putExtra("type", type.getType());
        getIntent().putExtra("position", holderPosition);

        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    }
}
