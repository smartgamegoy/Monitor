package com.jetec.Monitor.Listener;

import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;

import java.util.ArrayList;

public class GetDownloadLog {

    private DownloadLogListener downloadLogListener;
    private LogMessage logMessage = new LogMessage();
    private Parase parase = new Parase();
    private String TAG = "GetDownloadLog";
    private ArrayList<String> logdata;

    public void setListener(DownloadLogListener mDownloadLogListener) {
        logdata = new ArrayList<>();
        logdata.clear();
        downloadLogListener = mDownloadLogListener;
    }

    public void clearList() {
        logdata.clear();
    }

    public void addLogList(String log) {
        logdata.add(log);
        logMessage.showmessage(TAG, "logdata.size = " + logdata.size());
    }

    public void getValue() {
        new Thread(saveData).start();
        logMessage.showmessage(TAG, "不動?");
}

    private Runnable saveData = new Runnable() {
        @Override
        public void run() {
            logMessage.showmessage(TAG,"動? = ");
            String format = logdata.get(0);
            String count = format.substring(6, 8);
            logMessage.showmessage(TAG,"count = " + count);
            int calculate = parase.byteToInt(parase.hex2Byte(count)) / 5;
            logMessage.showmessage(TAG,"calculate = " + calculate);
            for (int i = 0; i < calculate; i++) {
                ArrayList<String> newList = new ArrayList<>();
                newList.clear();
                for (int j = 0; j < logdata.size(); j++) {
                    String data = logdata.get(j).substring(8 + (i * 10), 18 + (i * 10));
                    int point = Integer.valueOf(data.substring(0, 2));
                    int value = parase.byteToInt(parase.hex2Byte(data.substring(2)));
                    String datavalue = getString(point, value);
                    logMessage.showmessage(TAG,"value = " + value);
                    newList.add(datavalue);
                }

            }
        }
    };

    private String getString(int point, int value) {
        String datavalue = "";
        double p = Math.pow(10, point);
        double data = value / p;
        datavalue = reset(String.valueOf(data));
        return datavalue;
    }

    private String reset(String data) {
        String value = "";
        if (data.contains(".")) {
            logMessage.showmessage(TAG, "value = " + data.substring(data.indexOf(".") + 1, data.indexOf(".") + 2).matches("0"));
            logMessage.showmessage(TAG, "value = " + data.substring(0, data.indexOf(".") + 1));
            if(data.substring(data.indexOf(".") + 1, data.indexOf(".") + 2).matches("0")){
                value = data.substring(0, data.indexOf(".") + 1);
                logMessage.showmessage(TAG, "value = " + value);
            }else {
                value = data;
            }
        } else {
            value = data;
        }
        return value;
    }
}
