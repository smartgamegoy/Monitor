package com.jetec.Monitor.SwitchWL.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SwitchWL.EditListener.EditSwitchName;

import java.nio.charset.StandardCharsets;

public class SwitchNameDialog {

    private String TAG = "NameDialog";
    private LogMessage logMessage = new LogMessage();
    private Parase parase = new Parase();

    public SwitchNameDialog(){
        super();
    }

    public void setDialog(Context context, Vibrator vibrator, SendValue sendValue, String str){
        Dialog progressDialog = showDialog(context, vibrator, sendValue, str);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @SuppressLint("SetTextI18n")
    private Dialog showDialog(Context context, Vibrator vibrator, SendValue sendValue, String str) {
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.alterdialog, null);
        TextView title = v.findViewById(R.id.textView1);
        Button by = v.findViewById(R.id.button2);
        Button bn = v.findViewById(R.id.button1);
        final EditText editText = v.findViewById(R.id.editText1);
        title.setText(context.getString(R.string.switchname));
        editText.setHint(R.string.switchname);
        by.setText(context.getString(R.string.butoon_yes));
        bn.setText(context.getString(R.string.butoon_no));

        editText.setHint(context.getString(R.string.changename));
        editText.addTextChangedListener(new EditSwitchName(editText));

        by.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            String text = editText.getText().toString().trim();
            byte[] head = parase.hex2Byte(str);
            byte[] name = text.getBytes(StandardCharsets.UTF_8);
            if (name.length != 18) {
                byte[] check = new byte[18];
                for (int i = 0; i < 18; i++) {
                    if (i < name.length) {
                        check[i] = name[i];
                    } else {
                        check[i] = 0x00;
                    }
                }
                name = check;
            }
            byte[] ready = new byte[head.length + name.length];
            System.arraycopy(head, 0, ready, 0, head.length);
            System.arraycopy(name, 0, ready, head.length, name.length);
            StringBuilder hex = new StringBuilder(ready.length * 2);
            for (byte aData : ready) {
                hex.append(String.format("%02X", aData));
            }
            String gethex = hex.toString();
            logMessage.showmessage(TAG, "devicename = " + gethex);
            if(!text.matches("")) {
                sendValue.sendbyte(ready);
                progressDialog.dismiss();
            }
        });

        bn.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            progressDialog.dismiss();
        });

        if (dm.heightPixels > dm.widthPixels) { //需修改
            progressDialog.setContentView(v, new LinearLayout.LayoutParams(2 * dm.widthPixels / 3,
                    dm.heightPixels / 3));
        } else {
            progressDialog.setContentView(v, new LinearLayout.LayoutParams(dm.widthPixels / 3,
                    dm.heightPixels / 2));
        }

        return progressDialog;
    }
}
