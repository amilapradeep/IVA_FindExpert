package com.iva.findexpert.UI.Helpers.Swipe;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by LENOVO on 7/2/2017.
 */

public class ActivitySwipeDetector implements View.OnTouchListener {

    private ISwipeHelper activity;
    private float threshold = 0;
    private final float THRESHOLD_MDPI = 60;

    public ActivitySwipeDetector(Context context, ISwipeHelper activity){
        this.activity = activity;
        this.threshold = THRESHOLD_MDPI * context.getResources().getDisplayMetrics().density;
    }

    public void onRightToLeftSwipe(View v){
        activity.onRightToLeft(v);
    }

    public void onLeftToRightSwipe(View v){
        activity.onLeftToRight(v);
    }

    public void onDownSwipe(View v){}

    public void onUpSwipe(View v){}

    public boolean onTouch(View v, MotionEvent event) {
        int e = event.getAction();
        float deltaX = 0;
        float deltaY = 0;

        switch(e){
            case MotionEvent.ACTION_DOWN: {
                TouchPoint.downX = event.getX();
                TouchPoint.downY = event.getY();
                return true;
            }

            case MotionEvent.ACTION_UP:

                deltaX = TouchPoint.downX - event.getX();
                deltaY = TouchPoint.downY - event.getY();

                // swipe horizontal?
                if (Math.abs(deltaX) > threshold) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe(v);
                    } else {
                        this.onRightToLeftSwipe(v);
                    }
                    return true;
                }

                // swipe vertical?
                if (Math.abs(deltaY) > threshold) {
                    // top or down
                    if (deltaY < 0) {
                        onDownSwipe(v);
                    } else {
                        onUpSwipe(v);
                    }
                    return true;
                }

                if (Math.abs(deltaX) < threshold && Math.abs(deltaY) < threshold){
                    v.performClick();
                }
                return true;

        }
        return false;
    }

}
