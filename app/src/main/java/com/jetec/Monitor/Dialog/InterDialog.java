package com.jetec.Monitor.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.RunningFlash;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.SendValue;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class InterDialog {

    private boolean c = false;
    private RunningFlash runningFlash;
    private Handler handler;

    public InterDialog() {
        super();
    }

    public void setDialog(Context context, Vibrator vibrator, BluetoothLeService bluetoothLeService, String description) {
        runningFlash = new RunningFlash(context);
        handler = new Handler();
        Dialog progressDialog = showDialog(context, vibrator, bluetoothLeService, description);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private Dialog showDialog(Context context, Vibrator vibrator, BluetoothLeService bluetoothLeService, String description) {
        SendValue sendValue = new SendValue(bluetoothLeService);
        LayoutInflater inflater = LayoutInflater.from(context);
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.interval, null);
        ConstraintLayout constraint = v.findViewById(R.id.constraint);
        EditText e1 = v.findViewById(R.id.editText1);
        EditText e2 = v.findViewById(R.id.editText2);
        EditText e3 = v.findViewById(R.id.editText3);
        Button by = v.findViewById(R.id.button2);
        Button bn = v.findViewById(R.id.button1);

        by.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.reinterval)
                    .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                        vibrator.vibrate(100);
                        String hour, minute, second;
                        int ihour = 0, iminute = 0, isecond = 0, sum;
                        Log.e("btn", "c = " + c);
                        if (c) {
                            if (!e1.getText().toString().trim().matches("")) {
                                hour = e1.getText().toString().trim();
                                ihour = Integer.valueOf(hour);
                            }
                            if (!e2.getText().toString().trim().matches("")) {
                                minute = e2.getText().toString().trim();
                                iminute = Integer.valueOf(minute);
                            }
                            if (!e3.getText().toString().trim().matches("")) {
                                second = e3.getText().toString().trim();
                                isecond = Integer.valueOf(second);
                            }
                            sum = ihour * 3600 + iminute * 60 + isecond;
                            if (sum <= 3600 && sum >= 60) {
                                if (!runningFlash.isCheck()) {
                                    runningFlash.startFlash(context.getString(R.string.intervalset));
                                }
                                sendValue.send("END");
                                List<String> sendlist = new ArrayList<>();
                                sendlist.clear();
                                sendlist.add(String.valueOf(sum));
                                sendlist.add("START");
                                sendlist.add("END");
                                int timedelay = 500;
                                int i = 0;
                                sendliststr(sendlist, timedelay, i, bluetoothLeService, sendValue);
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.butoon_no, (dialog, which) -> {
                        vibrator.vibrate(100);
                        progressDialog.dismiss();
                    })
                    .show();
        });
        bn.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            progressDialog.dismiss();
        });

        e1.setKeyListener(DigitsKeyListener.getInstance("01"));
        e1.setHint("0 or 1");
        e1.addTextChangedListener(new TextWatcher() {
            int l = 0;    //記錄字串删除字元之前，字串的長度
            int location = 0; //記錄光標位置

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                l = s.length();
                location = e1.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = Pattern.compile("^[0-1]$");    //^\-?[0-5](.[0-9])?$
                Matcher m = p.matcher(s.toString());
                c = m.find() || ("").equals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e2.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        e2.setHint("1 ~ 59");
        e2.addTextChangedListener(new TextWatcher() {
            int l = 0;    //記錄字串删除字元之前，字串的長度
            int location = 0; //記錄光標位置

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                l = s.length();
                location = e1.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = Pattern.compile("^[1-5]?[0-9]?$");    //^\-?[0-5](.[0-9])?$
                Matcher m = p.matcher(s.toString());
                c = m.find() || ("").equals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e3.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        e3.setHint("1 ~ 59");
        e3.addTextChangedListener(new TextWatcher() {
            int l = 0;    //記錄字串删除字元之前，字串的長度
            int location = 0; //記錄光標位置

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                l = s.length();
                location = e1.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = Pattern.compile("^[1-5]?[0-9]?$");    //^\-?[0-5](.[0-9])?$
                Matcher m = p.matcher(s.toString());
                c = m.find() || ("").equals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        progressDialog.setContentView(constraint, new LinearLayout.LayoutParams((3 * dm.widthPixels / 4),
                (dm.heightPixels / 2)));

        return progressDialog;
    }

    private void sending(String value, BluetoothLeService bluetoothLeService) throws UnsupportedEncodingException {
        if (value.length() < 5) {
            int i = 5 - value.length();
            StringBuilder valueBuilder = new StringBuilder(value);
            for (int j = 0; j < i; j++) {
                valueBuilder.insert(0, "0");
            }
            value = valueBuilder.toString();
            String change = "INTER" + value;
            byte[] sends;
            sends = change.getBytes(StandardCharsets.UTF_8);
            String TAG = "Interval";
            Log.e(TAG, "sends = " + change);
            bluetoothLeService.writeRXCharacteristic(sends);
        }
    }

    private void sendliststr(List<String> sendlist, int timedelay, int i, BluetoothLeService bluetoothLeService, SendValue sendValue) {
        handler.postDelayed(() -> {
            try {
                if (i < sendlist.size()) {
                    if (i == 0) {
                        sending(sendlist.get(i), bluetoothLeService);
                    } else {
                        sendValue.send(sendlist.get(i));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            handler.removeCallbacksAndMessages(null);
            if((i + 1) == sendlist.size() - 1){
                sendliststr(sendlist, 2000,(i + 1), bluetoothLeService, sendValue);
            }
            else if((i + 1) < sendlist.size()){
                sendliststr(sendlist, timedelay,(i + 1), bluetoothLeService, sendValue);
            }else if(i == (sendlist.size() - 1)){
                runningFlash.closeFlash();
            }
        }, timedelay);
    }
}
