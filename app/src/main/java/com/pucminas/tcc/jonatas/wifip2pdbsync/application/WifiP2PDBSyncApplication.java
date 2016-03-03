package com.pucminas.tcc.jonatas.wifip2pdbsync.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by jonatas on 03/03/16.
 */
public class WifiP2PDBSyncApplication extends Application {

    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();

        // Application context
        instance = this;
    }

    public static Context getInstance() {
        return instance;
    }
}
