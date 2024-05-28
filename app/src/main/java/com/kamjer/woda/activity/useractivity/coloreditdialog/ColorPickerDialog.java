package com.kamjer.woda.activity.useractivity.coloreditdialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.MutableLiveData;

import com.kamjer.woda.R;
import com.kamjer.woda.model.Type;

import java.util.Optional;

public class ColorPickerDialog extends AppCompatActivity {
    private final MutableLiveData<Integer> color = new MutableLiveData<>();

    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;

    private ImageButton colorIndicator;
    private ImageView colorIndicatorAccept;

    private Type type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = (Type) Optional.ofNullable(getIntent().getSerializableExtra("type")).orElse(new Type());
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

        colorIndicatorAccept = findViewById(R.id.acceptImage);

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

        int max = Math.max(red, Math.max(green, blue));
        int inversColor = Color.argb(255, 255 - max, 255 - max, 255 - max);

        Drawable buttonShape = ResourcesCompat.getDrawable(getResources(), R.drawable.color_type_button_shape, null);
        if (buttonShape != null) {
            buttonShape.setColorFilter(rgb, PorterDuff.Mode.SRC_OVER);
            colorIndicator.setBackground(buttonShape);
        } else {
            Toast.makeText(this, getResources().getText(R.string.error_message_can_not_load), Toast.LENGTH_LONG).show();
            this.finish();
        }

        Drawable acceptImage = ResourcesCompat.getDrawable(getResources(), R.drawable.accept, null);
        if (acceptImage != null) {
            acceptImage.setTint(inversColor);
            colorIndicatorAccept.setBackground(acceptImage);
        } else {
            Toast.makeText(this, getResources().getText(R.string.error_message_can_not_load), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private void acceptAction(View v) {
        getIntent().putExtra("type", type);
        setResult(Activity.RESULT_OK, getIntent());
        this.finish();
    }
}
