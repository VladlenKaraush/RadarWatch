package com.example.vladlen.radarwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class RadarView extends View {

    private float currentAngleSecond = 90;
    private float currentAngleMinute = 90;
    private float currentAngleHour = 90;

    //first two bytes for opacity (00 is transparent)
    int[] outerColors = {
            0x00FFFFFF,
            0xFFFFFFFF,
    };
    int[] midColors = {
            0x00FF0000,
            0xFFFF0000,
    };
    int[] innerColors = {
            0x000000FF,
            0xFF0000FF,
    };

    //positions to change colors in midGradient
    float[] positions = {0.33f, 0.47f};
    boolean init = false;
    Matrix midMatrix, innerMatrix, outerMatrix;
    Shader midGradient, outerGradient, innerGradient;

    int canvasX, canvasY;

    private static final int STROKE_WIDTH = 70;
    private static final int INNER_STROKE_WIDTH = 50;
    private Paint innerPaint, outerPaint, whitePaint, midPaint, radarMapPaint;
    private RectF outerRect, midRect, innerRect;
    private int centerX, centerY, radius;

    public RadarView(Context context) {
        this(context, null);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!init) {

            radarMapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            radarMapPaint.setStyle(Paint.Style.STROKE);
            radarMapPaint.setStrokeWidth(4);
            radarMapPaint.setColor(Color.BLACK);

            //mid arc
            midPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            midPaint.setStyle(Paint.Style.STROKE);
            midPaint.setStrokeWidth(STROKE_WIDTH);

            whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            whitePaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            whitePaint.setStyle(Paint.Style.FILL);

            //inner arc
            innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            innerPaint.setStyle(Paint.Style.STROKE);
            innerPaint.setStrokeWidth(INNER_STROKE_WIDTH);

            //outer arc
            outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            outerPaint.setStyle(Paint.Style.STROKE);
            outerPaint.setStrokeWidth(STROKE_WIDTH);
            init = true;
        }
        //seconds
        new Thread(() -> {
            while (true) {
                this.currentAngleSecond += 1;
                this.currentAngleSecond = this.currentAngleSecond % 360;
                try {
                    Thread.sleep(35);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //minutes
        new Thread(() -> {
            while (true) {
                this.currentAngleMinute += 1;
                this.currentAngleMinute = this.currentAngleMinute % 360;
                try {
                    Thread.sleep(55);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //hours
        new Thread(() -> {
            while (true) {
                this.currentAngleHour += 1;
                this.currentAngleHour = this.currentAngleHour % 360;
                try {
                    Thread.sleep(75);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (outerRect == null) {

            centerX = getMeasuredWidth() / 2;
            centerY = getMeasuredHeight() / 2;
            radius = Math.min(centerX, centerY);


            int startTop = STROKE_WIDTH / 2;
            int startLeft = startTop;

            int endBottom = 2 * (radius) - startTop;
            int endRight = endBottom;

            outerRect = new RectF(startLeft, startTop, endRight, endBottom);

            centerX = (int) outerRect.centerX();
            centerY = (int) outerRect.centerY();

            startLeft += STROKE_WIDTH;
            startTop += STROKE_WIDTH;
            endBottom -= STROKE_WIDTH;
            endRight -= STROKE_WIDTH;

            midRect = new RectF(startLeft, startTop, endRight, endBottom);

            startLeft += STROKE_WIDTH;
            startTop += STROKE_WIDTH;
            endBottom -= STROKE_WIDTH;
            endRight -= STROKE_WIDTH;
            innerRect = new RectF(startLeft, startTop, endRight, endBottom);


            midGradient = new SweepGradient(centerX, centerY, midColors, positions);
            midMatrix = new Matrix();
            midMatrix.preRotate(270f, centerX, centerY);
            midGradient.setLocalMatrix(midMatrix);
            midPaint.setShader(midGradient);

            innerGradient = new SweepGradient(centerX, centerY, innerColors, positions);
            innerMatrix = new Matrix();
            innerMatrix.preRotate(270f, centerX, centerY);
            innerGradient.setLocalMatrix(innerMatrix);
            innerPaint.setShader(innerGradient);

            outerGradient = new SweepGradient(centerX, centerY, outerColors, positions);
            outerMatrix = new Matrix();
            outerMatrix.preRotate(270f, centerX, centerY);
            outerGradient.setLocalMatrix(outerMatrix);
            outerPaint.setShader(outerGradient);

        }

        invalidate();

        canvas.drawCircle(outerRect.centerX(), outerRect.centerY(), centerX / 4, radarMapPaint);
        canvas.drawCircle(centerX, centerY, centerX / 3, radarMapPaint);
        canvas.drawCircle(centerX, centerY, centerX / 2, radarMapPaint);
        canvas.drawCircle(centerX, centerY, (float) (centerX / 1.3), radarMapPaint);
        canvas.drawCircle(centerX, centerY, centerX, radarMapPaint);

        //outer arc
        outerMatrix.preRotate(currentAngleMinute, centerX, centerY);
        outerGradient.setLocalMatrix(outerMatrix);
        canvas.drawArc(outerRect, currentAngleMinute, 90, false, outerPaint);
        outerMatrix.preRotate(-currentAngleMinute, centerX, centerY);

        //mid arc
        midMatrix.preRotate(currentAngleSecond, centerX, centerY);
        midGradient.setLocalMatrix(midMatrix);
        canvas.drawArc(midRect, currentAngleSecond, 90, false, midPaint);
        midMatrix.preRotate(-currentAngleSecond, centerX, centerY);

        //inner arc
        innerMatrix.preRotate(currentAngleHour, centerX, centerY);
        innerGradient.setLocalMatrix(innerMatrix);
        canvas.drawArc(innerRect, currentAngleHour, 90, false, innerPaint);
        innerMatrix.preRotate(-currentAngleHour, centerX, centerY);


    }


}