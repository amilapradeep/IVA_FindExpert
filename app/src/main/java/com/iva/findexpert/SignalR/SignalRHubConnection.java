package com.iva.findexpert.SignalR;

/* Class to create a SignalR Connection */
import android.content.Context;
import com.google.gson.Gson;
import com.iva.findexpert.Utility.Network;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;


public class SignalRHubConnection {
    // HubConnection
    public static HubConnection mHubConnection;
    public static HubProxy mHubProxy;
    private static Context context;
    public static String mConnectionID;
    public SignalRHubConnection(Context context) {
        this.context = context;
    }
    //
     /*
    This function try to connect with chat hub and return connection ID.
     */
    public static void startSignalR() {

        if (Network.IsConnectedToInternet(context) == false) {
            //AppUtility.showAlertDialog(context, context.getString(R.string.app_name), context.getString(R.string.connection_error));
        } else {
            try {
                Platform.loadPlatformComponent(new AndroidPlatformComponent());
                Credentials credentials = new Credentials() {
                    @Override
                    public void prepareRequest(Request request) {
                        //AppPreferences appPrefs = AppPreferences.getInstance(context);
                        //request.addHeader("UserName",  appPrefs.getString(PrefConstants.USER_NAME));
                        request.addHeader("UserName",  "jay");
                    }
                };
                mHubConnection = new HubConnection("SignalR Hub Url");
                mHubConnection.setCredentials(credentials);
                mHubProxy = mHubConnection.createHubProxy("SignalR Hub");
                ClientTransport clientTransport = new   ServerSentEventsTransport(mHubConnection.getLogger());
                SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);
                signalRFuture.get();
                //set connection id
                mConnectionID = mHubConnection.getConnectionId();
                // To get onLine user list
                mHubProxy.on("onGetOnlineContacts",
                        new SubscriptionHandler1<Object>() {
                            @Override
                            public void run(final Object msg) {
                                try {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(msg);
                                    JSONObject responseObject = new JSONObject(json.toString());
                                    JSONArray jsonArray = responseObject.getJSONArray("messageRecipients");
                                    int sizeOfList = jsonArray.length();
                                    for (int i = 0; i < sizeOfList; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        JSONArray onLineUserList = jsonObject.getJSONArray("TwingleChatGroupIds");
                                        int onLineUserCount = onLineUserList.length();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        , Object.class);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
