package com.angelkjoseski.blebeacon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationHelper {
    private static final String CHANNEL_ID = "BLE";
    private static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private static NotificationManager notificationManager =
            (NotificationManager) App.getsInstance().getSystemService(Context.NOTIFICATION_SERVICE);

    public static void showNotification(String text, int id) {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Beacon scanning channel");
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(App.getsInstance(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round) // notification icon
                .setContentTitle("Gizmo Beacon") // title for notification
                .setContentText(text + " - " + sSimpleDateFormat.format(new Date())); // message for notification

        Intent intent = new Intent(App.getsInstance(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(App.getsInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        notificationManager.notify(id, mBuilder.build());
    }
}
