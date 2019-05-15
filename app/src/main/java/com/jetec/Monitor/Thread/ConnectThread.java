package com.jetec.Monitor.Thread;

import android.os.Handler;
import android.os.Message;

public class ConnectThread extends Thread{

    private Handler handler;

    public ConnectThread(Handler handler_remote_connec){
        this.handler = handler_remote_connec;
    }

    public void run() {
        Message message = new Message();
        message.obj= 2;
        handler.sendMessage(message);
    }
}
