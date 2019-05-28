package com.jetec.Monitor.SwitchWL.Listener;

public class GetBlueService {

    private BlueServiceListener blueServiceListener;

    public void setListener(BlueServiceListener mBlueServiceListener){
        blueServiceListener = mBlueServiceListener;
    }

    public void getbluetooth(){
        if(blueServiceListener != null){
            blueServiceListener.stay();
        }
    }
}
