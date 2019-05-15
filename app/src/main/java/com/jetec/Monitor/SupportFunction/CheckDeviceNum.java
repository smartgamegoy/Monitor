package com.jetec.Monitor.SupportFunction;

public class CheckDeviceNum {

    private String TAG = "GetDeviceNum";
    private LogMessage logMessage = new LogMessage();

    public CheckDeviceNum() {
        super();
    }

    public String get(String data) {

        String num = "";

        if (data.startsWith("SPK")) {
            if (data.substring(data.indexOf("+") + 1, data.length()).matches("0000.0")) {
                num = "Off";
            } else if (data.substring(data.indexOf("+") + 1, data.length()).matches("0001.0")) {
                num = "On";
            }else {
                num = "Off";
            }
        } else if (data.startsWith("EH") || data.startsWith("EL")) {
            if (data.contains("+")) {
                if (data.substring(data.indexOf("+") + 1, data.length()).matches("0000.0")) {
                    num = "0";
                }
                else {
                    String newStr = data.substring(data.indexOf("+") + 1, data.length())
                            .replaceFirst("^0*", "");
                    if (newStr.startsWith(".")) {
                        num = "0" + newStr;
                    } else {
                        num = newStr;
                    }
                }
            } else if (data.contains("-")) {
                String newStr = data.substring(data.indexOf("-") + 1, data.length())
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "-" + "0" + newStr;
                } else {
                    num = "-" + newStr;
                }
            }
        } else if (data.startsWith("ADR")) {
            if (data.substring(data.indexOf("+") + 1, data.length()).matches("0000.0")) {
                num = "0";
            }
            else {
                String newStr = data.substring(data.indexOf("+") + 1, data.length())
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "0" + newStr;
                } else {
                    num = newStr;
                }
            }
        } else if (data.startsWith("RL")) {
            if (data.substring(data.indexOf("+") + 1, data.length()).matches("0000.0")) {
                num = "0";
            }
            else {
                String newStr = data.substring(data.indexOf("+") + 1, data.length())
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "0" + newStr;
                } else {
                    num = newStr;
                }
            }
        }else if (data.startsWith("PR")) {
            if (data.substring(data.indexOf("+") + 1, data.length()).matches("0000.0")) {
                num = "0";
            }
            else {
                String newStr = data.substring(data.indexOf("+") + 1, data.length())
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "0" + newStr;
                } else {
                    num = newStr;
                }
            }
        }

        return num;
    }
}
