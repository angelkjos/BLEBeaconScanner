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

    private static final IBeaconManager sManager = new IBeaconManager(new IBeaconManager.Listener() {
        @Override
        public void onDetect(IBeaconResult result, IBeaconRegion region) {
        }

        @Override
        public void onEnter(IBeaconResult result, IBeaconRegion region) {
            Log.i(TAG, "Event iBeacon IN: " + result + "  -  match: " + region);
            if ("2008cf83-ecd7-52e1-a9ff-5f367e8a10cb".equals(result.uuid)) {
                Log.i(TAG, "FOUND!");
                MainActivity.getInstace().updateFound(true);
            }
        }

        @Override
        public void onLeave(IBeaconResult result, IBeaconRegion region) {
            Log.i(TAG, "Event iBeacon OUT: " + result + "  -  match: " + region);
            if ("2008cf83-ecd7-52e1-a9ff-5f367e8a10cb".equals(result.uuid)) {
                Log.i(TAG, "LOST!");
                MainActivity.getInstace().updateFound(false);
            }
        }

        @Override
        public void onIngored(IBeaconResult result, IBeaconRegion region) {
        }
    });

    public static IBeaconManager getIBeaconManager() {
        return sManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        int bleCallbackType = intent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1);
//        Log.i(TAG, "BT Event: " + bleCallbackType);

        ArrayList<ScanResult> scanResults = intent.getParcelableArrayListExtra(
                BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT);

        for (ScanResult scanResult : scanResults) {
            IBeaconResult info = new IBeaconResult(scanResult);
            sManager.onResult(info);
        }
    }
}
