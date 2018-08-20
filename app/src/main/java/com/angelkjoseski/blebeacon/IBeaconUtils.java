package com.angelkjoseski.blebeacon;

import android.bluetooth.le.ScanFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

class IBeaconUtils {
    private static final String TAG = "IBeaconUtils";

    static final int MANUFACTURER_ID = 0x004C;

    private static byte OFF = (byte) 0x00;
    private static byte ON = (byte) 0xff;

    public static long now() {
        return System.currentTimeMillis() / 1000;
    }

    @NonNull
    static ScanFilter buildScanFilter(String uuid, Short major, Short minor) {
        byte[] mask =
                new byte[]{
                        /* header  */
                        ON,
                        ON,
                        /* uuid    */
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        ON,
                        /* major   */
                        OFF,
                        OFF,
                        /* minor   */
                        OFF,
                        OFF,
                        /* RSSI@1m */
                        OFF,
                };

        byte[] manufacturerData = new byte[23];
        manufacturerData[0] = 0x02;
        manufacturerData[1] = 0x15;

        byte[] uuidByteArray = UuidToByteArray(uuid);
        System.arraycopy(uuidByteArray, 0, manufacturerData, 2, 16);

        if (major != null) {
            byte[] majorByteArray = ShortToByteArray(major, ByteOrder.BIG_ENDIAN);
            System.arraycopy(majorByteArray, 0, manufacturerData, 18, 2);
            Log.i(TAG, "Major: 0x" + String.format("%02X", majorByteArray[0]) + String.format("%02X", majorByteArray[1]));
            mask[18] = IBeaconResult.isIgnoreMajorMSB() ? OFF : ON;
            mask[19] = ON;
        }

        if (minor != null) {
            byte[] minorByteArray = ShortToByteArray(minor, ByteOrder.BIG_ENDIAN);
            System.arraycopy(minorByteArray, 0, manufacturerData, 20, 2);
            Log.i(TAG, "Minor: 0x" + String.format("%02X", minorByteArray[0]) + String.format("%02X", minorByteArray[1]));
            mask[20] = ON;
            mask[21] = ON;
        }

        {
            String data = "";
            for (byte b : manufacturerData)
                data = data + String.format("%02X", b);
            Log.i(TAG, "Data: " + data);
        }
        {
            String data = "";
            for (byte b : mask)
                data = data + String.format("%02X", b);
            Log.i(TAG, "Mask: " + data);
        }

        return new ScanFilter.Builder().setManufacturerData(MANUFACTURER_ID, manufacturerData, mask).build();
    }

    @NonNull
    static byte[] UuidToByteArray(@NonNull String uuid) {
        String hex = uuid.replace("-", "");
        int length = hex.length();
        byte[] result = new byte[length / 2];

        for (int ii = 0; ii < length; ii += 2) {
            result[ii / 2] = (byte) ((Character.digit(hex.charAt(ii), 16) << 4) + Character.digit(hex.charAt(ii + 1), 16));
        }

        return result;
    }

    @NonNull
    static byte[] ShortToByteArray(short value) {
        return ShortToByteArray(value, ByteOrder.LITTLE_ENDIAN);
    }

    @NonNull
    static byte[] ShortToByteArray(short value, ByteOrder order) {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.order(order);
        b.putShort(value);
        return b.array();
    }

    @NonNull
    static UUID parseUuid(byte[] bytes, int begin, int size) {
        ByteBuffer bb = ByteBuffer.wrap(bytes, begin, size);
        bb.order(ByteOrder.BIG_ENDIAN);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    static int parseInt(@NonNull byte[] source, int begin, int size, boolean signed) {
        int val = 0;
        for (int ii = begin; ii < begin + size; ++ii) {
            val = (val << 8) | (source[ii] & 0xff);
        }

        if (signed) {
            int mask = 1 << (size << 3);
            if ((val & (mask >> 1)) > 0) {
                val = val - mask;
            }
        }
        return val;
    }

    static String Unsigned(short value) {
        return Long.toString(value & 0xFFFFFFFFL);
    }
}
