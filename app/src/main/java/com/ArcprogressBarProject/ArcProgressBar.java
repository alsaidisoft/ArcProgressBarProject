package com.ArcprogressBarProject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

public class ArcProgressBar extends View {

    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint pointerPaint;
    private Paint tickPaint;
    private RectF rectF;
    private Path pointerPath;
    private float strokeWidth;
    private int progress = 0;
    private int min = 0;
    private int max = 100;
    private int[] gradientColors;
    private float[] colorPositions;


    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgressBar, 0, 0);

        try {
            //define below variables in the attrs file in values folder
            strokeWidth = typedArray.getDimension(R.styleable.ArcProgressBar_strokeWidth, 20);
            int backgroundColor = typedArray.getColor(R.styleable.ArcProgressBar_backgroundColor, 0xffd3d3d3);
            int pointerColor = typedArray.getColor(R.styleable.ArcProgressBar_pointerColor, 0xff000000);
            int startColor = typedArray.getColor(R.styleable.ArcProgressBar_startColor, 0xff0000);
            int centerColor = typedArray.getColor(R.styleable.ArcProgressBar_centerColor, 0x00ff00);
            int endColor = typedArray.getColor(R.styleable.ArcProgressBar_endColor, 0x0000ff);
            //define three color to show them in the ArcProgressBar
            gradientColors = new int[]{startColor, centerColor, endColor};
            //control percentage of the colors
            colorPositions = new float[]{0.0f, 0.3f, 0.5f};

            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setColor(backgroundColor);
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setStrokeWidth(strokeWidth);

            foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            foregroundPaint.setStyle(Paint.Style.STROKE);
            foregroundPaint.setStrokeWidth(strokeWidth);

            pointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            pointerPaint.setColor(pointerColor);
            pointerPaint.setStyle(Paint.Style.FILL);

            tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            tickPaint.setColor(0xff000000);
            tickPaint.setStrokeWidth(4);

            rectF = new RectF();
            pointerPath = new Path();
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectF.set(strokeWidth / 2, strokeWidth / 2, getWidth() - strokeWidth / 2, getHeight() - strokeWidth / 2);

        canvas.drawArc(rectF, 135, 270, false, backgroundPaint);

        float angle = 270 * progress / (float) max;

        SweepGradient sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, gradientColors, colorPositions);

        Matrix matrix = new Matrix();
        matrix.setRotate(135, getWidth() / 2, getHeight() / 2);
        sweepGradient.setLocalMatrix(matrix);

        foregroundPaint.setShader(sweepGradient);

        canvas.drawArc(rectF, 135, angle, false, foregroundPaint);

        drawTicks(canvas);
        drawPointer(canvas, 135 + angle);
    }

    private void drawTicks(Canvas canvas) {
        int tickCount = 10;
        float radius = (getWidth() - strokeWidth) / 2;
        float tickLength = 20;
        float angleStep = 270f / tickCount;

        for (int i = 0; i <= tickCount; i++) {
            float angle = 135 + i * angleStep;
            double angleRad = Math.toRadians(angle);

            float startX = (float) (getWidth() / 2 + radius * Math.cos(angleRad));
            float startY = (float) (getHeight() / 2 + radius * Math.sin(angleRad));
            float stopX = (float) (getWidth() / 2 + (radius - tickLength) * Math.cos(angleRad));
            float stopY = (float) (getHeight() / 2 + (radius - tickLength) * Math.sin(angleRad));

            canvas.drawLine(startX, startY, stopX, stopY, tickPaint);
        }
    }

    private void drawPointer(Canvas canvas, float angle) {
        float radius = (getWidth() - strokeWidth) / 2;
        float pointerRadius = radius - 40;

        double angleRad = Math.toRadians(angle);

        float x = (float) (getWidth() / 2 + pointerRadius * Math.cos(angleRad));
        float y = (float) (getHeight() / 2 + pointerRadius * Math.sin(angleRad));

        pointerPath.reset();
        pointerPath.moveTo(getWidth() / 2, getHeight() / 2);
        pointerPath.lineTo(x - 20, y - 20);
        pointerPath.lineTo(x + 20, y - 20);
        pointerPath.close();

        canvas.drawPath(pointerPath, pointerPaint);
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress, max);
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    // Add a property for progress to animate
    public static final Property<ArcProgressBar, Integer> PROGRESS_PROPERTY = new Property<ArcProgressBar, Integer>(Integer.class, "progress") {
        @Override
        public Integer get(ArcProgressBar object) {
            return object.getProgress();
        }

        @Override
        public void set(ArcProgressBar object, Integer value) {
            object.setProgress(value);
        }
    };

    // Method to set progress with animation
    public void setProgressWithAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, PROGRESS_PROPERTY, newProgress);
        animator.setDuration(1000); // 1 second
        animator.start();
    }
}
