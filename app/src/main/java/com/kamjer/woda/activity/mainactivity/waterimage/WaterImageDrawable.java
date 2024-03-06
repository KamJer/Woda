package com.kamjer.woda.activity.mainactivity.waterimage;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.VectorDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kamjer.woda.model.Type;
import com.kamjer.woda.utils.FloatPoint;

public class WaterImageDrawable extends VectorDrawable {

    private final Path path;
    private Matrix matrix;
    private final Paint paint = new Paint();

    private final Type type;
    private final FloatPoint textTypeStartPoint;
    private final FloatPoint textAmountStartPoint;
    private final String amountText;

    private static final int PADDING_TEXT_TOP = 5;
    private static final int PADDING_TEXT_LEFT = 10;
    private static final int PADDING_TEXT_RIGHT = 15;

    public WaterImageDrawable(Path path, Type type, int amount) {
        this.path = path;
        this.type = type;
//      setting paint
        paint.setColor(type.getColor());
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//      computing bounds of a path and texts for text placement
        RectF pathBounds = new RectF();
        path.computeBounds(pathBounds, false);
        Rect amountTextBounds = new Rect();
        Rect typeNameTextBounds = new Rect();
        amountText = amount + " ml";
        paint.getTextBounds(amountText, 0, amountText.length() - 1, amountTextBounds);
        paint.getTextBounds(type.getType(), 0, type.getType().length() - 1, typeNameTextBounds);
        textTypeStartPoint = new FloatPoint(pathBounds.right + PADDING_TEXT_LEFT, pathBounds.top + PADDING_TEXT_TOP);
        textAmountStartPoint = new FloatPoint(pathBounds.left - PADDING_TEXT_RIGHT - amountTextBounds.width(), pathBounds.top + PADDING_TEXT_TOP);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(matrix);
        canvas.drawPath(path, paint);
//        type of a water drank
        canvas.drawText(type.getType(), textTypeStartPoint.getX(), textTypeStartPoint.getY(), paint);
//        amount of a water drank
        canvas.drawText(amountText, textAmountStartPoint.getX(), textAmountStartPoint.getY(), paint);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
}
