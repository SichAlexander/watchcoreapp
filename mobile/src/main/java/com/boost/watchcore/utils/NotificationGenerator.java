package com.boost.watchcore.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.boost.watchcore.R;
import com.boost.watchcore.ui.activity.MainActivity;


/**
 * Created by BruSD on 15.04.2015.
 */
public class NotificationGenerator {


    public NotificationGenerator(){

    }

    public static NotificationGenerator newInstance(){
        NotificationGenerator notificationGenerator = new NotificationGenerator();

        return notificationGenerator;
    }


    public void showNotification(Context mContextc, int quoteCount){
        Intent intent = new Intent(mContextc, MainActivity.class);
        intent.putExtra("FRAGMENT_KEY", 3);
        PendingIntent pIntent = PendingIntent.getActivity(mContextc, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String newQuoteTitle = mContextc.getResources().getString(R.string.notification_title);
        String msg = mContextc.getResources().getString(R.string.new_watch_faces)+ " "+ quoteCount;
        Notification.Builder n = new Notification.Builder(mContextc)
                .setContentTitle(newQuoteTitle)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) mContextc.getSystemService(mContextc.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < 16) {
            notificationManager.notify(0, n.getNotification());
        } else {
            notificationManager.notify(0, n.build());
        }


    }

}
