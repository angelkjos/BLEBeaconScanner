package com.angelkjoseski.blebeacon;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * BroadcastReceiver which will be called with the PendingIntent from Bluetooth beacon scanning.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = MyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

        int bleCallbackType = intent.getIntExtra(BluetoothLeScanner.EXTRA_CALLBACK_TYPE, -1);
        if (bleCallbackType != -1) {
            Log.d(TAG, "Passive background scan callback type: "+bleCallbackType);
            ArrayList<ScanResult> scanResults = intent.getParcelableArrayListExtra(
                    BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT);

            Log.d(TAG, "ScanResult: " + scanResults.toString());
            Toast.makeText(context, scanResults.toString(), Toast.LENGTH_SHORT).show();

            // Do something with your ScanResult list here.
            // These contain the data of your matching BLE advertising packets
        }
    }
}
