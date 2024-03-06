package com.systronics.plugin;

import android.app.Application;
import android.content.Context;

public class ContextClass extends Application {
    private  static ContextClass instance;

    public ContextClass(){
        instance = this;
    }

    public static ContextClass instance(){
        return instance;
    }

    public static Context context() {
        return instance.getApplicationContext();
    }
}