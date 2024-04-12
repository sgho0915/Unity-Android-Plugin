package com.systronics.plugin;

import android.content.Context;
import com.android.jws.JwsManager;

public class GPIOControl {
    private JwsManager jwsManager;

    public GPIOControl(Context context) {
        jwsManager = new JwsManager(context);
    }

    public int readGPIO(int port) {
        return jwsManager.jwsReadInputGpioValue(port);
    }

    public int writeGPIO(int port, boolean state) {
        return jwsManager.jwsSetExtrnalGpioValue(port, state);
    }
}
