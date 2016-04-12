package com.pucminas.tcc.jonatas.wifip2pdbsync.adapters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pucminas.tcc.jonatas.wifip2pdbsync.R;
import com.pucminas.tcc.jonatas.wifip2pdbsync.application.WifiP2PDBSyncApplication;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jonatas on 03/03/16.
 */
public class DevicesAdapter extends ArrayAdapter<WifiP2pDevice> {

    private List<WifiP2pDevice> mDevices;

    public DevicesAdapter(Collection<WifiP2pDevice> devices) {
        super(WifiP2PDBSyncApplication.getInstance(), R.layout.device_item_layout,
                new LinkedList<>(devices));

        mDevices = new LinkedList<>(devices);
    }

    public List<WifiP2pDevice> getList() {
        return  mDevices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            Context context = WifiP2PDBSyncApplication.getInstance();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.device_item_layout, null);
        }

        if (mDevices.get(position) != null) {
            WifiP2pDevice device = mDevices.get(position);
            String info = device.deviceName +  " - "  + device.toString();

            TextView text = (TextView) rowView.findViewById(R.id.device);
            text.setText(info);
        }

        return rowView;
    }
}
