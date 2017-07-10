package com.iva.findexpert.UI.Helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.Utility.Network;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by LENOVO on 11/18/2016.
 */

public class HttpClientHelper {

    public HttpClientHelper(){ }

    public void getResponseString(final Context context,
                                    String url,
                                    int requestMethod,
                                    Map<String, String> params,
                                    final Map<String, String> headers,
                                    final IHttpResponse callback)
    {
        try
        {
            if(!Network.IsConnectedToInternet(context))
            {
                Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
                callback.onNoInternetConnection();
                return;
            }
            RequestQueue requestQueue = RequestQueueFactory.getSingleton(context.getApplicationContext());
            JSONObject jsonBody = new JSONObject();

            if(params !=  null)
            {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    jsonBody.put(entry.getKey(), entry.getValue());
                }
            }
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(requestMethod, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse response = error.networkResponse;
                            if(response != null && response.data != null){
                                switch(response.statusCode){
                                    case Constant.HttpStatus.UNAUTHORIZED:
                                        //Toast.makeText(context, "Error communicating the server(401)!", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        //Toast.makeText(context, "Error communicating the server!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                                callback.onError(response.statusCode);
                            }
                            else
                            {
                                //Toast.makeText(context, "Error communicating the server!", Toast.LENGTH_LONG).show();
                                callback.onError(0);
                            }

                            Log.e("VOLLEY", error.toString());

                        }
                    }
            )
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  headerParams = new HashMap<String, String>();
                    //headerParams.put("Content-Type", "application/json");
                    //headerParams.put("Authorization", "bearer  hecEjBsrNEjztCZ_FVbaA17YczCn46Req_PpI-iM53DLEkbjNPryXtkG7zv3svHPh4g5EogwpM9OGH1vF6bGf5taBHpG40gzRW7DURvuFpQKzvciipWtAHI57tq2l1PrOFJGmM-UCOA2Nq-d2-kyadFpCnDpq2wmnfouZO2h6_eGEfn3PwCIRt75A-bALoX9oexwAAa9ZYq_OslFeGObl1bVostMxjmb7BsT2uqc5Jqtc2NB9Rt140CRlC1k0Vq6ZU2X8glsyojBPk4T2NpC5IPAdKO04iaDJdrka5MIw_allbslSmHSrTStwYGWMgRbmi78af6kVMw-rRnDO8lu2tFhkTtw8uM7auALw1fzJhAAiewtaHBtBnbVTCWw0wYdBNQmv_XZIr82vV2VoN0dHBjYw7I5MYiIG6wArBe51mu21Os7m7ieqdGBU0QIkZUlUTPape1gYjPly6_ilNXWgvzm3Bc_IaqiaJDjCY72bFwnMfZqt4eAi7tXqQHvX3KFjuKiTksocjoIeSuVG0hgF9FdaIeYSBoxS0tdaaBAhjbVbyg10jsqNKsNXatNW8NF");
                    if(headers !=  null)
                    {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            headerParams.put(entry.getKey(), entry.getValue());
                        }
                    }
                    return headerParams;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(Constant.REQUEST_TIME_OUT),
                    0, //DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) );
            requestQueue.add(stringRequest);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void getResponseString(final Context context,
                                  String url,
                                  String JSONString,
                                  final Map<String, String> headers,
                                  final IHttpResponse callback)
    {
        try
        {
            if(!Network.IsConnectedToInternet(context))
            {
                //Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
                callback.onNoInternetConnection();
                return;
            }

            RequestQueue requestQueue = RequestQueueFactory.getSingleton(context.getApplicationContext());
            final String mRequestBody = JSONString;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse response = error.networkResponse;
                            if(response != null && response.data != null){
                                switch(response.statusCode){
                                    case Constant.HttpStatus.UNAUTHORIZED:
                                        //Toast.makeText(context, "Error communicating the server(401)!", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        //Toast.makeText(context, "Error communicating the server!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                                callback.onError(response.statusCode);
                            }
                            else
                            {
                                //Toast.makeText(context, "Error communicating the server!", Toast.LENGTH_LONG).show();
                                callback.onError(0);
                            }

                            Log.e("VOLLEY", error.toString());

                        }
                    }
            )
            {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  headerParams = new HashMap<String, String>();
                    //headerParams.put("Content-Type", "application/json");
                    //headerParams.put("Authorization", "bearer  hecEjBsrNEjztCZ_FVbaA17YczCn46Req_PpI-iM53DLEkbjNPryXtkG7zv3svHPh4g5EogwpM9OGH1vF6bGf5taBHpG40gzRW7DURvuFpQKzvciipWtAHI57tq2l1PrOFJGmM-UCOA2Nq-d2-kyadFpCnDpq2wmnfouZO2h6_eGEfn3PwCIRt75A-bALoX9oexwAAa9ZYq_OslFeGObl1bVostMxjmb7BsT2uqc5Jqtc2NB9Rt140CRlC1k0Vq6ZU2X8glsyojBPk4T2NpC5IPAdKO04iaDJdrka5MIw_allbslSmHSrTStwYGWMgRbmi78af6kVMw-rRnDO8lu2tFhkTtw8uM7auALw1fzJhAAiewtaHBtBnbVTCWw0wYdBNQmv_XZIr82vV2VoN0dHBjYw7I5MYiIG6wArBe51mu21Os7m7ieqdGBU0QIkZUlUTPape1gYjPly6_ilNXWgvzm3Bc_IaqiaJDjCY72bFwnMfZqt4eAi7tXqQHvX3KFjuKiTksocjoIeSuVG0hgF9FdaIeYSBoxS0tdaaBAhjbVbyg10jsqNKsNXatNW8NF");
                    if(headers !=  null)
                    {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            headerParams.put(entry.getKey(), entry.getValue());
                        }
                    }
                    return headerParams;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(Constant.REQUEST_TIME_OUT),
                    0, //DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) );
            requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void getResponseStringURLEncoded(final Context context,
                                            String url,
                                            int requestMethod,
                                            Map<String, String> params,
                                            final Map<String, String> headers,
                                            final IHttpResponse callback)
    {
        try
        {
            if(!Network.IsConnectedToInternet(context))
            {
                Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_LONG).show();
                callback.onNoInternetConnection();
                return;
            }

            RequestQueue requestQueue = RequestQueueFactory.getSingleton(context.getApplicationContext());
            String urlEncoded = "";
            int i = 0;

            if(params !=  null)
            {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if(i > 0)
                        urlEncoded = urlEncoded + "&";
                    urlEncoded = urlEncoded + entry.getKey() + "=" + entry.getValue();
                    i += 1;
                }
            }
            final String mRequestBody = urlEncoded;
            //if(requestMethod == Request.Method.GET)
            url = url + "?" + urlEncoded;

            StringRequest stringRequest = new StringRequest(requestMethod, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse response = error.networkResponse;
                            if(response != null && response.data != null){
                                switch(response.statusCode){
                                    case Constant.HttpStatus.UNAUTHORIZED:
                                        //Toast.makeText(context, "Error communicating the server(401)!", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        //Toast.makeText(context, "Error communicating the server!", Toast.LENGTH_LONG).show();
                                        break;
                                }
                                callback.onError(response.statusCode);
                            }
                            else
                            {
                                //Toast.makeText(context, "Error communicating the server!", Toast.LENGTH_LONG).show();
                                callback.onError(0);
                            }

                            Log.e("VOLLEY", error.toString());
                        }
                    }
            )
            {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  headerParams = new HashMap<String, String>();
                    if(headers !=  null)
                    {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            headerParams.put(entry.getKey(), entry.getValue());
                        }
                    }
                    return headerParams;
                }

            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(Constant.REQUEST_TIME_OUT),
                    0, //DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) );
            requestQueue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
