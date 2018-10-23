package com.angelkjoseski.blebeacon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationHelper {
    private static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void showNotification(String text, int id) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.getsInstance())
                .setSmallIcon(R.mipmap.ic_launcher_round) // notification icon
                .setContentTitle("Gizmo Beacon") // title for notification
                .setContentText(text + " - " + sSimpleDateFormat.format(new Date())); // message for notification

        Intent intent = new Intent(App.getsInstance(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(App.getsInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) App.getsInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }
}
