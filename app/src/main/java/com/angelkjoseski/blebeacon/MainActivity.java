package com.angelkjoseski.blebeacon;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;

    Spinner mScanModeSpinner;
    Spinner mCallbackSpinner;
    Spinner mMatchNumSpinner;
    EditText mReportDelay;
    CheckBox mAggressiveMatchCheckbox;
    TextView mTextFound;

    Button mBtnToggleScan;
    Button mBtnTimer;

    static PendingIntent mPendingIntent;
    static boolean mScanning;


    private static MainActivity sInstance;

    public static MainActivity getInstace() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sInstance = this;

        setContentView(R.layout.activity_main);

        IBeaconResult.setIgnoreMajorMSB(true);

        mScanning = false;

        Context context = getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mScanModeSpinner = findViewById(R.id.spinScanMode);
        mCallbackSpinner = findViewById(R.id.spinCallback);
        mMatchNumSpinner = findViewById(R.id.spinMatchNum);
        mReportDelay = findViewById(R.id.editReportDelay);
        mAggressiveMatchCheckbox = findViewById(R.id.cbAggressiveMatch);
        mTextFound = findViewById(R.id.textFound);

        mBtnToggleScan = findViewById(R.id.btnToggleScan);
        mBtnTimer = findViewById(R.id.btnTimer);

        updateFound(false);

        // TODO
        IBeaconRegionManager.addRegion(new IBeaconRegion("uuid", 120, -40));

        mBtnToggleScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mScanning) {
                    startBluetoothLowEnergyScanning();
                } else {
                    stopBluetoothLowEnergyScanning();
                }
            }
        });

        mBtnTimer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Timer...");
                MyBroadcastReceiver.getIBeaconManager().onTimer();
            }
        });
    }

    private void startBluetoothLowEnergyScanning() {
        Log.d(TAG, "Enabling Bluetooth...");
        mBluetoothAdapter.enable();

        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();

        int scanModePos = mScanModeSpinner.getSelectedItemPosition();
        switch (scanModePos) {
            case 0:
                settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
                Log.d(TAG, "Low power mode...");
                break;
            case 1:
                settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
                Log.d(TAG, "Balanced mode...");
                break;
            case 2:
                settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
                Log.d(TAG, "Low latency mode...");
                break;
            case 3:
                settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_OPPORTUNISTIC);
                Log.d(TAG, "Opportunistic mode...");
                break;
        }

        int callbackTypePos = mCallbackSpinner.getSelectedItemPosition();
        switch (callbackTypePos) {
            case 0:
                settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                Log.d(TAG, "All matches...");
                break;
            case 1:
                settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH | ScanSettings.CALLBACK_TYPE_MATCH_LOST);
                Log.d(TAG, "First + Lost match...");
                break;
            case 2:
                settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH);
                Log.d(TAG, "First match...");
                break;
            case 3:
                settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_MATCH_LOST);
                Log.d(TAG, "Lost match...");
                break;
        }

        int matchNumPos = mMatchNumSpinner.getSelectedItemPosition();
        switch (matchNumPos) {
            case 0:
                settingsBuilder.setNumOfMatches(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT);
                Log.d(TAG, "Few matches...");
                break;
            case 1:
                settingsBuilder.setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT);
                Log.d(TAG, "One match...");
                break;
            case 2:
                settingsBuilder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
                Log.d(TAG, "Max matches...");
                break;
        }

        if (mAggressiveMatchCheckbox.isChecked()) {
            settingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            Log.d(TAG, "Aggressive match...");
        } else {
            settingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
            Log.d(TAG, "Sticky match...");
        }

        int reportDelay = 1000;
        try {
            reportDelay = Integer.valueOf(mReportDelay.getText().toString());
            Log.d(TAG, "Report delay: " + reportDelay);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid report delay, defaulting to " + reportDelay);
        }
        settingsBuilder.setReportDelay(reportDelay);

        Log.d(TAG, "Extra scan settings...");
        settingsBuilder.setLegacy(false);

        Log.d(TAG, "Getting filters...");
        List<ScanFilter> filters = IBeaconRegionManager.getScanFilters();
        Log.i(TAG, "Starting iBeacon scanning...");
        mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settingsBuilder.build(), getScanIntent());

        mScanning = true;
        mBtnToggleScan.setText("Stop Scan");
    }

    private void stopBluetoothLowEnergyScanning() {
        PendingIntent pendingIntent = getScanIntent();

        if (mScanning && pendingIntent != null) {
            Log.i(TAG, "Stopping iBeacon scanning...");
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(pendingIntent);
        } else {
            Log.e(TAG, "NOT scanning. Nothing to do here...");
        }

        mScanning = false;
        mPendingIntent = null;

        MyBroadcastReceiver.getIBeaconManager().clear();

        updateToggleScanButton();
    }

    private void updateToggleScanButton() {
        mBtnToggleScan.setText(mScanning ? "Stop Scan" : "Start Scan");
    }

    public void updateFound(boolean found) {
        mTextFound.setText(found ? "Found!!  :)" : "NOT found!  :(");
    }

    private PendingIntent getScanIntent() {
        if (mPendingIntent == null) {
            Intent intent = new Intent(this, MyBroadcastReceiver.class);
            intent.putExtra("o-scan", true);
            mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mPendingIntent;
    }
}

