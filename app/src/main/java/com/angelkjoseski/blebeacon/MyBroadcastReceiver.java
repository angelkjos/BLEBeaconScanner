package com.angelkjoseski.blebeacon;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
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

        for (ScanResult scanResult : scanResults) {
            IBeaconResult info = new IBeaconResult(scanResult);
            Log.i(TAG, "Detected: " + info);
            sManager.onResult(info);
        }
    }
}
