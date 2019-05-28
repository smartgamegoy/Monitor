package com.jetec.Monitor.SupportFunction;

import com.jetec.Monitor.Service.BluetoothLeService;
import java.io.UnsupportedEncodingException;

public class SendValue {

    private BluetoothLeService bluetoothLeService;
    private LogMessage logMessage = new LogMessage();

    public SendValue(BluetoothLeService bluetoothLeService){
        this.bluetoothLeService = bluetoothLeService;
    }

    public void send(String str){
        byte[] sends;
        try {
            String TAG = "SendValue";
            logMessage.showmessage(TAG, "bluetoothLeService = " + bluetoothLeService);
            sends = str.getBytes("UTF-8");
            logMessage.showmessage(TAG, "sends = " + str);
            bluetoothLeService.writeRXCharacteristic(sends);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendbyte(byte[] strValue){
        bluetoothLeService.writeRXCharacteristic(strValue);
    }
}
