package com.iva.findexpert.UI.Common.Fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.iva.findexpert.R;
import com.iva.findexpert.UI.Common.CustomDialog;
import com.iva.findexpert.UI.Common.CustomProgress;
import com.iva.findexpert.UI.Helpers.NotificationsHelper;

/**
 * Created by LENOVO on 10/23/2016.
 */

public class BaseFragment extends Fragment implements OnFragmentBackPressEventHandler {

    private View currentView;
    private Dialog progressDialog;
    private  Context fContext;

    public void setCurrentView(View view)
    {
        currentView = view;
    }

    public View getCurrentView()
    {
        return currentView;
    }

    public void openFragement(Fragment fragment, boolean addToBackStack)
    {
        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(R.anim.trans_left_in,R.anim.trans_right_out);
        ft.replace(R.id.content_frame, fragment);
        if(addToBackStack)
            ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
        fm.executePendingTransactions();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fContext = context;

        NotificationsHelper.cancelAllNotification(context);
    }

    public Context getContext()
    {
        return fContext;
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, int[] grantResults){};
}
