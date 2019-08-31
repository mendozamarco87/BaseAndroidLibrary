package com.mm87.android.lib.base.config;

import android.app.Application;

public abstract class AppBase extends Application {

    protected static AppBase miApp;

    @Override
    public void onCreate() {
        super.onCreate();
        miApp = this;
    }


    public static AppBase getInstance() {
        return miApp;
    }


}
