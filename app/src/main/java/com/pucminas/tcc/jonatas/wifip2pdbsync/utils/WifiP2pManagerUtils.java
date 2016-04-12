package com.pucminas.tcc.jonatas.wifip2pdbsync.utils;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcel;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;

/**
 * Created by jonatas on 29/03/16.
 */
public class WifiP2pManagerUtils {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    public WifiP2pManagerUtils(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        mManager = manager;
        mChannel = channel;
    }

    public void requestGroupInfo() {
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                if (group != null) {
                    EventBus.getDefault().post(group);
                } else {
                    EventBus.getDefault().post("No group found!");
                }
            }
        });
    }

    public void requestConnectionInfo() {
        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                InetAddress groupOwnerAddress = info.groupOwnerAddress;

                if (info.groupFormed && info.isGroupOwner) {
                    EventBus.getDefault().post(groupOwnerAddress.toString());
                } else if (info.groupFormed) {
                    EventBus.getDefault().post("Group Formed! Check a devices list...");
                }
            }
        });
    }

    public void createGroup() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post("New group successful created!");
            }

            @Override
            public void onFailure(int reason) {
                EventBus.getDefault().post(new WifiP2PError(reason));
            }
        });
    }

    public void connect(final WifiP2pDevice device, int intent) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.groupOwnerIntent = intent;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post(device.deviceName + " connected!");
            }

            @Override
            public void onFailure(int reason) {
                EventBus.getDefault().post(new WifiP2PError(reason));
            }
        });
    }

    public void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post("Updating peers list...");
            }

            @Override
            public void onFailure(int reasonCode) {
                EventBus.getDefault().post(new WifiP2PError(reasonCode));
            }
        });
    }
}
