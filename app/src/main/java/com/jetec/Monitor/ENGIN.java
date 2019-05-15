package com.jetec.Monitor;

import android.content.Context;

public class ENGIN {

    private Context context;

    public ENGIN(Context context) {
        this.context = context;
    }

    public String todo(Float t, String name, String gets){
        String value = "";
        if (t == 0.0) {
            String out = name + "+" + "0000.0";
            value = out;
        } else {
            if (gets.startsWith("-")) {
                gets = String.valueOf(t);
                int i = gets.indexOf(".");
                String num1 = gets.substring(1, gets.indexOf("."));
                String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
                StringBuilder set = new StringBuilder("0");
                if (i <= 4) {
                    for (int j = 0; j < (4 - i); j++)
                        set.append("0");
                    String out = name + "-" + set + num1 + num2;
                    value = out;
                } else {
                    String out = name + "-" + num1 + num2;
                    value = out;
                }
            } else {
                gets = String.valueOf(t);
                int i = gets.indexOf(".");
                String num1 = gets.substring(0, gets.indexOf("."));
                String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
                StringBuilder set = new StringBuilder("0");
                if (i != 4) {
                    for (int j = 1; j < (4 - i); j++)
                        set.append("0");
                    String out = name + "+" + set + num1 + num2;
                    value = out;
                } else {
                    String out = name + "+" + num1 + num2;
                    value = out;
                }
            }
        }
        return value;
    }
}