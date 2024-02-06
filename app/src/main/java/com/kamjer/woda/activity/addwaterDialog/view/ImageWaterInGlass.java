package com.kamjer.woda.activity.addwaterDialog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.google.android.material.imageview.ShapeableImageView;
import com.kamjer.woda.viewmodel.WaterViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageWaterInGlass extends ShapeableImageView {

    private Path customPath;
//    image size in pd
    private int imageSizeX = 200;
    private int imageSizeY = 200;

    float scaleX;
    float scaleY;

    private Paint paint = new Paint();
    private Matrix matrix = new Matrix();
    int amount = 0;

    public ImageWaterInGlass(Context context) {
        super(context);
    }

    public ImageWaterInGlass(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWaterInGlass(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAmount(int amount) {
        this.amount = amount;
        StringBuilder bd = new StringBuilder();

        if (amount<= WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO) {
//          creating string builder for a path
//          initial point to move mouse to
            FloatPoint stLeftPoint = newLeftPointInGlass(amount);
//          appending initial point
            bd.append("M").
                    append(stLeftPoint.getX()).
                    append(",").
                    append(stLeftPoint.getY()).
                    append(" ");
//          creating bottom of a water in a glass and appending it
            String path = "C75.12,139.75 78.38,147.9 85.2,149.26 92.01,150.62 115.28,149.17 115.37,149.17 115.46,149.17 120.62,149.3 123.8,141.39 ";
            bd.append(path);
//          creating last point for water, the one on a right side and appending it
            FloatPoint rightPoint = newRightPointInGlass(amount);
            bd.append("L").
                    append(rightPoint.getX()).
                    append(",").
                    append(rightPoint.getY()).
                    append(" Z");
        } else {
//          creating string builder for a path
//          initial point to move mouse to
            FloatPoint stLeftPoint = newLeftPointInPitcher(amount);
//          appending initial point
            bd.append("M").
                    append(stLeftPoint.getX()).
                    append(",").
                    append(stLeftPoint.getY()).
                    append(" ");

            String path = "C127.05,135.75 126.75,138.74 124.94,139.8 124.94,139.8 78.91,139.9 78.88,139.8 77.12,139.61 76.5,135.8 76.5,135.8 ";
            bd.append(path);

//          creating last point for water, the one on a right side and appending it
            FloatPoint rightPoint = newRightPointInPitcher(amount);
            bd.append("L").
                    append(rightPoint.getX()).
                    append(",").
                    append(rightPoint.getY()).
                    append(" Z");
        }

//      parsing path
        customPath = PathParser.createPathFromPathData(bd.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        scaleX = (float) getWidth() / imageSizeX;
        scaleY = (float) getHeight() / imageSizeY;

        matrix.postScale(scaleX, scaleY);

        // Apply scaling to the path
        customPath.transform(matrix);

        int color = 0xFF337cf2;

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawPath(customPath, paint);
    }

    private List<FloatPoint> separatePathData(String path) {
        return Arrays.stream(path.split(" ")).map(s -> {
            String[] coordinates = s.split(",");
            float x = Integer.parseInt(coordinates[0]);
            float y = Integer.parseInt(coordinates[1]);
            return new FloatPoint(x, y);}
        ).collect(Collectors.toList());
    }

    private FloatPoint newLeftPointInGlass(float amount) {
//      angular coefficient
        float aLeft =   6.589041096f;
        float bLeft = -355.1369863f;

//      calculating y position on a left side of a glass
        float y = clcYInGlass(amount);
//      calculating x position based on a already defined side of a glass
        float x = (y - bLeft) / aLeft;
//      returning new point
        return new FloatPoint(x, y);
    }

    private FloatPoint newRightPointInGlass(float amount) {
//      angular coefficient
        float aRight = -6.119047619f;
        float bRight = 898.9380952f;

//      calculating y position on a right side of a glass
        float y = clcYInGlass(amount);
//      calculating x position based on a already defined side of a glass
        float x = (y - bRight) / aRight;
//      returning new point
        return new FloatPoint(x, y);
    }

    private float clcYInGlass(float amount) {
//      angular coefficient
        float a = -2.525252525f;
        float b = 376.2626263f;
//      y value for a glass (from y = ax + b transformed to x = (y - b) / a)
        return (amount - b) / a;
    }

    private FloatPoint newLeftPointInPitcher(float amount) {
//      angular coefficient
        float aLeft = -4.686567164f;
        float bLeft = 731.4626866f;

//      calculating y position on a left side of a glass
        float y = clcYInPitcher(amount);
//      calculating x position based on a already defined side of a glass
        float x = (y - bLeft) / aLeft;
//      returning new point
        return new FloatPoint(x, y);
    }

    private FloatPoint newRightPointInPitcher(float amount) {
//      angular coefficient
        float aRight = 5.190082645f;
        float bRight = -245.6710744f;

//      calculating y position on a right side of a glass
        float y = clcYInPitcher(amount);
//      calculating x position based on a already defined side of a glass
        float x = (y - bRight) / aRight;
//      returning new point
        return new FloatPoint(x, y);
    }

    private float clcYInPitcher(float amount) {
        float a = -14.92537313f;
        float b = 2089.552239f;

        return (amount - b) / a;
    }

    public static double roundAvoid(float value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private static class FloatPoint {
        private float x;
        private float y;

        public FloatPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
