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
    private int outerCircleOffset = 4;
    private int sweepAngle = 70;

    //first two bytes for opacity (00 is transparent)
    private int color1 = 0x00FF3333;
    private int color2 = 0xFFFF3333;
    int[] outerColors = {
            color1,
            color2,
    };
    int[] midColors = {
            color1,
            color2,
    };
    int[] innerColors = {
            color1,
            color2,
    };

    //positions to change colors in midGradient
    float[] positions = {0.33f, 0.47f};
    float[] newPositions = {0.1f, 0.7f};
    boolean init = false;
    Matrix midMatrix, innerMatrix, outerMatrix;
    Shader midGradient, outerGradient, innerGradient;
    private static final int STROKE_WIDTH = 70;
    private static final int INNER_STROKE_WIDTH = 50;
    private Paint innerPaint, outerPaint, whitePaint, midPaint, radarMapPaint, radarCirclesPaint;
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

            //light vertical and horizontal lines
            radarMapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            radarMapPaint.setStyle(Paint.Style.STROKE);
            radarMapPaint.setStrokeWidth(4);
            radarMapPaint.setColor(Color.rgb(255, 100, 100));

            //black circles
            radarCirclesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            radarCirclesPaint.setStyle(Paint.Style.STROKE);
            radarCirclesPaint.setStrokeWidth(4);
            radarCirclesPaint.setColor(Color.BLACK);

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
                this.currentAngleSecond = this.currentAngleSecond  + 1 % 360;
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
                this.currentAngleMinute = this.currentAngleMinute + 1 % 360;
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
                this.currentAngleHour = this.currentAngleHour + 1 % 360;
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
            System.out.println("x = " + centerX + ", Y = " + centerY);

            int startTop = STROKE_WIDTH / 2 + outerCircleOffset;
            int startLeft = startTop;

            int endBottom = 2 * (radius) - startTop;
            int endRight = endBottom;

            outerRect = new RectF(startLeft, startTop, endRight, endBottom);

            centerX = (int) outerRect.centerX();
            centerY = (int) outerRect.centerY();

            //offset for mid circle
            startLeft += STROKE_WIDTH + outerCircleOffset;
            startTop  += STROKE_WIDTH + outerCircleOffset;
            endBottom -= STROKE_WIDTH + outerCircleOffset;
            endRight  -= STROKE_WIDTH + outerCircleOffset;

            midRect = new RectF(startLeft, startTop, endRight, endBottom);

            //offset for inner circle
            int diff = (STROKE_WIDTH - INNER_STROKE_WIDTH) / 2;
            startLeft += STROKE_WIDTH + outerCircleOffset - diff;
            startTop  += STROKE_WIDTH + outerCircleOffset - diff;
            endBottom -= STROKE_WIDTH + outerCircleOffset - diff;
            endRight  -= STROKE_WIDTH + outerCircleOffset - diff;
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
        //draw light lines across all view through the center

        int midCircleRadius = 50;
        //horizontal line
        canvas.drawLine(0, centerY, centerX  - midCircleRadius, centerY, radarMapPaint);
        canvas.drawLine(centerX + midCircleRadius, centerY, centerX * 2, centerY, radarMapPaint);

        //vertical line
        canvas.drawLine(centerX, 0, centerX, centerY  - midCircleRadius, radarMapPaint);
        canvas.drawLine(centerX, centerY + midCircleRadius, centerX, centerY  * 2, radarMapPaint);

        //light mid circle
        canvas.drawCircle(centerX, centerY, midCircleRadius, radarMapPaint);

        //draw black circles between arcs
        canvas.drawCircle(centerX, centerY, centerX - outerCircleOffset / 2, radarCirclesPaint);
        canvas.drawCircle(centerX, centerY, centerX - outerCircleOffset * 3 / 2 - STROKE_WIDTH, radarCirclesPaint);
        canvas.drawCircle(centerX, centerY, centerX - outerCircleOffset * 5 / 2 - STROKE_WIDTH * 2, radarCirclesPaint);


        //outer arc
        outerMatrix.preRotate(currentAngleMinute, centerX, centerY);
        outerGradient.setLocalMatrix(outerMatrix);
        canvas.drawArc(outerRect, currentAngleMinute, sweepAngle, false, outerPaint);
        outerMatrix.preRotate(-currentAngleMinute, centerX, centerY);

        //mid arc
        midMatrix.preRotate(currentAngleSecond, centerX, centerY);
        midGradient.setLocalMatrix(midMatrix);
        canvas.drawArc(midRect, currentAngleSecond, sweepAngle, false, midPaint);
        midMatrix.preRotate(-currentAngleSecond, centerX, centerY);

        //inner arc
        innerMatrix.preRotate(currentAngleHour, centerX, centerY);
        innerGradient.setLocalMatrix(innerMatrix);
        canvas.drawArc(innerRect, currentAngleHour, sweepAngle, false, innerPaint);
        innerMatrix.preRotate(-currentAngleHour, centerX, centerY);


    }


}