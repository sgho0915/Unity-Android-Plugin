package com.systronics.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.core.content.FileProvider;

import java.io.File;

public class UnityPlugin extends Activity {
    static String errMessage;

    // 앱 내 설치
    public static String InstallApp(Context context, String ApkPath){
        try {
            errMessage = "test";
            File toInstall = new File(ApkPath);
            Uri apkUri = FileProvider.getUriForFile(context,
                    context.getPackageName() +
                            ".fileprovider", toInstall);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception e){
            errMessage = e.getMessage();
        }
        return errMessage;
    }


    // 와이파이 상태 체크
    public static String getWiFiStatus(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null){
            if (wifiManager.isWifiEnabled()) {
                WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null && info.getNetworkId() != -1) {
                    int rssi = info.getRssi();
                    int level = WifiManager.calculateSignalLevel(rssi, 5);
                    String name = info.getSSID();
                    String macAddr = info.getMacAddress();
                    return "Connected" + name + level;
                }
            }
            else{
                return "wifi Disabled";
            }
        }
        return "wifiManager is null";
    }
}