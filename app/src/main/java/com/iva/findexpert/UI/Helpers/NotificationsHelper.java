package com.iva.findexpert.UI.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.DomainModel.User;
import com.iva.findexpert.R;
import com.iva.findexpert.Service.UserService;
import com.iva.findexpert.UI.Agent.AgentHomeActivity;
import com.iva.findexpert.UI.Buyer.BuyerHomeActivity;
import com.iva.findexpert.UI.Helpers.HttpClientHelper;
import com.iva.findexpert.UI.Helpers.IHttpResponse;
import com.iva.findexpert.Utility.Network;
import com.iva.findexpert.ViewModel.NotificationViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jayan on 13/01/2017.
 */

public class NotificationsHelper {

    private Context context;
    private User user;
    private int numMessages;

    public NotificationsHelper(Context context)
    {
        this.context = context;
    }

    public void getNotifications()
    {
        user = new UserService(context).GetCurrentUser();
        if(user == null || !user.IsAuthenticated)
            return;
        if(!Network.IsConnectedToInternet(context))
            return;

        String url = Constant.BASE_URL + Constant.CONTROLLER_NOTIFICATION + Constant.SERVICE_GET_NOTIFICATIONS;
        Map<String, String> params = new HashMap<>();
        params.put("UserId", String.valueOf(user.Id));

        Map<String,String> headerParams = new HashMap<>();
        headerParams.put("Authorization", "bearer " + user.Token);

        new HttpClientHelper().getResponseStringURLEncoded(context, url, Request.Method.GET, params, headerParams, new IHttpResponse() {
            @Override
            public void onSuccess(String result) {
                if(result != null)
                {
                    Gson gson = new Gson();
                    List<NotificationViewModel> notifications = Arrays.asList(gson.fromJson(result, NotificationViewModel[].class ));
                    if(notifications != null)
                    {
                        for(int i = 0; i<notifications.size(); i++)
                        {
                            NotificationViewModel n = notifications.get(i);
                            Notification notification = createNotification(n, notifications);

                            if(notification != null) {
                                //notification.number = getNotificationCountForType(notifications, n.Type);
                                displayNotification(notification, n.Type);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(int error) { Log.e("Error", String.valueOf(error));}

            @Override
            public void onNoInternetConnection(){ }
        });
    }

    public Notification createNotification(NotificationViewModel model, List<NotificationViewModel> notifications) {
        // Prepare intent which is triggered if the
        // notification is selected
        user = new UserService(context).GetCurrentUser();
        if(user == null)
            return null;

        Intent intent = new Intent(context, BuyerHomeActivity.class);
        if(user.Type == Constant.UserType.SELLER)
            intent = new Intent(context, AgentHomeActivity.class);
        Bundle args = new Bundle();
        args.putInt(Constant.NotificationType.NAME, model.Type);
        args.putLong(Constant.NotificationType.ID, model.RecordId);
        intent.putExtras(args);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        int count = getNotificationCountForType(notifications, model.Type);

        // Build notification
        // Actions are just fake
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(model.Title)
                .setContentText(model.Text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setNumber(count)
                .build();

        return notification;
    }

    private void displayNotification(Notification notification, int id)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        /*Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);*/
        notification.defaults|= Notification.DEFAULT_SOUND;
        notification.defaults|= Notification.DEFAULT_LIGHTS;
        notification.defaults|= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(id, notification);
    }

    protected void displayNotification() {
        //Log.i("Start", "notification");

   /* Invoking the default notification service */
        NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setContentTitle("New Message");
        mBuilder.setContentText("You've received new message.");
        mBuilder.setTicker("New Message Alert!");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);

   /* Increase notification number every time a new notification arrives */
        mBuilder.setNumber(++numMessages);

   /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[6];
        events[0] = new String("This is first line....");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Big Title Details:");

        // Moves events into the big view
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

   /* Creates an explicit intent for an Activity in your app */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        Intent intent = new Intent(context, BuyerHomeActivity.class);
        if(user.Type == Constant.UserType.SELLER)
            intent = new Intent(context, AgentHomeActivity.class);

        stackBuilder.addParentStack(BuyerHomeActivity.class);

   /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
   /* notificationID allows you to update the notification later on. */
        notificationManager.notify(0, mBuilder.build());
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public static void cancelAllNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

    private int getNotificationCountForType(List<NotificationViewModel> notifications, int Type)
    {
        int count = 0;
        if(notifications != null)
        {
            for(int i = 0; i < notifications.size(); i++)
            {
                NotificationViewModel n = notifications.get(i);
                if(n.Type == Type)
                    count = count + 1;
            }
        }

        return count;
    }

}
