package com.example.vladlen.radarwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class RadarView extends View {

    private float currentAngleSecond = 0;
    private float currentAngleMinute = 90;
    private float currentAngleHour = 180;
    private final int outerCircleOffset = 4;
    private final int sweepAngle = 70;
    private final int labelSize = 13;
    private int midCircleRadius = 50;
    private int hour, minute, second;
    private final String[] labels = {
            "12", "60", "9", "45", "30", "6", "15", "3"
    };

    //first two bytes for opacity (00 is transparent)
    private final int color1 = 0x00Fa4040;
    private final int color2 = 0xFFFa4040;
    final int[] outerColors = {
            color1,
            color2,
    };
    final int[] midColors = {
            color1,
            color2,
    };
    final int[] innerColors = {
            color1,
            color2,
    };

    //positions to change colors in midGradient
    final float[] positions = {0.33f, 0.47f};
    float[] newPositions = {0.1f, 0.7f};
    boolean init = false;
    Matrix midMatrix, innerMatrix, outerMatrix;
    Shader midGradient, outerGradient, innerGradient;
    private static final int STROKE_WIDTH = 70;
    private static final int INNER_STROKE_WIDTH = 35;
    private Paint innerPaint, outerPaint, labelsPaint, midPaint, radarMapPaint, radarCirclesPaint, labelsBackgroundPaint;
    private RectF outerRect, midRect, innerRect;
    private Rect labelBounds, labelBackground;
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

            //current time
            Date time = Calendar.getInstance().getTime();
            hour = time.getHours();
            minute = time.getMinutes();
            second = time.getSeconds();

            //light vertical and horizontal lines
            radarMapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            radarMapPaint.setStyle(Paint.Style.STROKE);
            radarMapPaint.setStrokeWidth(4);
            radarMapPaint.setColor(Color.rgb(255, 85, 85));

            //black circles
            radarCirclesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            radarCirclesPaint.setStyle(Paint.Style.STROKE);
            radarCirclesPaint.setStrokeWidth(4);
            radarCirclesPaint.setColor(Color.rgb(40,40,40));

            //mid arc
            midPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            midPaint.setStyle(Paint.Style.STROKE);
            midPaint.setStrokeWidth(STROKE_WIDTH);

            labelsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            labelsPaint.setColor(Color.rgb(255, 85, 85));
            labelsPaint.setStyle(Paint.Style.FILL);
            labelsPaint.setTextSize(labelSize * getResources().getDisplayMetrics().density);

            //inner arc
            innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            innerPaint.setStyle(Paint.Style.STROKE);
            innerPaint.setStrokeWidth(INNER_STROKE_WIDTH);

            //outer arc
            outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            outerPaint.setStyle(Paint.Style.STROKE);
            outerPaint.setStrokeWidth(STROKE_WIDTH);
            init = true;

            //background for numbers
            labelsBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelsBackgroundPaint.setColor(Color.rgb(80, 40, 40));
            labelsBackgroundPaint.setStyle(Paint.Style.FILL);

            labelBounds = new Rect();
            labelBackground = new Rect();

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
            System.out.println("init onDraw");
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


        //horizontal line
        canvas.drawLine(0, centerY, centerX  - midCircleRadius, centerY, radarMapPaint);
        canvas.drawLine(centerX + midCircleRadius, centerY, centerX * 2, centerY, radarMapPaint);

        //vertical line
        canvas.drawLine(centerX, 0, centerX, centerY  - midCircleRadius, radarMapPaint);
        canvas.drawLine(centerX, centerY + midCircleRadius, centerX, centerY  * 2, radarMapPaint);

        //light mid circle
        canvas.drawCircle(centerX, centerY, midCircleRadius, radarMapPaint);


        //number labels

        //left labels
        labelsPaint.getTextBounds(labels[2], 0, labels[2].length(), labelBounds);
        int height = labelBounds.height();
        int width = labelBounds.width();
        int base = STROKE_WIDTH / 2 + outerCircleOffset - width;
        //draw background for label
        labelBackground.set(base, centerY - height / 2,base + 2 * width, centerY + height / 2);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[2], base + width / 2, centerY + height / 2, labelsPaint);

        labelsPaint.getTextBounds(labels[3], 0, labels[3].length(), labelBounds);
        height = labelBounds.height();
        width = labelBounds.width();
        base = STROKE_WIDTH * 3 / 2 + 2 * outerCircleOffset - width *  2 / 3;
        //draw background for label
        labelBackground.set(base, centerY - height / 2, base + width * 4 / 3, centerY + height / 2);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[3], base + width / 6, centerY + height / 2, labelsPaint);


        //top labels
        labelsPaint.getTextBounds(labels[0], 0, labels[0].length(), labelBounds);
        height = labelBounds.height();
        width = labelBounds.width();
        base = outerCircleOffset + STROKE_WIDTH / 2 - height * 2 / 3;
        labelBackground.set(centerX - width / 2, base,centerX + width / 2, base + height * 4 / 3);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[0],  centerX - labelBounds.width() / 2,base + height * 7/6, labelsPaint);

        labelsPaint.getTextBounds(labels[1], 0, labels[1].length(), labelBounds);
        height = labelBounds.height();
        width = labelBounds.width();
        base = outerCircleOffset * 2 + STROKE_WIDTH * 3 / 2 - height * 2 / 3;
        labelBackground.set(centerX - width / 2,base,centerX + width / 2, base + height * 4 / 3);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[1],  centerX - labelBounds.width() / 2,base + height * 7 / 6, labelsPaint);

        //right labels
        labelsPaint.getTextBounds(labels[7], 0, labels[7].length(), labelBounds);
        height = labelBounds.height();
        width = labelBounds.width();
        base = centerX * 2 - (STROKE_WIDTH / 2 + outerCircleOffset + width );
        labelBackground.set(base, centerY - height / 2,base + width * 2, centerY + height / 2);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[7],  base + width / 2, centerY + height / 2, labelsPaint);

        labelsPaint.getTextBounds(labels[6], 0, labels[6].length(), labelBounds);
        width = labelBounds.width();
        height = labelBounds.height();
        base = centerX * 2 - (STROKE_WIDTH * 3 / 2 + 2 * outerCircleOffset + width * 2/ 3);
        labelBackground.set(base, centerY - height / 2,base + width * 4 / 3, centerY + height / 2);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[6],  base + width /12, centerY + height / 2, labelsPaint);

        //draw black circles between arcs
        canvas.drawCircle(centerX, centerY, centerX - outerCircleOffset / 2, radarCirclesPaint);
        canvas.drawCircle(centerX, centerY, centerX - outerCircleOffset * 3 / 2 - STROKE_WIDTH, radarCirclesPaint);
        canvas.drawCircle(centerX, centerY, centerX - outerCircleOffset * 5 / 2 - STROKE_WIDTH * 2, radarCirclesPaint);

        //bottom labels
        labelsPaint.getTextBounds(labels[5], 0, labels[5].length(), labelBounds);
        height = labelBounds.height();
        width = labelBounds.width();
        base =  centerX * 2 - (outerCircleOffset + STROKE_WIDTH / 2 + height * 2 / 3);
        labelBackground.set(centerX - width / 2, base,centerX + width / 2, base + height * 4 / 3);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[5],  centerX - labelBounds.width() / 2
                , base + height * 7 / 6, labelsPaint);

        labelsPaint.getTextBounds(labels[4], 0, labels[4].length(), labelBounds);
        height = labelBounds.height();
        width = labelBounds.width();
        base = centerX * 2 - (outerCircleOffset * 2 + STROKE_WIDTH *  3 / 2 + height * 2 / 3);
        labelBackground.set(centerX - width / 2, base,centerX + width / 2, base + height * 4 / 3);
        canvas.drawRect(labelBackground, labelsBackgroundPaint);
        canvas.drawText(labels[4],  centerX - labelBounds.width() / 2
                , base + height * 7 / 6, labelsPaint);


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