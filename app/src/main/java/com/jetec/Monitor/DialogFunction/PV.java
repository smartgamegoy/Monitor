package com.jetec.Monitor.DialogFunction;

import android.app.Dialog;

import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.SendValue;

import java.util.List;

public class PV {

    public PV() {
        super();
    }

    public void todo(Float t, String name, Dialog inDialog, BluetoothLeService bluetoothLeService,
                     String gets, List<String> nameList) {
        SendValue sendValue = new SendValue(bluetoothLeService);
        if (name.matches("PV1")) {
            if (nameList.get(0).matches("T")) {
                if (t == 0.0) {
                    String out = name + "+" + "0000.0";
                    sendValue.send(out);
                    inDialog.dismiss();
                } else {
                    if (gets.startsWith("-")) {
                        gets = String.valueOf(t);
                        int i = gets.indexOf(".");
                        String num1 = gets.substring(1, gets.indexOf("."));
                        String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
                        StringBuilder set = new StringBuilder("0");
                        if (i != 4) {
                            for (int j = 0; j < (4 - i); j++)
                                set.append("0");
                            String out = name + "-" + set + num1 + num2;
                            sendValue.send(out);
                        } else {
                            String out = name + "-" + "0" + num1 + num2;
                            sendValue.send(out);
                        }
                        inDialog.dismiss();
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
                            sendValue.send(out);
                        } else {
                            String out = name + "+" + num1 + num2;
                            sendValue.send(out);
                        }
                        inDialog.dismiss();
                    }
                }
            } else {
                if (t == 0.0) {
                    String out = name + "+" + "0000.0";
                    sendValue.send(out);
                    inDialog.dismiss();
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
                        sendValue.send(out);
                    } else {
                        String out = name + "+" + num1 + num2;
                        sendValue.send(out);
                    }
                    inDialog.dismiss();
                }
            }
        } else if (name.matches("PV2")) {
            if (nameList.get(1).matches("T")) {
                if (t == 0.0) {
                    String out = name + "+" + "0000.0";
                    sendValue.send(out);
                    inDialog.dismiss();
                } else {
                    if (gets.startsWith("-")) {
                        gets = String.valueOf(t);
                        int i = gets.indexOf(".");
                        String num1 = gets.substring(1, gets.indexOf("."));
                        String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
                        StringBuilder set = new StringBuilder("0");
                        if (i != 4) {
                            for (int j = 0; j < (4 - i); j++)
                                set.append("0");
                            String out = name + "-" + set + num1 + num2;
                            sendValue.send(out);
                        } else {
                            String out = name + "-" + "0" + num1 + num2;
                            sendValue.send(out);
                        }
                        inDialog.dismiss();
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
                            sendValue.send(out);
                        } else {
                            String out = name + "+" + num1 + num2;
                            sendValue.send(out);
                        }
                        inDialog.dismiss();
                    }
                }
            } else {
                if (t == 0.0) {
                    String out = name + "+" + "0000.0";
                    sendValue.send(out);
                    inDialog.dismiss();
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
                        sendValue.send(out);
                    } else {
                        String out = name + "+" + num1 + num2;
                        sendValue.send(out);
                    }
                    inDialog.dismiss();
                }
            }
        } else if (name.matches("PV3")) {
            if (nameList.get(2).matches("T")) {
                if (t == 0.0) {
                    String out = name + "+" + "0000.0";
                    sendValue.send(out);
                    inDialog.dismiss();
                } else {
                    if (gets.startsWith("-")) {
                        gets = String.valueOf(t);
                        int i = gets.indexOf(".");
                        String num1 = gets.substring(1, gets.indexOf("."));
                        String num2 = gets.substring(gets.indexOf("."), gets.indexOf(".") + 2);
                        StringBuilder set = new StringBuilder("0");
                        if (i != 4) {
                            for (int j = 0; j < (4 - i); j++)
                                set.append("0");
                            String out = name + "-" + set + num1 + num2;
                            sendValue.send(out);
                        } else {
                            String out = name + "-" + "0" + num1 + num2;
                            sendValue.send(out);
                        }
                        inDialog.dismiss();
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
                            sendValue.send(out);
                        } else {
                            String out = name + "+" + num1 + num2;
                            sendValue.send(out);
                        }
                        inDialog.dismiss();
                    }
                }
            } else {
                if (t == 0.0) {
                    String out = name + "+" + "0000.0";
                    sendValue.send(out);
                    inDialog.dismiss();
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
                        sendValue.send(out);
                    } else {
                        String out = name + "+" + num1 + num2;
                        sendValue.send(out);
                    }
                    inDialog.dismiss();
                }
            }
        }
    }
}
