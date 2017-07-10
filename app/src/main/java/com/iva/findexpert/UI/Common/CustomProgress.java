package com.iva.findexpert.UI.Common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.iva.findexpert.R;

/**
 * Created by jayan on 23/11/2016.
 */

public class CustomProgress extends Dialog {

    public CustomProgress(final Context mContext)
    {
        super(mContext, R.style.CustomProgressTheme);
        this.setContentView(R.layout.custom_progress);
        this.setCancelable(false);
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.progress_background, null));
        //ImageView imgView = (ImageView) findViewById(R.id.imgProgress);
        //imgView.setBackgroundResource(R.drawable.progress_animation);
        //progressAnimation = (AnimationDrawable) imgView.getBackground();

        if (Build.VERSION.SDK_INT > 19) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    mContext.getResources().getColor(R.color.progress_bar), PorterDuff.Mode.MULTIPLY);
        }
        else {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    mContext.getResources().getColor(R.color.progress_bar), PorterDuff.Mode.SRC_IN);
        }

    }
}
