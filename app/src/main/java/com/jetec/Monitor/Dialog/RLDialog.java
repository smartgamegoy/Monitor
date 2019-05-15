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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SupportFunction.Value;
import java.util.ArrayList;

import static android.content.Context.VIBRATOR_SERVICE;

public class RLDialog {

    private String TAG = "RLDialog";
    private LogMessage logMessage = new LogMessage();
    private ArrayList<String> nameList;
    private Dialog processing = null;
    private Context context;
    private int select;

    public RLDialog(Context context) {
        this.context = context;
        nameList = new ArrayList<>();
        nameList.clear();
        nameList.add(context.getString(R.string.none));
        for (int i = 0; i < Value.model_name.length(); i++) {
            nameList.add(setString(String.valueOf(Value.model_name.charAt(i))));
        }
    }

    public void setAlert(BluetoothLeService mBluetoothLeService, String chose,
                         String title) {
        processing = input(context, mBluetoothLeService, chose, title);
        processing.show();
        processing.setCanceledOnTouchOutside(false);
    }

    private Dialog input(Context context, BluetoothLeService mBluetoothLeService, String chose,
                         String title) {
        SendValue sendValue = new SendValue(mBluetoothLeService);
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.rldialog, null);

        ConstraintLayout layout = v.findViewById(R.id.input_dialog);
        TextView textView = v.findViewById(R.id.textView1);
        Button by = v.findViewById(R.id.button2);
        Button bn = v.findViewById(R.id.button1);
        final Spinner spinner = v.findViewById(R.id.spinner);
        textView.setText(title);
        by.setText(context.getString(R.string.butoon_yes));
        bn.setText(context.getString(R.string.butoon_no));

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(    //R.layout.spinner_style
                context, R.layout.spinner_style, nameList) {    //android.R.layout.simple_spinner_item
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return true;
                } else {
                    return true;
                }
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);    //R.layout.spinner_style
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        by.setOnClickListener(v12 -> {
            assert vibrator != null;
            vibrator.vibrate(100);
            sendValue.send(todo(chose));
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

    private String setString(String str) {
        String setstr = "";

        if (str.matches("T")) {
            setstr = context.getString(R.string.T);
        } else if (str.matches("H")) {
            setstr = context.getString(R.string.H);
        } else if (str.matches("C") || str.matches("D") || str.matches("E")) {
            setstr = context.getString(R.string.C);
        } else if (str.matches("P")) {
            setstr = context.getString(R.string.P);
        } else if (str.matches("M")) {
            setstr = context.getString(R.string.M);
        } else if (str.matches("Z")) {
            setstr = context.getString(R.string.percent);
        }else if (str.matches("W")) {
            setstr = context.getString(R.string.W);
        }

        return setstr;
    }

    private String todo(String name){
        float t = (float) select;
        String gets = String.valueOf(t);
        int i = gets.indexOf(".");
        String num1 = gets.substring(0, gets.indexOf("."));
        String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
        StringBuilder set = new StringBuilder("0");
        for (int j = 1; j < (4 - i); j++)
            set.append("0");
        logMessage.showmessage(TAG,"todo = " + name + "+" + set + num1 + num2);
        return name + "+" + set + num1 + num2;
    }
}
