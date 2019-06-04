package com.jetec.Monitor.Handler;

import android.os.Handler;
import com.jetec.Monitor.Listener.GetDownloadLog;
import com.jetec.Monitor.SupportFunction.LogMessage;

public class DownloadHandler extends Handler {

    private String TAG = "DownloadHandler";
    private LogMessage logMessage = new LogMessage();
    private GetDownloadLog getDownloadLog;

    public DownloadHandler(){
        super();
    }

    public Handler startHandler(GetDownloadLog getDownloadLog){
        this.getDownloadLog = getDownloadLog;
        return new Handler(msg -> {
            String message = msg.obj.toString();
            logMessage.showmessage(TAG,"message = " + message);
            getDownloadLog.creatdialog();
            stopHandler();
            return true;
        });
    }

    public void stopHandler() {
        startHandler(getDownloadLog).removeCallbacksAndMessages(null);
    }
}
