package com.weather.weather.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.weather.weather.R;

import java.util.Random;

/**
 * Custom view to display a constant, random animation of rain.
 * TODO could be enhanced to draw water drops falling instead randomly appearing, perhaps consider other ways to implementß
 */
public class WeatherAnimationView extends View {
    protected Bitmap[] waterDropBitmap = new Bitmap[5];
    protected Random random;
    protected Handler handler;
    protected Runnable runnable;

    public WeatherAnimationView(Context context) {
        super(context);
        init();
    }

    public WeatherAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeatherAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Bitmap waterDropFull = BitmapFactory.decodeResource(getResources(), R.drawable.water_drop);
        random = new Random();
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                WeatherAnimationView.this.invalidate();
            }
        };

        for (int i = 0; i < waterDropBitmap.length; i++) {
            waterDropBitmap[i] = Bitmap.createScaledBitmap(waterDropFull, 10 * (i + 1), 10 * (i + 1), false);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int numberOfDrops = random.nextInt(50 - 40) + 40;

        for (int i = 0; i < numberOfDrops; i++) {
            int dropIndex = random.nextInt(waterDropBitmap.length);
            int xCoordinate = random.nextInt(canvas.getWidth());
            int yCoordinate = random.nextInt(canvas.getHeight());
            canvas.drawBitmap(waterDropBitmap[dropIndex], xCoordinate, yCoordinate, null);
        }

        handler.postDelayed(runnable, 200);
    }
}
