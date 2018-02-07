package com.example.revuk.ble_sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_FLOAT;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT16;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT32;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;

public class Connect_Activity extends AppCompatActivity {


    private static UUID NANOPI_BLE_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private static UUID NANOP = UUID.fromString("6e400005-b5a3-f393-e0a9-e50e24dcca9e");

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    private static final String TAG = "h";
    private BluetoothDevice device;
    ParcelUuid[] uuids;
    Connect_Adapter con_ad;
    BluetoothAdapter adapter;
    String MACadress;
    BluetoothGatt mBluetoothGatt;
    TextView mainText;
    private BluetoothGattService mBluetoothGattService;
    ListView list_characteristics;
    List<BluetoothGattCharacteristic> gattCharacteristics;
    ArrayList<BluetoothGattCharacteristic> characteristics;
    Characteristics_Adapter charc;
    int num_data;
    BluetoothGattDescriptor descriptor;

    AdvertisingSet currentAdvertisingSet;

    byte[] advertice = "Data".getBytes( );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);


        TextView device_name = (TextView) findViewById(R.id._device_name);
        TextView MAC_adress = (TextView) findViewById(R.id._MAC_adress);
        mainText = (TextView) findViewById(R.id.mainText);

        ListView listView = (ListView) findViewById(R.id.list_UUID);
        list_characteristics = (ListView) findViewById(R.id.characteristics);


        characteristics = new ArrayList<BluetoothGattCharacteristic>();

        Bundle data = getIntent().getExtras();
        device = (BluetoothDevice) data.getParcelable("BLEdevice");

        device_name.setText(device.getName());
        MAC_adress.setText(device.getAddress());
        MACadress = (String) MAC_adress.getText();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        Method getUuidsMethod = null;
        try {
            getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        for (ParcelUuid uuid : uuids) {
            Log.d(TAG, "UUID: " + uuid.getUuid().toString());
        }



        con_ad = new Connect_Adapter(getApplicationContext(), uuids);
        listView.setAdapter(con_ad);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View view) {


        //  Toast.makeText(this, MACadress, Toast.LENGTH_SHORT).show();
        connectDevice();


    }


    private boolean connectDevice() {

        num_data = 3;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();


        BluetoothDevice device1 = adapter.getRemoteDevice(device.getAddress());
        if (device1 == null) {
            //  statusUpdate("Unable to connect");
            return false;
        }

        BluetoothLeAdvertiser advertiser = adapter.getBluetoothLeAdvertiser();
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( false )
                .build();


        final AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceUuid( uuids[2] )
                .addServiceData( uuids[2], advertice )
                .build();

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {

                if (advertice != null && advertice.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(advertice.length);
                    for (byte byteChar : advertice)
                        stringBuilder.append(String.format("0x%02X ", byteChar));
                    statusUpdate("Advertice Data: "  + stringBuilder.toString() + ")");
                } else {
                    statusUpdate("Advertice Data: no data");
                }



                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising( settings, data, advertisingCallback );


        statusUpdate("Connecting ...");
        mBluetoothGatt = device1.connectGatt(this, false, mGattCallback);
        return true;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                statusUpdate("Connected");
                statusUpdate("Searching for services");
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                statusUpdate("Device disconnected");

               // num_data = 3;
                //connectDevice();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {


            if (status == BluetoothGatt.GATT_SUCCESS) {


                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
                for (BluetoothGattService gattService : gattServices) {
                   // statusUpdate("Service discovered : " + gattService.getUuid());


                }


                gattCharacteristics = mBluetoothGatt.getService(NANOPI_BLE_SERVICE_UUID).getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                   // statusUpdate("Characteristic discovered: " + gattCharacteristic.getUuid());
                    characteristics.add(gattCharacteristic);







                }
                characteristics.get(num_data).setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGatt.setCharacteristicNotification(characteristics.get(num_data), true);
                get_data(characteristics.get(num_data));




            }

        }


            @Override
            public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic) {

                final byte[] data =  characteristic.getValue();
                final int str0 = characteristic.getIntValue(FORMAT_UINT8,0);
                final int str1 = characteristic.getIntValue(FORMAT_UINT8,1);
                //final int str1 = descriptor.
                final String str2 = characteristic.getStringValue(0).toString();


                if (data != null && data.length > 0) {
                    final StringBuilder sB = new StringBuilder(data.length);

                    for (byte byteChar : data)
                        sB.append(String.format("0x%02X ", byteChar));
                    statusUpdate("Received Data: " + str0 +  " "   + str1  + " " + str2  + " (" + sB.toString() + ")");
                } else {
                    statusUpdate("Received Data: no data");
                }




                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
                    // and then call setValue on that descriptor
                    byte[] descr = descriptor.getValue();

                    if (descr != null && descr.length > 0) {
                        final StringBuilder stringBuilder = new StringBuilder(descr.length);
                        for (byte byteChar : descr)
                            stringBuilder.append(String.format("0x%02X ", byteChar));
                        statusUpdate("Descriptor" + " = " + stringBuilder.toString());
                    } else {
                        statusUpdate("Received Data: no data");
                    }
                }


            }


    };






        private void get_data(BluetoothGattCharacteristic characteristic) {


           descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
          //  characteristic.setValue(new byte[]{1, 1});
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            mBluetoothGatt.writeDescriptor(descriptor);

        }












        //output helper method
        private void statusUpdate(final String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.w("BLE", msg);
                    mainText.setText(mainText.getText() + "\r\n" + msg);
                }
            });
        }

}