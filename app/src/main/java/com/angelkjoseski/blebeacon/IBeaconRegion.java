package com.angelkjoseski.blebeacon;

import android.bluetooth.le.ScanFilter;

public class IBeaconRegion {
    public String uuid;
    public Short major;
    public Short minor;
    long ttl;
    public int powerThreshold;
    ScanFilter scanFilter;

    public static String createId(String uuid, Short major, Short minor) {
        String keyMajor = major != null ? ("_" + IBeaconUtils.Unsigned(major)) : "";
        String keyMinor = minor != null ? ("_" + IBeaconUtils.Unsigned(minor)) : "";
        return uuid + keyMajor + keyMinor;
    }

    public IBeaconRegion(String uuid) {
        this(uuid, null, null, 120, -40);
    }

    public IBeaconRegion(String uuid, long ttl) {
        this(uuid, null, null, ttl, -40);
    }

    public IBeaconRegion(String uuid, long ttl, int powerThreshold) {
        this(uuid, null, null, ttl, powerThreshold);
    }

    public IBeaconRegion(String uuid, Short major, long ttl, int powerThreshold) {
        this(uuid, major, null, ttl, powerThreshold);
    }

    public IBeaconRegion(String uuid, Short major, Short minor, long ttl, int powerThreshold) {
        this.uuid = uuid;

        if (major != null && IBeaconResult.isIgnoreMajorMSB()) {
            this.major = (short) (major & 0x00ff);
        } else {
            this.major = major;
        }

        this.minor = minor;
        this.ttl = ttl;
        this.powerThreshold = powerThreshold;
        this.scanFilter = IBeaconUtils.buildScanFilter(uuid, major, minor);
    }

    public String getId() {
        return createId(uuid, major, minor);
    }

    @Override
    public String toString() {
        return createId(uuid, major, minor);
    }
}
