package com.pucminas.tcc.jonatas.wifip2pdbsync.utils;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcel;

import org.greenrobot.eventbus.EventBus;

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
                EventBus.getDefault().post(group);
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

    public void connect() {
        WifiP2pConfig config = WifiP2pConfig.CREATOR.createFromParcel(Parcel.obtain());
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                EventBus.getDefault().post("Connected!");
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
                EventBus.getDefault().post("News peers has discovered!");
            }

            @Override
            public void onFailure(int reasonCode) {
                EventBus.getDefault().post(new WifiP2PError(reasonCode));
            }
        });
    }
}
