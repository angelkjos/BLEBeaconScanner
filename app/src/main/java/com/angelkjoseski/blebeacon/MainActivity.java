package com.angelkjoseski.blebeacon;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;

    EditText mTextResults;

    static final DateFormat sDF = new SimpleDateFormat("HH:mm");

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

        Context context = getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mTextResults = findViewById(R.id.editTextResults);

        mTextResults.setFocusable(false);
        mTextResults.setClickable(true);

        mTextResults.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return true;
            }
        });

        mTextResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Scan History", mTextResults.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });

        updateFound(false, false);

        Button btnStartScan = findViewById(R.id.btnStartScan);
        btnStartScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startBluetoothLowEnergyScanning();
            }
        });

        Button btnStopScan = findViewById(R.id.btnStopScan);
        btnStopScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBluetoothLowEnergyScanning();
            }
        });

        Button btnTimer = findViewById(R.id.btnTimer);
        btnTimer.setOnClickListener(new OnClickListener() {
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

        Spinner scanModeSpinner = findViewById(R.id.spinScanMode);
        int scanModePos = scanModeSpinner.getSelectedItemPosition();
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

        Spinner callbackSpinner = findViewById(R.id.spinCallback);
        int callbackTypePos = callbackSpinner.getSelectedItemPosition();
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

        Spinner matchNumSpinner = findViewById(R.id.spinMatchNum);
        int matchNumPos = matchNumSpinner.getSelectedItemPosition();
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

        CheckBox cbAggressiveMatch = findViewById(R.id.cbAggressiveMatch);
        if (cbAggressiveMatch.isChecked()) {
            settingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            Log.d(TAG, "Aggressive match...");
        } else {
            settingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
            Log.d(TAG, "Sticky match...");
        }

        int reportDelay = 1000;
        try {
            EditText editReportDelay = findViewById(R.id.editReportDelay);
            reportDelay = Integer.valueOf(editReportDelay.getText().toString());
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

        addToHistory("Scan started");
    }

    private void stopBluetoothLowEnergyScanning() {
        Log.i(TAG, "Stopping iBeacon scanning...");
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(getScanIntent());
        addToHistory("Scan stopped");
        MyBroadcastReceiver.getIBeaconManager().clear();
    }

    public void updateFound(boolean found, boolean addHistory) {
        TextView textFound = findViewById(R.id.textFound);
        textFound.setText(found ? "Found!!  :)" : "NOT found!  :(");
        if (addHistory) {
            addToHistory(found ? "IN" : "OUT");
        }
    }

    private void addToHistory(String text) {
        Date currentTime = Calendar.getInstance().getTime();
        mTextResults.append(sDF.format(currentTime) + " - " + text + "\n");
    }

    private PendingIntent getScanIntent() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("o-scan", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}

