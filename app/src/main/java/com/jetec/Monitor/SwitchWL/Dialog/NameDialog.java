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
import com.jetec.Monitor.EditManagert.EditChangeName;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.SendValue;

public class NameDialog {

    private String TAG = "NameDialog";

    public NameDialog(){
        super();
    }

    public void setDialog(Context context, Vibrator vibrator, SendValue sendValue){
        Dialog progressDialog = showDialog(context, vibrator, sendValue);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @SuppressLint("SetTextI18n")
    private Dialog showDialog(Context context, Vibrator vibrator, SendValue sendValue) {
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
        title.setText(context.getString(R.string.device_name));
        by.setText(context.getString(R.string.butoon_yes));
        bn.setText(context.getString(R.string.butoon_no));

        editText.setHint(context.getString(R.string.changename));
        editText.addTextChangedListener(new EditChangeName(editText));

        by.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            String text = editText.getText().toString().trim();
            if(!text.matches("")) {
                String devicename = "NAME" + text;
                sendValue.send(devicename);
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
