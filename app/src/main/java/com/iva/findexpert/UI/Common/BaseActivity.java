package com.iva.findexpert.UI.Common;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.iva.findexpert.R;
import com.iva.findexpert.Utility.Common;

/**
 * Created by LENOVO on 10/23/2016.
 */

public class BaseActivity extends AppCompatActivity {

    private Dialog progressDialog;

    protected void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    public Dialog showProgress(Context context)
    {
        progressDialog = new CustomProgress(context);
        progressDialog.show();
        return progressDialog;
    }

    public void hideProgress()
    {
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    public CustomDialog showAlert(Context context, String title, String message)
    {
        CustomDialog alert = new CustomDialog(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.show();
        return alert;
    }

    public void showAlert(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //Common.hideKeyboard(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Common.hideKeyboard(this);
    }
}
