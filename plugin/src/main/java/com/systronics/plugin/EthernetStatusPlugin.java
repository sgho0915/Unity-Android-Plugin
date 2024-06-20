package com.systronics.plugin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class EthernetStatusPlugin {

    // 이더넷 연결 상태 확인
    public static boolean isEthernetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();

        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true;
            }
        }
        return false;
    }

    // 무선랜(Wi-Fi) 내부 IP 주소 가져오기
    public static String getWifiLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ipAddress);
    }

    // 무선랜(Wi-Fi) MAC 주소 가져오기
    public static String getWifiMacAddress() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
            String macAddress = bufferedReader.readLine();
            bufferedReader.close();
            return macAddress;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // 유선랜(Ethernet) 내부 IP 주소 가져오기
    public static String getEthernetLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (networkInterface.getName().startsWith("eth")) {
                    List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                    for (InetAddress address : addresses) {
                        if (!address.isLoopbackAddress() && address instanceof InetAddress) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 유선랜(Ethernet) MAC 주소 가져오기
    public static String getEthernetMacAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                if (networkInterface.getName().startsWith("eth")) {
                    byte[] mac = networkInterface.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder macAddress = new StringBuilder();
                        for (byte b : mac) {
                            macAddress.append(String.format("%02X:", b));
                        }
                        if (macAddress.length() > 0) {
                            macAddress.deleteCharAt(macAddress.length() - 1);
                        }
                        return macAddress.toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
