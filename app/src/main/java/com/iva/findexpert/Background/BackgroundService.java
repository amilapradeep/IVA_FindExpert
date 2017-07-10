package com.iva.findexpert.Background;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.iva.findexpert.UI.Helpers.NotificationsHelper;
import com.iva.findexpert.Common.Constant;
import com.iva.findexpert.Utility.Network;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    private Timer mTimer = null;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try
        {
            /*Notification notification = new NotificationsHelper(
                    getApplicationContext()).createNotification("Service", "Service running in background now");
            if(notification != null) {
                startForeground(777, notification);
                // schedule task
                mTimer.scheduleAtFixedRate(new SyncTimerTask(), 0, Constant.SERVICE_NOTIFY_INTERVAL);
            }*/

            mTimer.scheduleAtFixedRate(new SyncTimerTask(), 0, Constant.SERVICE_NOTIFY_INTERVAL);
        }
        catch (Exception e)
        {
            Log.e("BGSyncOnStartError", e.getMessage());
        }

        return START_STICKY;
    }

    private class SyncTimerTask extends TimerTask
    {
        @Override
        public void run() {
            notification();
        }
    }

    public void notification() {

        Intent intent = new Intent(Constant.Notification.REFRESH_MESSAGE_COUNT);
        if(Network.IsConnectedToInternet(this))
            sendBroadcast(intent);

        NotificationsHelper notificationsHelper = new NotificationsHelper(this);
        notificationsHelper.getNotifications();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
