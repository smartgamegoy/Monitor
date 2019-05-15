package com.jetec.Monitor.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jetec.Monitor.SupportFunction.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@SuppressLint("Registered")
public class BluetoothLeService extends Service {

    private final static String TAG = "bluetoothleservice";
    LogMessage logMessage = new LogMessage();
    PhoneName phoneName = new PhoneName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public BluetoothGatt mBluetoothGatt;
    public int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART =
            "com.example.bluetooth.le.DEVICE_DOES_NOT_SUPPORT_UART";

    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private Boolean oppo = false;

    public void enableTXNotification() {
        List<BluetoothGattService> bluetoothGattServices = mBluetoothGatt.getServices();
        for (int i = 0; i < bluetoothGattServices.size(); i++) {
            List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = bluetoothGattServices.get(i).getCharacteristics();
            for (int j = 0; j < bluetoothGattCharacteristics.size(); j++) {
                if (bluetoothGattCharacteristics.get(j).getProperties() == 0xC) {
                    Value.Service_uuid = bluetoothGattServices.get(i).getUuid().toString();
                    Value.Characteristic_uuid_RX = bluetoothGattCharacteristics.get(j).getUuid().toString();
                }
                if (bluetoothGattCharacteristics.get(j).getProperties() == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                    Value.Service_uuid = bluetoothGattServices.get(i).getUuid().toString();
                    Value.Characteristic_uuid_TX = bluetoothGattCharacteristics.get(j).getUuid().toString();
                }
            }
        }

        BluetoothGattService RxService = mBluetoothGatt.getService(UUID.fromString(Value.Service_uuid));
        if (RxService == null) {
            logMessage.showmessage(TAG, "Rx service not found on enable!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            mConnectionState = STATE_DISCONNECTED;
            logMessage.showmessage(TAG, "Disconnected from GATT server.");
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            return;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(UUID.fromString(Value.Characteristic_uuid_TX));
        if (TxChar == null) {
            logMessage.showmessage(TAG, "Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar, true);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); //ENABLE_NOTIFICATION_VALUE
        mBluetoothGatt.writeDescriptor(descriptor);
        logMessage.showmessage(TAG, "mBluetoothGatt = 發生變化");
    }

    public void writeRXCharacteristic(byte[] value) {

        logMessage.showmessage(TAG, "Service_uuid = " + Value.Service_uuid);
        logMessage.showmessage(TAG, "Characteristic_uuid_TX = " + Value.Characteristic_uuid_TX);
        logMessage.showmessage(TAG, "Characteristic_uuid_RX = " + Value.Characteristic_uuid_RX);
        logMessage.showmessage(TAG, "mBluetoothGatt = " + mBluetoothGatt.getServices());

        BluetoothGattService RxService = mBluetoothGatt.getService(UUID.fromString(Value.Service_uuid));
        if (RxService == null) {
            logMessage.showmessage(TAG, "Rx service not found on RxService!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            //mBluetoothGatt.close();
            mConnectionState = STATE_DISCONNECTED;
            logMessage.showmessage(TAG, "Disconnected from GATT server.");
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            return;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(Value.Characteristic_uuid_RX));
        if (RxChar == null) {
            logMessage.showmessage(TAG, "Rx charateristic not found onRxchar!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
        logMessage.showmessage(TAG, "writeRXCharacteristic - status = " + status);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        int getstatus;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                getstatus = status;
                gatt.discoverServices();
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                logMessage.showmessage(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                logMessage.showmessage(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                logMessage.showmessage(TAG, "已連線");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                getstatus = status;
                logMessage.showmessage(TAG, "斷線囉");
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                logMessage.showmessage(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                logMessage.showmessage(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (oppo) {
                gatt.disconnect();
                gatt.close();
            }
            readCharacteristic(characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        Intent intent = new Intent(action);
        this.sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUpdate(String action, BluetoothGattCharacteristic characteristic) {
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        //Log.e(TAG, "uuid = " + characteristic.getUuid());
        if (UUID.fromString(Value.Characteristic_uuid_TX).equals(characteristic.getUuid())) {
            Intent intent = new Intent(action);
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
            this.sendBroadcast(intent);
        } else {
        }
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
        //return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    public IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                Log.e(TAG, "mBluetoothGatt connect.");
                return true;
            } else {
                Log.e(TAG, "mBluetoothGatt disconnect.");
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        refreshDeviceCache(mBluetoothGatt);
        Log.d(TAG, "Trying to create a new connection.");
        Log.e(TAG, "refresh = " + refreshDeviceCache(mBluetoothGatt));
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (phoneName.getname().matches("OPPO")) {
            oppo = true;
            mBluetoothGatt.connect();
        }
        mBluetoothGatt.disconnect();
        close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        // This is specific to Heart Rate Measurement.
        if (UUID.fromString(HEART_RATE_MEASUREMENT).equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;
        return mBluetoothGatt.getServices();
    }

    public boolean refreshDeviceCache(BluetoothGatt gatt) {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod(
                        "refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(
                            localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                Log.e(TAG, "An exception occured while refreshing device");
            }
        }
        return false;
    }
}