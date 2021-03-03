package com.example.floatball.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.floatball.R;

/**
 * 自定义悬浮球
 */
public class FloatCircleView extends View {
    // 宽度
    public int width = 150;
    // 高度
    public int height = 150;
    // 绘制圆的画笔
    private Paint circlePaint;
    // 绘制文字的画笔
    private Paint textPaint;
    // 绘制图片的画笔
    private Paint imgPaint;
    private String text = "50%";
    // 是否拖动的标记
    private boolean drag = false;
    private Bitmap bitmap;

    public FloatCircleView(Context context) {
        super(context);
        initPaints();
    }

    public FloatCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public FloatCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (drag) {
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            imgPaint.setShader(bitmapShader);
            canvas.drawCircle(width / 2, height / 2, width / 2, imgPaint);
        } else {
            canvas.drawCircle(width/2, height/2, width/2, circlePaint);
            float textWidth = textPaint.measureText(text);
            float x = width/2 - textWidth/2;
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float dy = (fontMetrics.descent + fontMetrics.ascent)/2;
            float y = height/2 - dy;
            canvas.drawText(text, x, y, textPaint);
        }

    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.GRAY);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        imgPaint = new Paint();
        imgPaint.setAntiAlias(true);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.c);
        bitmap = Bitmap.createScaledBitmap(b, width, height, true);

    }

    /**
     * 设置拖动状态
     * @param state
     */
    public void setDragState(boolean state) {
        drag = state;
        invalidate();
    }
}
