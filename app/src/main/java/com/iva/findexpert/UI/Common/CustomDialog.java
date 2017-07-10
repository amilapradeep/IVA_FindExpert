package com.iva.findexpert.UI.Common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iva.findexpert.R;

/**
 * Created by jayan on 4/24/2015.
 */
public class CustomDialog extends Dialog {

    public Button OKButton;
    public Button CancelButton;

    public CustomDialog(final Context mContext)
    {
        super(mContext, R.style.CustomDialogTheme);
        this.setContentView(R.layout.custom_dialog);
        this.setCancelable(false);
        OKButton = (Button) findViewById(R.id.OKButton);
        CancelButton = (Button) findViewById(R.id.CancelButton);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.dialog_background));
        //getWindow().setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.background_white)));
    }

    public CustomDialog(final Context mContext, int LayoutResource)
    {
        super(mContext);
        this.setContentView(LayoutResource);
        this.setCancelable(false);
        OKButton = (Button) findViewById(R.id.OKButton);
        CancelButton = (Button) findViewById(R.id.CancelButton);
        getWindow().setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.dialog_background));
    }

    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        TextView msgView = (TextView) findViewById(R.id.TitleLable);
        msgView.setText(title);
    }

    public void setMessage(CharSequence message)
    {
        TextView msgView = (TextView) findViewById(R.id.MessageLabel);
        msgView.setText(message);
    }


}
