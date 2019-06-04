package com.jetec.Monitor.Listener;

import android.content.Context;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;
import com.jetec.Monitor.SupportFunction.RunningLog;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Collections;

public class GetDownloadLog {

    private DownloadLogListener downloadLogListener;
    private LogMessage logMessage = new LogMessage();
    private Parase parase = new Parase();
    private String TAG = "GetDownloadLog";
    private ArrayList<String> logdata, saveList;
    private Context context;
    private RunningLog runningLog;
    private int count;

    public GetDownloadLog(Context context){
        this.context = context;
        runningLog = new RunningLog(context);
    }

    public void setListener(DownloadLogListener mDownloadLogListener) {
        logdata = new ArrayList<>();
        saveList = new ArrayList<>();
        saveList.clear();
        logdata.clear();
        downloadLogListener = mDownloadLogListener;
    }

    public void setRun(String str, int count){
        this.count = count;
        runningLog.startFlash(str);
    }

    public void clearList() {
        logdata.clear();
        saveList.clear();
    }

    public void addLogList(String log) {
        logdata.add(log);
        runningLog.setTextView(logdata.size(), count);
        logMessage.showmessage(TAG, "logdata.size = " + logdata.size());
    }

    public void getValue() {
        new Thread(saveData).start();
        logMessage.showmessage(TAG, "不動?");
    }

    private Runnable saveData = new Runnable() {
        @Override
        public void run() {
            String format = logdata.get(0);
            String count = format.substring(6, 8);
            int calculate = parase.byteToInt(parase.hex2Byte(count)) / 5;
            for (int i = 0; i < calculate; i++) {
                ArrayList<String> newList = new ArrayList<>();
                newList.clear();
                for (int j = 0; j < logdata.size(); j++) {
                    String data = logdata.get(j).substring(8 + (i * 10), 18 + (i * 10));
                    int point = Integer.valueOf(data.substring(0, 2));
                    int value = parase.byteToRealInt(parase.hex2Byte(data.substring(2)));
                    String datavalue = getString(point, value);
                    logMessage.showmessage(TAG, "datavalue = " + datavalue);
                    newList.add(datavalue);
                }
                Collections.reverse(newList);
                JSONArray jsonArray = new JSONArray(newList);
                saveList.add(jsonArray.toString());
            }
            runningLog.closeFlash();
        }
    };

    private String getString(int point, int value) {
        String datavalue;
        double p = Math.pow(10, point);
        double data = value / p;
        datavalue = reset(String.valueOf(data));
        return datavalue;
    }

    private String reset(String data) {
        String value;
        if (data.contains(".")) {
            if(data.substring(data.indexOf(".") + 1, data.indexOf(".") + 2).matches("0")){
                value = data.substring(0, data.indexOf("."));
            }else {
                value = data;
            }
        } else {
            value = data;
        }
        return value;
    }
}
