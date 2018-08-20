package com.angelkjoseski.blebeacon;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

public class IBeaconResult {
    public String mac;
    public String uuid;
    public short major;
    public short minor;
    public int rssi1m;
    public int rssi;
    public long timestamp;

    static boolean sIgnoreMajorMSB = false;

    public static void setIgnoreMajorMSB(boolean ignoreMajorMSB) {
        sIgnoreMajorMSB = ignoreMajorMSB;
    }

    public static boolean isIgnoreMajorMSB() {
        return sIgnoreMajorMSB;
    }

    public static boolean isValid(ScanResult scanResult) {
        ScanRecord record = scanResult.getScanRecord();
        if (record == null || record.getManufacturerSpecificData() == null) {
            return false;
        }

        int manufacturerID = record.getManufacturerSpecificData().keyAt(0);
        byte[] manufacturerData = record.getManufacturerSpecificData().valueAt(0);
        if (manufacturerID != IBeaconUtils.MANUFACTURER_ID || manufacturerData == null || manufacturerData[0] != 2) {
            return false;
        }

        return true;
    }

    public IBeaconResult(ScanResult scanResult) {
        ScanRecord record = scanResult.getScanRecord();
        BluetoothDevice device = scanResult.getDevice();
        byte[] manufacturerData = record.getManufacturerSpecificData().valueAt(0);

        this.mac = device.getAddress();
        this.rssi = scanResult.getRssi();
        this.uuid = IBeaconUtils.parseUuid(manufacturerData, 2, 16).toString();
        this.major = (short) IBeaconUtils.parseInt(manufacturerData, 18, 2, false);

        if (sIgnoreMajorMSB) {
            this.major = (short) (this.major & 0x00FF);
        }

        this.minor = (short) IBeaconUtils.parseInt(manufacturerData, 20, 2, false);
        this.rssi1m = IBeaconUtils.parseInt(manufacturerData, 22, 1, true);
        this.timestamp = IBeaconUtils.now();
    }

    @Override
    public String toString() {
        return mac + " - " + uuid + " - " + String.format("%5s - %5s", IBeaconUtils.Unsigned(major), IBeaconUtils.Unsigned(minor)) + " - " + rssi + " - " + rssi1m;
    }
}
