package com.example.revuk.ble_sensor;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.ParcelUuid;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Revuk on 12/28/2017.
 */

public class Characteristics_Adapter extends BaseAdapter {

    ArrayList<BluetoothGattCharacteristic> characteristics;
    LayoutInflater inflater;


    public Characteristics_Adapter(Context context, ArrayList<BluetoothGattCharacteristic> characteristics1) {
        super();
        characteristics = characteristics1;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return characteristics.size();
    }

    @Override
    public Object getItem(int i) {
        return characteristics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.item_characteristics, null);
        TextView charac_UUID = (TextView) view.findViewById(R.id.char_UUID);
        TextView charac_property = (TextView) view.findViewById(R.id.char_property);

        charac_UUID.setText(characteristics.get(i).getUuid().toString());
        charac_property.setText(String.valueOf( characteristics.get(i).getProperties()));


        return view;
    }
}
