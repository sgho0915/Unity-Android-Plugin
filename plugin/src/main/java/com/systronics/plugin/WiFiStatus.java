package com.systronics.plugin;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class WiFiStatus {

    public static boolean isWifiOn = false;
    public static String ssid = "";
    public static int rssi = 0;
    public static boolean getWiFiOn(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            isWifiOn = true;
            return isWifiOn;
        } else {
            isWifiOn = false;
            return isWifiOn;
        }
    }

    public static String getWiFiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            ssid = "<unknown ssid>";
//        }
        return ssid;
    }

    public static int getWiFiRSSI(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        rssi = wifiInfo.getRssi();
        return rssi;
    }
}