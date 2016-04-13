package com.pucminas.tcc.jonatas.wifip2pdbsync.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pucminas.tcc.jonatas.wifip2pdbsync.R;
import com.pucminas.tcc.jonatas.wifip2pdbsync.adapters.DevicesAdapter;
import com.pucminas.tcc.jonatas.wifip2pdbsync.utils.WiFiDirectBroadcastReceiver;
import com.pucminas.tcc.jonatas.wifip2pdbsync.utils.WifiP2PError;
import com.pucminas.tcc.jonatas.wifip2pdbsync.utils.WifiP2pManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private Menu mMenu;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManagerUtils mWifiP2pManagerUtils;
    WifiP2pManagerUtils mWifiP2pManagerUtilsAux;

    @Bind(R.id.device_ip)
    TextView mDeviceIp;

    @Bind(R.id.device_mac)
    TextView mDeviceMac;

    @Bind(R.id.group_info)
    TextView mGroupInfo;

    @Bind(R.id.group_container)
    LinearLayout mContainer;

    @Bind(R.id.devices_list)
    ListView mList;

    @Bind(R.id.seek_bar)
    SeekBar mSeekBar;

    private WifiP2pGroup mCurrentGroup;
    private int mGroupOwnerIntent = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel);
        mWifiP2pManagerUtils = new WifiP2pManagerUtils(this, mManager, mChannel);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();

        mDeviceMac.setText(String.valueOf(info.getMacAddress()));
        mDeviceIp.setText(Formatter.formatIpAddress(info.getIpAddress()));

        mWifiP2pManagerUtils.discoverPeers();

        setupSeekBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, mIntentFilter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
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
               mWifiP2pManagerUtils.createGroup();
                return true;
            case R.id.action_refresh:
                mWifiP2pManagerUtils.discoverPeers();
                return true;
            case R.id.action_group_info:
                mWifiP2pManagerUtils.requestGroupInfo();
                return true;
            case R.id.action_connect:
                mWifiP2pManagerUtils.requestConnectionInfo();
                return true;
            case R.id.action_list_devices:
                gotToGroupScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onDevices(final WifiP2pDeviceList devices) {
        final DevicesAdapter adapter = new DevicesAdapter(devices.getDeviceList());
        mList.setAdapter(adapter);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice device= adapter.getList().get(position);
                mWifiP2pManagerUtils.connect(device, mGroupOwnerIntent);
                return false;
            }
        });
    }

    @Subscribe
    public void onGroupInfo(WifiP2pGroup group) {
        mCurrentGroup = group;
        updateView(group);
    }

    @Subscribe
    public void onError(WifiP2PError error) {
        Toast.makeText(getApplicationContext(), error.getReason(), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onWifiP2pManagerUtils(WifiP2pManagerUtils manager) {
        mWifiP2pManagerUtilsAux = manager;
    }

    @OnClick(R.id.show_group)
    public void gotToGroupScreen() {
        if (mCurrentGroup != null) {
            final DevicesAdapter adapter = new DevicesAdapter(mCurrentGroup.getClientList());
            mList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateView(WifiP2pGroup group) {
        mContainer.setVisibility(View.VISIBLE);
        mGroupInfo.setVisibility(View.VISIBLE);
        mGroupInfo.setText(group.toString());
    }

    private void removeGroupView(WifiP2pGroup group) {
        mContainer.setVisibility(View.GONE);
        mGroupInfo.setVisibility(View.GONE);
    }

    private void setupSeekBar() {
        final int step = 1;
        final int min = 0;
        int max = 15;

        mSeekBar.setMax((max - min) / step);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGroupOwnerIntent = min + (progress * step);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
