package com.angelkjoseski.blebeacon;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBluetoothLowEnergyScanning();
    }

    private void startBluetoothLowEnergyScanning() {
        ScanSettings settings =
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build();

        List<ScanFilter> filters = getScanFilters(); // Make a scan filter matching the beacons I care about

        BluetoothManager bluetoothManager =
                (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("o-scan", true);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        bluetoothAdapter.enable();
        bluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, pendingIntent);
    }

    private List<ScanFilter> getScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder().setDeviceAddress("A8:1E:84:AF:D8:33").build();
        scanFilters.add(scanFilter);
        return scanFilters;
    }
}
