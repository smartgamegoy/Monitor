package com.jetec.Monitor.SwitchWL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SwitchWL.Dialog.SwitchNameDialog;
import java.util.ArrayList;
import java.util.List;

public class SwitchView {

    private String TAG = "SwitchView";
    private LogMessage logMessage = new LogMessage();
    private Parase parase = new Parase();
    private SwitchNameDialog switchNameDialog = new SwitchNameDialog();
    private List<View> viewList;
    private List<String> itemlayout;

    public SwitchView(){
        super();
    }

    @SuppressLint("SetTextI18n")
    public void setView(Context context, Vibrator vibrator, LinearLayout linearLayout, ArrayList<String> ItemList, SendValue sendValue){
        int count = ItemList.size() - 1;
        itemlayout = new ArrayList<>();
        viewList = new ArrayList<>();
        itemlayout.clear();
        viewList.clear();
        for (int i = 0; i < count; i++){
            itemlayout.add(ItemList.get(i + 1));
        }
        for(int i = 0; i < count; i++) {
            View view = View.inflate(context, R.layout.switchclick, null);
            TextView textView1 = view.findViewById(R.id.textView1);
            textView1.setText(context.getString(R.string.switchs) + (i + 1));
            TextView textView2 = view.findViewById(R.id.textView2);
            textView2.setText(R.string.switchname);
            String buttonstr = itemlayout.get(i);
            String buttonname = buttonstr.substring(4);
            String status = buttonstr.substring(0, 4);
            logMessage.showmessage(TAG, "buttonname = " + buttonname);
            logMessage.showmessage(TAG, "status = " + status);
            Button button1 = view.findViewById(R.id.button1);
            if(buttonname.matches("000000000000000000000000000000000000")){
                button1.setText("N/A");
            }else {
                button1.setText(parase.paraseString(buttonname));
            }

            button1.setOnClickListener(v -> {
                vibrator.vibrate(100);
                switchNameDialog.setDialog(context, vibrator, sendValue, status);
            });
            Button button2 = view.findViewById(R.id.button2);
            if(status.substring(2, 4).matches("00")){
                button2.setText("Off");
                button2.setBackgroundResource(R.drawable.shape_button);
            }
            else {
                button2.setText("On");
                button2.setBackgroundResource(R.drawable.shape_button2);
            }
            button2.setOnClickListener(v -> {
                vibrator.vibrate(100);
                if(status.substring(2, 4).matches("00")){
                    String chang = "01";
                    sendbyte(status, chang, buttonname, sendValue);
                }
                else {
                    String chang = "00";
                    sendbyte(status, chang, buttonname, sendValue);
                }
            });
            linearLayout.addView(view);
            viewList.add(view);
        }

    }

    @SuppressLint("SetTextI18n")
    public void resetView(Context context, Vibrator vibrator, String buttonstr, SendValue sendValue){
        String buttonname = buttonstr.substring(4);
        String status = buttonstr.substring(0, 4);
        int row = Integer.valueOf(buttonstr.substring(0, 2));
        itemlayout.set(row - 1, buttonstr);
        View view = viewList.get(row - 1);
        TextView textView1 = view.findViewById(R.id.textView1);
        TextView textView2 = view.findViewById(R.id.textView2);
        Button button1 = view.findViewById(R.id.button1);
        textView1.setText(context.getString(R.string.switchs) + row);
        textView2.setText(R.string.switchname);
        if(buttonname.matches("000000000000000000000000000000000000")){
            button1.setText("N/A");
        }else {
            button1.setText(parase.paraseString(buttonname));
        }
        button1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            switchNameDialog.setDialog(context, vibrator, sendValue, status);
        });
        Button button2 = view.findViewById(R.id.button2);
        if(status.substring(2, 4).matches("00")){
            button2.setText("Off");
            button2.setBackgroundResource(R.drawable.shape_button);
        }
        else {
            button2.setText("On");
            button2.setBackgroundResource(R.drawable.shape_button2);
        }
        button2.setOnClickListener(v -> {
            vibrator.vibrate(100);
            if(status.substring(2, 4).matches("00")){
                String chang = "01";
                sendbyte(status, chang, buttonname, sendValue);
            }
            else {
                String chang = "00";
                sendbyte(status, chang, buttonname, sendValue);
            }
        });
    }

    private void sendbyte(String status, String change, String buttonname, SendValue sendValue){
        String row = status.substring(0, 2);
        row = row + change;
        String sendstr = row + buttonname;
        byte[] sendByte = parase.hex2Byte(sendstr);
        StringBuilder hex = new StringBuilder(sendByte.length * 2);
        for (byte aData : sendByte) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        logMessage.showmessage(TAG, "devicename = " + gethex);
        if(!sendstr.matches("")) {
            sendValue.sendbyte(sendByte);
        }
    }
}
