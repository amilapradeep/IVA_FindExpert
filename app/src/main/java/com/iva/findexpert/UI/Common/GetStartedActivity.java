package com.iva.findexpert.UI.Common;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.iva.findexpert.R;
import com.iva.findexpert.UI.Helpers.Swipe.ActivitySwipeDetector;
import com.iva.findexpert.UI.Helpers.Swipe.ISwipeHelper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GetStartedActivity extends BaseActivity implements ISwipeHelper{

    private ViewFlipper vf;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        setViewFlipper();
        setSwipe();
    }

    private void setViewFlipper()
    {
        vf = ((ViewFlipper) findViewById(R.id.viewFlipper));
        final Button next = ((Button) findViewById(R.id.nextButton));
        final Button prev = ((Button) findViewById(R.id.previousButton));
        final Button skip = ((Button) findViewById(R.id.skipButton));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = index + 1;
                if(index >= vf.getChildCount()-1) {
                    index = vf.getChildCount() - 1;
                    next.setVisibility(View.GONE);
                    skip.setText("Exit");
                }
                else
                    skip.setText("Skip");
                if(index > 0)
                    prev.setVisibility(View.VISIBLE);
                vf.setInAnimation(GetStartedActivity.this, R.anim.trans_left_in);
                vf.setOutAnimation(GetStartedActivity.this, R.anim.trans_left_out);
                vf.setDisplayedChild(index);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = index - 1;
                if(index < 0)
                    index = 0;
                if(index <= 0)
                    prev.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
                skip.setText("Skip");
                vf.setInAnimation(GetStartedActivity.this, R.anim.trans_right_in);
                vf.setOutAnimation(GetStartedActivity.this, R.anim.trans_right_out);
                vf.setDisplayedChild(index);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setSwipe()
    {
        RelativeLayout root = (RelativeLayout) findViewById(R.id.rootView);
        root.setOnTouchListener(new ActivitySwipeDetector(getApplicationContext(), this));
    }

    public void onLeftToRight(View v)
    {
        if (vf.getDisplayedChild() == 0)
            return;

        findViewById(R.id.previousButton).performClick();
        SetPageNumber();
    }

    public void onRightToLeft(View v)
    {
        if (vf.getDisplayedChild() == vf.getChildCount() - 1)
            return;
        findViewById(R.id.nextButton).performClick();
        SetPageNumber();
    }

    private void SetPageNumber()
    {

    }





}
