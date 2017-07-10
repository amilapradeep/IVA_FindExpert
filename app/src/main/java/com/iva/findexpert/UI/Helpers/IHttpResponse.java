package com.iva.findexpert.UI.Helpers;

/**
 * Created by jayan on 24/11/2016.
 */

public interface IHttpResponse {

    String ResponseString = "";
    int StatusCode = 0;

    void onSuccess(String result);
    void onError(int error);
    void onNoInternetConnection();
}
