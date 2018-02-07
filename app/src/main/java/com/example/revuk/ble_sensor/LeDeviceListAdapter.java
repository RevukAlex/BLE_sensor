package com.example.revuk.ble_sensor;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Revuk on 12/19/2017.
 */

public class LeDeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> mLeDevices;
    LayoutInflater inflter;


    public LeDeviceListAdapter(Context context, ArrayList<BluetoothDevice> mLeDevices1) {
        super();
        mLeDevices = mLeDevices1;
        inflter = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.item, null);
        TextView name = (TextView) view.findViewById(R.id._name);
        TextView mac = (TextView) view.findViewById(R.id._MAC);
        TextView UUID = (TextView) view.findViewById(R.id._else);
        name.setText(mLeDevices.get(i).getName());
        mac.setText(mLeDevices.get(i).getAddress());


        return view;
    }
}




