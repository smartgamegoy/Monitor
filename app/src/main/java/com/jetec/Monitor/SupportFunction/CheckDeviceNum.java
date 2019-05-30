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
            if (data.substring(data.indexOf("+") + 1).matches("0000.0")) {
                num = "Off";
            } else if (data.substring(data.indexOf("+") + 1).matches("0001.0")) {
                num = "On";
            } else {
                num = "Off";
            }
        } else if (data.startsWith("EH") || data.startsWith("EL")) {
            if (data.contains("+")) {
                if (data.substring(data.indexOf("+") + 1).matches("0000.0")) {
                    num = "0";
                } else {
                    String newStr = data.substring(data.indexOf("+") + 1)
                            .replaceFirst("^0*", "");
                    if (newStr.startsWith(".")) {
                        num = "0" + newStr;
                    } else {
                        num = newStr;
                    }
                }
            } else if (data.contains("-")) {
                String newStr = data.substring(data.indexOf("-") + 1)
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "-" + "0" + newStr;
                } else {
                    num = "-" + newStr;
                }
            }
        } else if (data.startsWith("PV")) {
            if (data.contains("+")) {
                if (data.substring(data.indexOf("+") + 1).matches("0000.0")) {
                    num = "0";
                } else {
                    String newStr = data.substring(data.indexOf("+") + 1)
                            .replaceFirst("^0*", "");
                    if (newStr.startsWith(".")) {
                        num = "0" + newStr;
                    } else {
                        num = newStr;
                    }
                }
            } else if (data.contains("-")) {
                String newStr = data.substring(data.indexOf("-") + 1)
                        .replaceFirst("^0*", "");
                logMessage.showmessage(TAG, "newStr = " + newStr);
                if (newStr.startsWith(".")) {
                    num = "-" + "0" + newStr;
                } else {
                    num = "-" + newStr;
                }
            }
        } else if (data.startsWith("ADR")) {
            if (data.substring(data.indexOf("+") + 1).matches("0000.0")) {
                num = "0";
            } else {
                String newStr = data.substring(data.indexOf("+") + 1)
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "0" + newStr;
                } else {
                    num = newStr;
                }
            }
        } else if (data.startsWith("RL")) {
            if (data.substring(data.indexOf("+") + 1).matches("0000.0")) {
                num = "0";
            } else {
                String newStr = data.substring(data.indexOf("+") + 1)
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "0" + newStr;
                } else {
                    num = newStr;
                }
            }
        } else if (data.startsWith("PR")) {
            if (data.substring(data.indexOf("+") + 1).matches("0000.0")) {
                num = "0";
            } else {
                String newStr = data.substring(data.indexOf("+") + 1)
                        .replaceFirst("^0*", "");
                if (newStr.startsWith(".")) {
                    num = "0" + newStr;
                } else {
                    num = newStr;
                }
            }
        } else {
            if (data.startsWith("INTER")) {
                data = data.substring(data.indexOf("INTER") + 5);
                int i = Integer.valueOf(data);
                if (i == 3600)
                    num = "1h";
                else if (60 <= i && i < 3600) {
                    int j = i / 60;
                    int k = i % 60;
                    if (k == 0)
                        num = String.valueOf(j) + "m";
                    else
                        num = String.valueOf(j) + "m" + String.valueOf(k) + "s";
                } else
                    num = String.valueOf(i) + "s";
            }
        }

        return num;
    }
}
