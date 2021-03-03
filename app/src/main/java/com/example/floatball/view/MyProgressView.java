package com.example.floatball.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 自定义加速球
 */
public class MyProgressView extends View {

    private int width = 200;
    private int height = 200;
    private Paint circlePaint;
    private Paint progressPaint;
    private Paint textPaint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Path path = new Path();
    private int progress = 50;
    private int maxProgress = 100;
    private int currentProgress = 0;
    private int count = 50;
    private boolean isSingleTap = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

        }
    };
    public MyProgressView(Context context) {
        super(context);
        init();
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmapCanvas.drawCircle(width/2, height/2, width/2, circlePaint);
        path.reset();
        float y = (1 - (float)currentProgress/maxProgress) * height;
        Log.e("lwj", y +"----");
        path.moveTo(width, y);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, y);
        // 根据手势不同绘制不同的贝塞尔曲线
        if (!isSingleTap) {
            float d = (1-((float) currentProgress/progress))*10;
            for(int i=0; i<5; i++) {
                path.rQuadTo(10, -d, 20, 0);
                path.rQuadTo(10, d, 20, 0);
            }
        } else {
            float d = (float)count/50*10;
            if (count%2 == 0) {
                for (int i=0; i<5; i++) {
                    path.rQuadTo(20, -d, 40, 0);
                    path.rQuadTo(20, d, 40, 0);
                }
            } else {
                for (int i=0; i<5; i++) {
                    path.rQuadTo(20, d, 40, 0);
                    path.rQuadTo(20, -d, 40, 0);
                }
            }
        }

        path.close();
        bitmapCanvas.drawPath(path, progressPaint);
        String text = (int)(((float)currentProgress/maxProgress)*100) + "%";
        float textWidth = textPaint.measureText(text);
        float x = width/2 - textWidth/2;
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float baseLine = height/2 - (fontMetrics.descent + fontMetrics.ascent)/2;
        bitmapCanvas.drawText(text, x, baseLine, textPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    /**
     * 初始化操作
     */
    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#00DB00"));

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.parseColor("#A6FFA6"));
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        // 手势
        GestureDetector detector = new GestureDetector(new MyGestureDetectorListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
        setClickable(true);

    }

    /**
     * 开启双击动画
     */
    private void startDoubleTapAnimation() {
        handler.postDelayed(doubleTapRunnable, 50);
    }

    /**
     * 开启单击动画
     */
    private void startSingleTapAnimation() {
        handler.postDelayed(singleTapRunnable, 200);
    }

    private SingleTapRunnable singleTapRunnable = new SingleTapRunnable();
    class SingleTapRunnable implements Runnable {
        @Override
        public void run() {
            count --;
            if (count >= 0) {
                invalidate();
                handler.postDelayed(singleTapRunnable, 200);
            } else {
                handler.removeCallbacks(singleTapRunnable);
                count = 50;
            }
        }
    }

    private DoubleTapRunnable doubleTapRunnable = new DoubleTapRunnable();
    class DoubleTapRunnable implements Runnable {
        @Override
        public void run() {
            currentProgress ++;
            if (currentProgress <= progress) {
                invalidate();
                handler.postDelayed(doubleTapRunnable, 50);
            } else {
                handler.removeCallbacks(doubleTapRunnable);
                currentProgress = 0;
            }
        }
    }

    /**
     * 手势监听器
     */
    class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isSingleTap = false;
            startDoubleTapAnimation();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            isSingleTap = true;
            currentProgress = progress;
            startSingleTapAnimation();
            return super.onSingleTapConfirmed(e);
        }
    }



}
