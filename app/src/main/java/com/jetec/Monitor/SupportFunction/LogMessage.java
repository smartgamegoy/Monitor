package com.jetec.Monitor.SupportFunction;

import android.util.Log;

public class LogMessage {

    public LogMessage(){
        super();
    }

    public void showmessage(String TAG, String str){
        Log.d(TAG,str);
    }

    public void showerror(String TAG, String str){
        Log.e(TAG,str);
    }
}