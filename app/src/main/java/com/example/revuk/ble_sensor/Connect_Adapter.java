package com.example.revuk.ble_sensor;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Revuk on 12/27/2017.
 */

public class Connect_Adapter extends BaseAdapter {

    ParcelUuid[] uuid;
    LayoutInflater inflater;


    public Connect_Adapter(Context context, ParcelUuid[] uuids) {
        super();
        uuid = uuids;
        inflater = (LayoutInflater.from(context));
    }




    @Override
    public int getCount() {
        return uuid.length;
    }

    @Override
    public Object getItem(int i) {
        return uuid[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        view = inflater.inflate(R.layout.item_uuid, null);
        TextView data = (TextView) view.findViewById(R.id.textView_UUID);
        TextView any = (TextView) view.findViewById(R.id.textView_any);

        data.setText(uuid[i].getUuid().toString());
        any.setText(String.valueOf(uuid[i].hashCode()));



        return view;
    }
}
