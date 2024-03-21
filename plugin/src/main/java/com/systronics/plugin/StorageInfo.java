package com.systronics.plugin;

import android.os.Environment;
import android.os.StatFs;

public class StorageInfo {

    // Bytes to Gigabytes 변환 상수
    private static final long BYTES_PER_GB = 1024 * 1024 * 1024;

    public static String getExternalStorageInfo() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSizeLong() * (long)stat.getAvailableBlocksLong();
        long totalBytes = (long)stat.getBlockSizeLong() * (long)stat.getBlockCountLong();
        long usedBytes = totalBytes - bytesAvailable;

        double totalGB = totalBytes / (double)BYTES_PER_GB;
        double usedGB = usedBytes / (double)BYTES_PER_GB;
        double freeGB = bytesAvailable / (double)BYTES_PER_GB;

        return "Total: " + String.format("%.2f", totalGB) + " GB, " +
                "Used: " + String.format("%.2f", usedGB) + " GB, " +
                "Free: " + String.format("%.2f", freeGB) + " GB";
    }

    public static String getTotalExternalStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long totalBytes = (long)stat.getBlockSizeLong() * (long)stat.getBlockCountLong();
        double dTotalBytes = totalBytes / (double)BYTES_PER_GB;
        return String.format("%.2f", dTotalBytes);
    }

    public static String getUsedExternalStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSizeLong() * (long)stat.getAvailableBlocksLong();
        long totalBytes = (long)stat.getBlockSizeLong() * (long)stat.getBlockCountLong();
        double dUsedBytes = (totalBytes - bytesAvailable) / (double)BYTES_PER_GB;
        return String.format("%.2f", dUsedBytes);
    }

    public static String getFreeExternalStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSizeLong() * (long)stat.getAvailableBlocksLong();
        double dbytesAvailable = bytesAvailable / (double)BYTES_PER_GB;
        return String.format("%.2f", dbytesAvailable);
    }
}
