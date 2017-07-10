package com.iva.findexpert.UI.Helpers;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jayan on 09/05/2017.
 */

public class RequestQueueFactory {

    static RequestQueue requestQueue;

    public static RequestQueue getSingleton(Context context)
    {
        if(requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);

        return requestQueue;
    }
}
