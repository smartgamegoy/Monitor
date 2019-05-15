package com.jetec.Monitor.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jetec.Monitor.DialogFunction.*;
import com.jetec.Monitor.EditManagert.EditChangeName;
import com.jetec.Monitor.EditManagert.EditChangeNum;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SupportFunction.Value;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class InputDialog {

    private Context context;
    private String TAG = "InputDialog";
    private Dialog processing = null;
    private LogMessage logMessage = new LogMessage();
    private List<String> nameList;
    private ArrayList<String> selectItem, deviceNumList;
    private PR pr = new PR();
    private EH eh = new EH();
    private EL el = new EL();
    private ADR adr = new ADR();

    public InputDialog() {
        super();
        nameList = new ArrayList<>();
        nameList.clear();
        for (int i = 0; i < Value.model_name.length(); i++) {
            nameList.add(String.valueOf(Value.model_name.charAt(i)));
        }
    }

    public void setAlert(Context context, BluetoothLeService mBluetoothLeService, String chose,
                         String title, ArrayList<String> selectItem, ArrayList<String> deviceNumList) {
        this.selectItem = selectItem;
        this.deviceNumList = deviceNumList;
        processing = input(context, mBluetoothLeService, chose, title);
        processing.show();
        processing.setCanceledOnTouchOutside(false);
    }

    private Dialog input(Context context, BluetoothLeService mBluetoothLeService, String chose, String title) {
        SendValue sendValue = new SendValue(mBluetoothLeService);
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.alterdialog, null);

        ConstraintLayout layout = v.findViewById(R.id.input_dialog);
        TextView textView = v.findViewById(R.id.textView1);
        Button by = v.findViewById(R.id.button2);
        Button bn = v.findViewById(R.id.button1);
        final EditText editText = v.findViewById(R.id.editText1);
        textView.setText(title);
        by.setText(context.getString(R.string.butoon_yes));
        bn.setText(context.getString(R.string.butoon_no));

        if (chose.contains("NAME")) {
            editText.setHint(" " + context.getString(R.string.changename));
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.addTextChangedListener(new EditChangeName(editText));
        } else if (chose.contains("EH") || chose.contains("EL")) {
            int get = Integer.valueOf(String.valueOf(chose.charAt(2)));
            if (nameList.get(get - 1).matches("T")) {
                editText.setHint(" - 10 ~ 65");
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED |
                        InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(new EditChangeNum(editText, "T"));
            } else if (nameList.get(get - 1).matches("H")) {
                editText.setHint(" 0 ~ 100");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "H"));
            } else if (nameList.get(get - 1).matches("C")) {
                editText.setHint(" 0 ~ 2000");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "C"));
            } else if (nameList.get(get - 1).matches("D")) {
                editText.setHint(" 0 ~ 3000");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "D"));
            } else if (nameList.get(get - 1).matches("E")) {
                editText.setHint(" 0 ~ 5000");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "E"));
            } else if (nameList.get(get - 1).matches("M")) {
                editText.setHint(" 0 ~ 1000");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "M"));
            } else if (nameList.get(get - 1).matches("Z")) {
                editText.setHint(" 0 ~ 100");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "Z"));
            } else if (nameList.get(get - 1).matches("W")) {
                editText.setHint(" 0 ~ 24");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "W"));
            }else if (nameList.get(get - 1).matches("P")) {
                editText.setHint(" 0 ~ 1000");
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(new EditChangeNum(editText, "P"));
            }
        } else if (chose.contains("PR")) {
            editText.setHint(" 0 ~ 100");
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.addTextChangedListener(new EditChangeNum(editText, "Z"));
        } else if (chose.contains("ADR")) {
            editText.setHint(" 1 ~ 255");
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.addTextChangedListener(new EditChangeNum(editText, "ADR"));
        }

        bn.setOnClickListener(v1 -> {
            assert vibrator != null;
            vibrator.vibrate(100);
            processing.dismiss();
        });

        by.setOnClickListener(v12 -> {
            assert vibrator != null;
            vibrator.vibrate(100);
            String gets = editText.getText().toString().trim();
            logMessage.showmessage(TAG, "chose = " + chose);
            if (!gets.matches("") && !gets.matches("-")) {
                if(chose.contains("NAME")){
                    String out = "NAME" + gets;
                    sendValue.send(out);
                    processing.dismiss();
                }else if(chose.contains("EH")){
                    float t = Float.valueOf(gets);
                    int i = selectItem.indexOf(chose);
                    logMessage.showmessage(TAG, "selectItem = " + selectItem);
                    String value = deviceNumList.get(i + 1);
                    Float min = Float.valueOf(value);
                    if (t > min) {
                        eh.todo(t, chose, processing, mBluetoothLeService, gets, nameList);
                    } else {
                        Toast.makeText(context, context.getString(R.string.max), Toast.LENGTH_SHORT).show();
                    }
                }else if(chose.contains("EL")){
                    float t = Float.valueOf(gets);
                    int i = selectItem.indexOf(chose);
                    String value = deviceNumList.get(i - 1);
                    Float max = Float.valueOf(value);
                    if (t < max) {
                        el.todo(t, chose, processing, mBluetoothLeService, gets, nameList);
                    } else {
                        Toast.makeText(context, context.getString(R.string.min), Toast.LENGTH_SHORT).show();
                    }
                }else if(chose.contains("PR")){
                    float t = Float.valueOf(gets);
                    pr.todo(t, chose, processing, mBluetoothLeService);
                }
                else if(chose.contains("ADR")){
                    float t = Float.valueOf(gets);
                    adr.todo(t, chose, processing, mBluetoothLeService);
                }
            } else {
                Toast.makeText(context, context.getString(R.string.wrong), Toast.LENGTH_SHORT).show();
            }
        });

        if (dm.heightPixels > dm.widthPixels) {
            progressDialog.setContentView(layout, new ConstraintLayout.LayoutParams((3 * dm.widthPixels / 5),
                    (dm.heightPixels / 4)));
        } else {
            progressDialog.setContentView(layout, new ConstraintLayout.LayoutParams((2 * dm.widthPixels / 5),
                    (2 * dm.heightPixels / 5)));
        }

        return progressDialog;
    }
}
