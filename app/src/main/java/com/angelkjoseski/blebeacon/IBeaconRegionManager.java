package com.angelkjoseski.blebeacon;

import android.bluetooth.le.ScanFilter;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IBeaconRegionManager {

    private static Map<String, IBeaconRegion> Filters = new HashMap<>();

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