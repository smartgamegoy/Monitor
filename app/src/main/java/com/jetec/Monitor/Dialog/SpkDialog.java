package com.jetec.Monitor.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.SendValue;

import static android.content.Context.VIBRATOR_SERVICE;

public class SpkDialog {

    private String TAG = "SpkDialog";
    private LogMessage logMessage = new LogMessage();
    private Dialog processing = null;
    private String set;

    public SpkDialog() {
        super();
    }

    public void setAlert(Context context, BluetoothLeService mBluetoothLeService, String chose,
                         String title, String num) {
        processing = input(context, mBluetoothLeService, chose, title, num);
        processing.show();
        processing.setCanceledOnTouchOutside(false);
    }

    private Dialog input(Context context, BluetoothLeService mBluetoothLeService, String chose,
                         String title, String num) {
        SendValue sendValue = new SendValue(mBluetoothLeService);
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.spkdialog, null);

        ConstraintLayout layout = v.findViewById(R.id.input_dialog);
        TextView textView = v.findViewById(R.id.textView1);
        Button by = v.findViewById(R.id.button2);
        Button bn = v.findViewById(R.id.button1);
        final Switch switch1 = v.findViewById(R.id.switch1);
        textView.setText(title);
        by.setText(context.getString(R.string.butoon_yes));
        bn.setText(context.getString(R.string.butoon_no));

        if (num.matches("On")) {
            switch1.setChecked(true);
            set = chose + "+0001.0";
        }
        else {
            switch1.setChecked(false);
            set = chose + "+0000.0";
        }

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO Auto-generated method stub
            vibrator.vibrate(100);
            if (isChecked) {
                set = chose + "+0001.0";
            } else {
                set = chose + "+0000.0";
            }
        });

        by.setOnClickListener(v1 -> {
            assert vibrator != null;
            vibrator.vibrate(100);
            sendValue.send(set);
            logMessage.showmessage(TAG,"set = " + set);
            processing.dismiss();
        });

        bn.setOnClickListener(v1 -> {
            assert vibrator != null;
            vibrator.vibrate(100);
            processing.dismiss();
        });

        if(dm.heightPixels > dm.widthPixels) {
            progressDialog.setContentView(layout, new ConstraintLayout.LayoutParams((3 * dm.widthPixels / 5),
                    (dm.heightPixels / 4)));
        }
        else {
            progressDialog.setContentView(layout, new ConstraintLayout.LayoutParams((2 * dm.widthPixels / 5),
                    (2 * dm.heightPixels / 5)));
        }

        return progressDialog;
    }
}
