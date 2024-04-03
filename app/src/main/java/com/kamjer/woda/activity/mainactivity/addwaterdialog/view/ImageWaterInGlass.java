package com.kamjer.woda.activity.mainactivity.addwaterdialog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.google.android.material.imageview.ShapeableImageView;
import com.kamjer.woda.utils.FloatPoint;
import com.kamjer.woda.viewmodel.WaterViewModel;

public class ImageWaterInGlass extends ShapeableImageView {

    private Path customPath;
    private StringBuilder cutomPathStringBuilder;
//    image size in pd
    private static final int IMAGE_SIZE_X = 200;
    private static final int IMAGE_SIZE_Y = 200;
    private final Paint paint = new Paint();
    private final Matrix matrix = new Matrix();

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
        cutomPathStringBuilder = new StringBuilder();

        if (amount<= WaterViewModel.DEFAULT_WATER_DRANK_IN_ONE_GO) {
//          creating string builder for a path
//          initial point to move mouse to
            FloatPoint stLeftPoint = newLeftPointInGlass(amount);
//          appending initial point
            cutomPathStringBuilder.append("M").
                    append(stLeftPoint.getX()).
                    append(",").
                    append(stLeftPoint.getY()).
                    append(" ");
//          creating bottom of a water in a glass and appending it
            String path = "L75.48,140.02C75.95,142.47 78.01,148.98 85.17,149.02 85.17,149.02 115.36,149.04 115.36,149.04 122.04,149.02 124,142.55 124.52,140.05 ";
            cutomPathStringBuilder.append(path);
//          creating last point for water, the one on a right side and appending it
            FloatPoint rightPoint = newRightPointInGlass(amount);
            cutomPathStringBuilder.append("L").
                    append(rightPoint.getX()).
                    append(",").
                    append(rightPoint.getY()).
                    append(" Z");
        } else {
//          creating string builder for a path
//          initial point to move mouse to
            FloatPoint stLeftPoint = newLeftPointInPitcher(amount);
//          appending initial point
            cutomPathStringBuilder.append("M").
                    append(stLeftPoint.getX()).
                    append(",").
                    append(stLeftPoint.getY()).
                    append(" ");

            String path = "C127.05,135.75 126.75,138.74 124.94,139.8 124.94,139.8 78.91,139.9 78.88,139.8 77.12,139.61 76.5,135.8 76.5,135.8 ";
            cutomPathStringBuilder.append(path);

//          creating last point for water, the one on a right side and appending it
            FloatPoint rightPoint = newRightPointInPitcher(amount);
            cutomPathStringBuilder.append("L").
                    append(rightPoint.getX()).
                    append(",").
                    append(rightPoint.getY()).
                    append(" Z");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        matrix.reset();

        float scaleX = (float) getWidth() / IMAGE_SIZE_X;
        float scaleY = (float) getHeight() / IMAGE_SIZE_Y;

        matrix.postScale(scaleX, scaleY);

        // Apply scaling to the path
        customPath = PathParser.createPathFromPathData(cutomPathStringBuilder.toString());
        customPath.transform(matrix);

        int color = 0xFF337cf2;

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawPath(customPath, paint);
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
}
