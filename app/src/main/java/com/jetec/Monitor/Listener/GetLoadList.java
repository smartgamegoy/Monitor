package com.jetec.Monitor.Listener;

public class GetLoadList {

    public LoadListListener loadListListener;

    public void setListener(LoadListListener mLoadListListener){
        loadListListener = mLoadListListener;
    }

    public void readytointent(String savelist) {
        if(loadListListener != null && savelist != null){
            loadListListener.update(savelist);
        }
    }
}
