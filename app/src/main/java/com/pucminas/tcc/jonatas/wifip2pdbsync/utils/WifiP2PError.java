package com.pucminas.tcc.jonatas.wifip2pdbsync.utils;

import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by jonatas on 29/03/16.
 */
public class WifiP2PError {

    private int mReason;

    public WifiP2PError(int reason) {
        mReason = reason;
    }

    public String getReason() {
        switch (mReason) {
            case WifiP2pManager.ERROR:
                return "Operation failed due to an internal error";
            case WifiP2pManager.BUSY:
                return "Operation failed because p2p is unsupported on the device";
            case WifiP2pManager.P2P_UNSUPPORTED:
                return "Operation failed because the framework is busy and unable to service the request.";
        }

        return "Error";
    }
}
