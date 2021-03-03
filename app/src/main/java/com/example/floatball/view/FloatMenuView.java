package com.example.floatball.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.example.floatball.FloatViewManager;
import com.example.floatball.R;

/**
 * 自定义悬浮菜单
 */
public class FloatMenuView extends LinearLayout {

    private LinearLayout ll;
    private TranslateAnimation animation;
    public FloatMenuView(Context context) {
        super(context);
        View view = View.inflate(getContext(), R.layout.float_menu_view, null);
        ll = view.findViewById(R.id.ll);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        ll.setAnimation(animation);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatViewManager manager = FloatViewManager.getInstance(getContext());
                manager.hideFloatView();
                return false;
            }
        });
        addView(view);

    }

    public void startAnimation() {
        animation.start();
    }

}
