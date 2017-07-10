package com.iva.findexpert.UI.CloudMessaging;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.iva.findexpert.UI.Helpers.CloudMessagingHelper;

/**
 * Created by jayan on 19/01/2017.
 */

public class IvaFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        new CloudMessagingHelper(getApplicationContext()).sendRegistrationToServer(refreshedToken);
    }


}
