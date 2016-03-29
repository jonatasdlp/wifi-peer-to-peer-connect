package com.pucminas.tcc.jonatas.wifip2pdbsync.activities;

import android.net.wifi.p2p.WifiP2pGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.pucminas.tcc.jonatas.wifip2pdbsync.R;

public class GroupInfoActivity extends AppCompatActivity {

    private WifiP2pGroup mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        String jsonObject = null;
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            jsonObject = extras.getString("group");
            mGroup = new Gson().fromJson(jsonObject, WifiP2pGroup.class);
        }
    }
}
