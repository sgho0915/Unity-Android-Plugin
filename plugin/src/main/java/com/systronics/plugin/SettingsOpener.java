package com.systronics.plugin;

import android.content.Intent;
import android.content.Context;
import com.unity3d.player.UnityPlayer;

public class SettingsOpener {

    public static void openSettings() {
        Context context = UnityPlayer.currentActivity;
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }
}
