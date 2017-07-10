package com.iva.findexpert.UI.Helpers;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.iva.findexpert.R;

/**
 * Created by LENOVO on 12/2/2016.
 */

public class FragmentHelper {
    public static void openFragement(android.app.Fragment fragment, Activity activity)
    {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commit();
        fm.executePendingTransactions();
    }
}
