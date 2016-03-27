package com.pucminas.tcc.jonatas.wifip2pdbsync.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pucminas.tcc.jonatas.wifip2pdbsync.R;
import com.pucminas.tcc.jonatas.wifip2pdbsync.utils.WiFiDirectBroadcastReceiver;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Menu mMenu;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    @Bind(R.id.device_ip)
    TextView mDeviceIp;

    @Bind(R.id.device_bss)
    TextView mDeviceBss;

    @Bind(R.id.device_mac)
    TextView mDeviceMac;

    @Bind(R.id.device_link)
    TextView mLink;

    @Bind(R.id.device_freq)
    TextView mDeviceFreq;

    @Bind(R.id.devices_list)
    ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, mList);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        mDeviceMac.setText(String.valueOf(info.getMacAddress()));
        mDeviceIp.setText(Formatter.formatIpAddress(info.getIpAddress()));
        mDeviceBss.setText(String.valueOf(info.getBSSID()));

        String link = String.valueOf(info.getLinkSpeed()) + "Mbps";
        mLink.setText(link);

        String freq = String.valueOf(info.getFrequency()) + "MHz";
        mDeviceFreq.setText(freq);


        discoverPeers();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, mMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_group:
                createGroup();
                return true;
            case R.id.action_refresh:
                discoverPeers();
                return true;
            case R.id.action_connect:
                connect();
            case R.id.action_list_devices:
                listGroupDevices();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestGroupInfo() {
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                // TODO: show group devices
            }
        });
    }

    private void listGroupDevices() {
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                // TODO: list group.getClientList();
            }
        });
    }

    private void createGroup() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "New group successful created!", Toast.LENGTH_SHORT);
                toast.show();
                getMenuInflater().inflate(R.menu.main_menu_group, mMenu);
            }

            @Override
            public void onFailure(int reason) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Fail!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void connect() {
        WifiP2pConfig config = WifiP2pConfig.CREATOR.createFromParcel(Parcel.obtain());
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // TODO: show group info

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Successful connected!", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(int reason) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Fail!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Cool, new peers!", Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        String.valueOf(reasonCode), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
