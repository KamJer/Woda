package com.kamjer.woda.activity.mainactivity.waterimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.model.WaterDayWithWaters;
import com.kamjer.woda.utils.FloatPoint;
import com.kamjer.woda.viewmodel.WaterDataRepository;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class WaterImage extends androidx.appcompat.widget.AppCompatImageView {

    private Queue<WaterImageDrawable> waterImageDrawables = new LinkedList<>();
    //    image size in pd
    private static final int IMAGE_SIZE_X = 200;
    private static final int IMAGE_SIZE_Y = 200;
    private Drawable glassDrawable;
    private Matrix matrix1;

    public WaterImage(Context context) {
        super(context);
        init();
    }

    public WaterImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterImage(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setScaleType(ScaleType.CENTER_INSIDE);
    }

    public void setAmount(WaterDayWithWaters waterDayWithWaters) {
        if (glassDrawable == null) {
            glassDrawable = getDrawable();
        }

        Drawable[] layers = new Drawable[waterDayWithWaters.getWaters().size() + 1];
        layers[0] = glassDrawable;

        // Calculate scaling factors
        float scaleX = (float) glassDrawable.getIntrinsicWidth() / IMAGE_SIZE_X;
        float scaleY = (float) glassDrawable.getIntrinsicHeight() / IMAGE_SIZE_Y;

        matrix1 = new Matrix();
        matrix1.postScale(scaleX, scaleY);

        int maxValue = Math.max(waterDayWithWaters.getWaterDay().getWaterToDrink(), waterDayWithWaters.getWaterDaySum());

        waterDayWithWaters.getWaters().sort((o1, o2) -> o2.getWaterDrank() - o1.getWaterDrank());

        for (int i = 0; i < waterDayWithWaters.getWaters().size(); i++) {
            int amount = 0;
            for (int j = i; j < waterDayWithWaters.getWaters().size(); j++) {
                amount += waterDayWithWaters.getWaters().get(j).getWaterDrank();
            }
//          creating string builder for a path
            StringBuilder bd = new StringBuilder();
//          initial point to move mouse to
            FloatPoint stLeftPoint = newLeftPointInGlass(maxValue , amount);
//          appending initial point
            bd.append("M").
                    append(stLeftPoint.getX()).
                    append(",").
                    append(stLeftPoint.getY()).
                    append(" ");
//          creating bottom of a water in a glass and appending it
            String path = "C75, 140 78.38,147.9 85.2,149.26 92.01,150.62 115.28,149.17 115.37,149.17 115.46,149.17 120.62,149.3 123.8, 141.39 ";
            bd.append(path);

//          creating last point for water, the one on a right side and appending it
            FloatPoint rightPoint = newRightPointInGlass(maxValue, amount);
            bd.append("L").
                    append(rightPoint.getX()).
                    append(",").
                    append(rightPoint.getY()).
                    append(" Z");

            Path path1 = (PathParser.createPathFromPathData(bd.toString()));
//          since for the rest of a code it is necessary for type to exists get default one (Water) if for some reason there is no type for water
            Type type = Optional.ofNullable(WaterDataRepository.getInstance().
                    getWaterTypes().
                    get(waterDayWithWaters.getWaters().get(i).getTypeId()))
                    .orElse(WaterDataRepository.getInstance().getDefaultDrinksTypes()[0]);
            WaterImageDrawable waterImageDrawable = new WaterImageDrawable(path1, type, waterDayWithWaters.getWaters().get(i).getWaterDrank());

            waterImageDrawable.setMatrix(matrix1);
            layers[i + 1] = waterImageDrawable;
            waterImageDrawables.add(waterImageDrawable);
        }
        setImageDrawable(new LayerDrawable(layers));
    }

    private FloatPoint newLeftPointInGlass(int glassSize, float amount) {
//      angular coefficient
        float aLeft =   6.589041096f;
        float bLeft = -355.1369863f;

//      calculating y position on a left side of a glass
        float y = clcYInGlass(glassSize, amount);
//      calculating x position based on a already defined side of a glass
        float x = (y - bLeft) / aLeft;
//      returning new point
        return new FloatPoint(x, y);
    }


    private FloatPoint newRightPointInGlass(int glassSize, float amount) {
//      angular coefficient
        float aRight = -6.119047619f;
        float bRight = 898.9380952f;

//      calculating y position on a right side of a glass
        float y = clcYInGlass(glassSize, amount);
//      calculating x position based on a already defined side of a glass
        float x = (y - bRight) / aRight;
//      returning new point
        return new FloatPoint(x, y);
    }

    private float clcYInGlass(int glassSize, float amount) {
//      for how match water point in glass symbolizes
        int bottomGlassXImage = 149;
        int bottomGlassYWater = 0;

        int topGlassXImage = 50;
//      angular coefficient
        FloatPoint aAndB = calculateCoefficients(topGlassXImage, glassSize, bottomGlassXImage, bottomGlassYWater);
        float a =  aAndB.getX();
        float b = aAndB.getY();

//      y value for a glass (from y = ax + b transformed to x = (y - b) / a)
        return (amount - b) / a;
    }

    private FloatPoint calculateCoefficients(int xA, int yA, int xB, int yB) {
        return new FloatPoint(
                (float) (yA - yB) / (xA - xB),
                yA - ((float) (yA - yB) / (xA - xB)) * xA);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
