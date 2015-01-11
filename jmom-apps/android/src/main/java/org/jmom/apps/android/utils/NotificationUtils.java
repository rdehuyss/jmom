package org.jmom.apps.android.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public final class NotificationUtils {

    public static void sendNotification(Context context, int id, int icon, String tickerText, String contentTitle,
                                        String contentText) {
        PendingIntent intent = PendingIntent.getActivity(context, id, new Intent(), 0);
        Notification notification = new Notification.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(tickerText)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(intent)
                .getNotification();

        sendNotification(context, id, notification);
    }

    public static void sendNotification(Context context, int id, Notification notification) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }
}
