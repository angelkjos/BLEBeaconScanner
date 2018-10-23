package com.angelkjoseski.blebeacon;

import android.bluetooth.le.ScanFilter;
import android.content.res.Resources;
import android.util.Log;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IBeaconRegionManager {

    private static final String COMMON_UUID = "f7222a42-5dd5-5ff7-9999-88b0e2407e0d";
    private static Map<String, IBeaconRegion> Filters = new HashMap<>();

    static {
        IBeaconRegionManager.addRegion(
                new IBeaconRegion(COMMON_UUID, (short) 1, (short) 3380, 60, -40, "Fitspiration"));
        IBeaconRegionManager.addRegion(
                new IBeaconRegion(COMMON_UUID, (short) 1, (short) 16354, 60, -40, "Maurice Wilkes"));
        IBeaconRegionManager.addRegion(
                new IBeaconRegion(COMMON_UUID, (short) 1, (short) 4944, 60, -40, "Westfalenstadion"));
        IBeaconRegionManager.addRegion(
                new IBeaconRegion(COMMON_UUID, (short) 1, (short) 4939, 60, -40, "Name If you want a rainbow"));
    }

    public static void addRegion(IBeaconRegion region) {
        Filters.put(region.getId(), region);
    }

    public static List<ScanFilter> getScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        for (IBeaconRegion filterInfo : Filters.values()) {
            scanFilters.add(filterInfo.scanFilter);
        }
        return scanFilters;
    }

    public static IBeaconRegion getMatchRegion(IBeaconResult result) {
        IBeaconRegion region = getMatchRegion(result.uuid, result.major, result.minor);
        if (region == null) {
            region = getMatchRegion(result.uuid, result.major, null);
        }
        if (region == null) {
            region = getMatchRegion(result.uuid, null, null);
        }
        return region;
    }

    private static IBeaconRegion getMatchRegion(String uuid, Short major, Short minor) {
        String key = createKey(uuid, major, minor);
        return Filters.get(key);
    }

    public static String createKey(String uuid, Short major, Short minor) {
        String keyMajor = major != null ? ("_" + major) : "";
        String keyMinor = minor != null ? ("_" + minor) : "";
        return uuid + keyMajor + keyMinor;
    }
}