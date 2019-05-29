package com.jetec.Monitor.SwitchWL.Listener;

public class GetStatus {

    private LoadListener loadListener;

    public void setListener(LoadListener mloadListener) {
        loadListener = mloadListener;
    }

    public void readytointent(String savelist) {
        if (loadListener != null && savelist != null) {
            loadListener.update(savelist);
        }
    }
}
