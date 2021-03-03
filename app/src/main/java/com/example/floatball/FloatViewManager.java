package com.example.floatball;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.floatball.view.FloatCircleView;
import com.example.floatball.view.FloatMenuView;

import java.lang.reflect.Field;

/**
 * 悬浮球管理类
 */
public class FloatViewManager {
    private static FloatViewManager instance;
    private Context context;
    private WindowManager wm;
    private FloatCircleView circleView;
    private FloatMenuView menuView;
    private WindowManager.LayoutParams params;
    private float startX;
    private float startY;
    private float x0;
    private float y0;
    private View.OnTouchListener circleViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            x0 = event.getRawX();
            y0 = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX();
                    float y = event.getRawY();
                    float dx = x - startX;
                    float dy = y - startY;
                    params.x += dx;
                    params.y += dy;
                    wm.updateViewLayout(circleView, params);
                    startX = x;
                    startY = y;
                    circleView.setDragState(true);
                    break;
                case MotionEvent.ACTION_UP:
                    float lastX = event.getRawX();
                    if (lastX > getScreenWidth()/2) {
                        params.x = getScreenWidth() - circleView.width;
                    } else {
                        params.x = 0;
                    }
                    wm.updateViewLayout(circleView, params);
                    circleView.setDragState(false);
                    if (Math.abs(lastX - x0) > 6) {
                        return false;
                    } else {
                        return true;
                    }
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 获取屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕高度
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getScreenHeight() {
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size.y;
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusHeight() {
        Class<?> c = null;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    private  FloatViewManager(Context context) {
        this.context = context;
        // 通过WindowManager来操控浮窗体的显示和隐藏以及位置的改变
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        circleView = new FloatCircleView(context);
        menuView = new FloatMenuView(context);
        circleView.setOnTouchListener(circleViewTouchListener);
        circleView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                wm.removeView(circleView);
                showFloatMenuView();
                menuView.startAnimation();
            }
        });
    }

    /**
     * 单例
     * @param context
     * @return
     */
    public static FloatViewManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FloatViewManager.class) {
                if (instance == null) {
                    instance = new FloatViewManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 显示菜单在窗口上
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showFloatMenuView() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = getScreenWidth();
        params.height = getScreenHeight() - getStatusHeight();
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.RGBA_8888;
        wm.addView(menuView, params);
    }

    /**
     * 显示浮窗小球到窗口上
     */
    public void showFloatCircleView() {
        params = new WindowManager.LayoutParams();
        params.width = circleView.width;
        params.height = circleView.height;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.RGBA_8888;
        wm.addView(circleView, params);
    }

    /**
     * 隐藏悬浮菜单，显示悬浮按钮
     */
    public void hideFloatView() {
        wm.removeView(menuView);
        showFloatCircleView();
    }
}
