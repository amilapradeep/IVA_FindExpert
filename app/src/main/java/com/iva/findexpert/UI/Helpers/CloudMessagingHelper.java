package com.iva.findexpert.UI.Helpers;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.ViewModel.UserDeviceModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jayan on 19/01/2017.
 */

public class CloudMessagingHelper {

    private Context context;

    public CloudMessagingHelper(Context context)
    {
        this.context = context;
    }

    public String getDeviceId()
    {
        String serial =    Build.SERIAL;
        return serial + Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }

    public void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        final User user = new UserService(context).GetCurrentUser();
        if(user == null)
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_USER + Constant.SERVICE_USER_DEVICE;

        UserDeviceModel device = new UserDeviceModel();
        device.UserId = user.Id;
        device.DeviceId = new CloudMessagingHelper(context).getDeviceId();
        device.DeviceToken = token;
        String jsonObj = new Gson().toJson(device);

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token );

        new HttpClientHelper().getResponseString(context, url, jsonObj, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onNoInternetConnection(){ }
        });

    }
}
