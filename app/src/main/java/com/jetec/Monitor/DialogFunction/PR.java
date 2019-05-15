package com.jetec.Monitor.DialogFunction;

import android.app.Dialog;

import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.SendValue;

public class PR {

    public PR() {
        super();
    }

    public void todo(Float t, String name, Dialog inDialog, BluetoothLeService bluetoothLeService) {
        SendValue sendValue = new SendValue(bluetoothLeService);
        if (t == 0.0) {
            String out = name + "+" + "0000.0";
            sendValue.send(out);
            inDialog.dismiss();
        } else {
            String gets = String.valueOf(t);
            int i = gets.indexOf(".");
            String num1 = gets.substring(0, gets.indexOf("."));
            String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
            StringBuilder set = new StringBuilder("0");
            for (int j = 1; j < (4 - i); j++)
                set.append("0");
            String out = name + "+" + set + num1 + num2;
            sendValue.send(out);
            inDialog.dismiss();
        }
    }
}
