package com.iva.findexpert.Utility;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by jayan on 20/12/2016.
 */

public class Common {

    public static boolean ValidEmail(String email, Context context)
    {
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(context,"Email required", Toast.LENGTH_SHORT).show();
            return false;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+(\\.+[a-z]+)?";
        if (!email.matches(emailPattern))
        {
            Toast.makeText(context,"Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean ValidName(String name)
    {
        String namePattern = "^[a-zA-Z0-9 ]*$";
        if (!name.matches(namePattern))
        {
            return false;
        }

        return true;
    }

    public static boolean ValidAddress(String name, Context context)
    {
        String namePattern = "^[a-zA-Z0-9 ,-]*$";
        if (!name.matches(namePattern))
        {
            Toast.makeText(context,"Invalid characters typed for the address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean ValidVehicleNumber(String name, Context context)
    {
        String namePattern = "^[a-zA-Z0-9- ]+";
        if (!name.matches(namePattern))
        {
            Toast.makeText(context,"Invalid characters typed for vehicle number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static int ConvertDPToPixels(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    /*public static String formatDecimal(Double value)
    {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat formatter = new DecimalFormat("###,###.##", symbols);
        return formatter.format(value);
    }
*/
    public static String formatDecimal(double number, boolean isAddMoneySymbol) {

        DecimalFormat nf = new DecimalFormat("###,###,###.00");
        // or this way: nf = new DecimalFormat("###,###,###,##0.00");

        String formatted = nf.format(number);
        if(!isAddMoneySymbol)
            return formatted;
        else
            return formatted + " Rs";
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("failed to encode", e);
        }
    }

}
