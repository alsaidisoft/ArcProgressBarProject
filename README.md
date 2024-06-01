# ArcProgressBarProject

This project demonstrates how to create a customizable Arc Progress Bar in Android, with gradient colors and smooth animations.
## Demo

![Arc Progress Bar Demo](https://github.com/saidalsaidi/ArcProgressBarProject/blob/master/screenshot.gif)

## Step-by-Step Instructions

### 1. Create the `ArcProgressBar` class

Create a new Java file named `ArcProgressBar.java` and add the following code:

```java
package com.ArcprogressBarProject;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
            // Initialize attributes
            strokeWidth = typedArray.getDimension(R.styleable.ArcProgressBar_strokeWidth, 20);
            int backgroundColor = typedArray.getColor(R.styleable.ArcProgressBar_backgroundColor, 0xffd3d3d3);
            int pointerColor = typedArray.getColor(R.styleable.ArcProgressBar_pointerColor, 0xff000000);
            int startColor = typedArray.getColor(R.styleable.ArcProgressBar_startColor, 0xff0000);
            int centerColor = typedArray.getColor(R.styleable.ArcProgressBar_centerColor, 0x00ff00);
            int endColor = typedArray.getColor(R.styleable.ArcProgressBar_endColor, 0x0000ff);

            // Define gradient colors and their positions
            gradientColors = new int[]{startColor, centerColor, endColor};
            colorPositions = new float[]{0.0f, 0.3f, 1.0f};

            // Initialize paints
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

        // Define the rectangle bounds for the arc
        rectF.set(strokeWidth / 2, strokeWidth / 2, getWidth() - strokeWidth / 2, getHeight() - strokeWidth / 2);

        // Draw the background arc
        canvas.drawArc(rectF, 135, 270, false, backgroundPaint);

        // Calculate the sweep angle based on the progress
        float angle = 270 * progress / (float) max;

        // Create the SweepGradient with the defined colors and positions
        SweepGradient sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, gradientColors, colorPositions);

        // Rotate the gradient to align with the arc
        Matrix matrix = new Matrix();
        matrix.setRotate(135, getWidth() / 2, getHeight() / 2);
        sweepGradient.setLocalMatrix(matrix);

        // Apply the gradient to the foreground paint
        foregroundPaint.setShader(sweepGradient);

        // Draw the foreground arc
        canvas.drawArc(rectF, 135, angle, false, foregroundPaint);

        // Draw the ticks and the pointer
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

    // Property for animating progress
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
```
### 2. Define Custom Attributes in res/values/attrs.xml
Create an attrs.xml file in the res/values directory and add the following custom attributes:
```xml
<resources>
    <declare-styleable name="ArcProgressBar">
        <attr name="strokeWidth" format="dimension" />
        <attr name="backgroundColor" format="color" />
        <attr name="pointerColor" format="color" />
        <attr name="startColor" format="color" />
        <attr name="centerColor" format="color" />
        <attr name="endColor" format="color" />
    </declare-styleable>
</resources>
```
### 3. Use the ArcProgressBar in Layout XML
Add the ArcProgressBar to your layout XML file, specifying the custom attributes:
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:background="#eee"
    tools:context=".MainActivity">
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="5dp">

        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">



            <com.ArcprogressBarProject.ArcProgressBar
                android:id="@+id/progressBar"
                android:layout_width="330dp"
                android:layout_height="330dp"
                android:layout_centerInParent="true"
                app:strokeWidth="20dp"
                app:backgroundColor="#7B7B7B"
                app:foregroundColor="#47934C"
                app:startColor="#D7CB5B"
                app:centerColor="#359939"
                app:endColor="#DA613B"
                app:pointerColor="#000"
                />


            <TextView
                android:id="@+id/txtProgress"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_alignBottom="@+id/progressBar"
                android:textAlignment="center"
                android:layout_centerInParent="true"
                android:textSize="20sp"
                android:text="speed"
                android:textAppearance="?android:attr/textAppearanceSmall" />



            <Button
                android:layout_width="match_parent"
                android:layout_height="60dp"
                android:layout_marginStart="16dp"
                android:layout_marginTop="100dp"
                android:layout_marginEnd="16dp"
                android:paddingStart="12dp"
                android:paddingEnd="12dp"
                android:layout_below="@+id/progressBar"
                android:background="#3F51B5"
                android:textColor="@color/white"
                android:id="@+id/calculate_speed"
                android:text="click me"/>
        </RelativeLayout>

    </ScrollView>

</RelativeLayout>
```
### 4. Control the Progress in Your Activity
In your activity, you can control the progress and animate it as follows:
```java
ArcProgressBar arcProgressBar = findViewById(R.id.arcProgressBar);
arcProgressBar.setProgressWithAnimation(75); // Set progress to 75 with animation
```
This setup provides a fully customizable arc progress bar with gradient colors and smooth animations.
