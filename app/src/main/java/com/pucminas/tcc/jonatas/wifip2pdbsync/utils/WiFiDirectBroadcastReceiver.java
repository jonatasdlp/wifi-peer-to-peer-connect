package com.pucminas.tcc.jonatas.wifip2pdbsync.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.pucminas.tcc.jonatas.wifip2pdbsync.adapters.DevicesAdapter;
import com.pucminas.tcc.jonatas.wifip2pdbsync.application.WifiP2PDBSyncApplication;

import java.util.Collection;

/**
 * Created by jonatas on 03/03/16.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                Toast toast = Toast.makeText(WifiP2PDBSyncApplication.getInstance(),
                        "WI-FI DISABLED!", Toast.LENGTH_SHORT);
                toast.show();
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {

                @Override
                public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                    DevicesAdapter adapter = new DevicesAdapter(wifiP2pDeviceList.getDeviceList());
                    Collection<WifiP2pDevice> devices = wifiP2pDeviceList.getDeviceList();

                    // TODO: notify list
                }
            });

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            Toast toast = Toast.makeText(WifiP2PDBSyncApplication.getInstance(),
                    "New peers has discovered!", Toast.LENGTH_SHORT);
            toast.show();

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            // TODO: Respond to this device's wifi state changing

        }
    }
}