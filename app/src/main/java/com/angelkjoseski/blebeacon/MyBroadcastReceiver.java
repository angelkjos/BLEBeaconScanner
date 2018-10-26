package com.angelkjoseski.blebeacon;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * BroadcastReceiver which will be called with the PendingIntent from Bluetooth beacon scanning.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    private static final IBeaconManager sManager = IBeaconManager.getInstance(new DefaultIBeaconManager());

    public static IBeaconManager getIBeaconManager() {
        return sManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<ScanResult> scanResults = intent.getParcelableArrayListExtra(
                BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT);
        int callbackType = intent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1);

        for (ScanResult scanResult : scanResults) {
            if (callbackType > -1 && (callbackType == ScanSettings.CALLBACK_TYPE_FIRST_MATCH) || callbackType == ScanSettings.CALLBACK_TYPE_MATCH_LOST) {
                boolean enter = callbackType == ScanSettings.CALLBACK_TYPE_FIRST_MATCH;
                String logText = String.format("%s detected: %s", enter ? "Enter " : "Leave ", scanResult.toString());
                Log.i(TAG, logText);
                NotificationHelper.showNotification(logText, enter ? 123 : 321);
            } else {
                Log.i(TAG, String.format("CallbackType: %s - Detected: %s", callbackType, scanResult.toString()));
            }
            //IBeaconResult info = new IBeaconResult(scanResult);
            //sManager.onResult(info);
        }
    }
}
