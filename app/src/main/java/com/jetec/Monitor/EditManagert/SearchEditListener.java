package com.jetec.Monitor.EditManagert;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class SearchEditListener implements TextWatcher {

    private String TAG = "SearchEditListener";
    private EditText editText;
    private String name;
    private boolean last;

    public SearchEditListener() {
        super();
    }

    public void setValue(EditText editText, String name){
        this.editText = editText;
        this.name = name;
        Log.e(TAG, "name = " + name);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void afterTextChanged(Editable editable) {
        String num = editText.getText().toString().trim();
        Log.e(TAG, "num = " + num);
        if (name.matches("0")) { //1~8均為壓力，單位不同，尚無給定範圍，均暫時給定100
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("1")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("2")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("3")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("4")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("5")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("6")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("7")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("8")) {
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("9")) {  //攝氏
            setSize(editable, num, (float) 65.0, (float) -10.0);
        } else if (name.matches("10")) { //華氏
            setSize(editable, num, (float) 149.0, (float) 14.0);
        } else if (name.matches("11")) { //濕度
            setSize(editable, num, (float) 100.0, (float) 0.0);
        } else if (name.matches("12")) { //一氧化碳，因不清楚最大範圍多少，先行給定1000
            setSize(editable, num, (float) 1000.0, (float) 0.0);
        } else if (name.matches("13")) { //二氧化碳，因不清楚最大範圍多少，先行給定2000
            setSize(editable, num, (float) 2000.0, (float) 0.0);
        } else if (name.matches("14")) { //PM2.5
            setSize(editable, num, (float) 1000.0, (float) 0.0);
        } else if (name.matches("15")) { //比例
            setSize(editable, num, (float) 100.0, (float) 0.0);
        }
    }

    private void setSize(Editable editable, String num, float big, float small) {
        if (!num.equals("-") && !num.equals("")) {
            if (num.equals(".")) {
                last = true;
                editText.setText("0.");
            } else if (num.matches("-\\.") && !num.matches("-0")) {
                last = true;
                if (num.equals("-."))
                    editText.setText("-0.");
            } else {
                if (Float.valueOf(num) > big) {
                    last = true;
                    editText.setText(String.valueOf((int) big));
                } else if (Float.valueOf(num) < small) {
                    last = true;
                    editText.setText(String.valueOf((int) small));
                }
                if (last) {
                    last = false;
                    int selEndIndex = editText.getText().length();
                    Selection.setSelection(editable, selEndIndex);
                }
            }
        }
    }
}
