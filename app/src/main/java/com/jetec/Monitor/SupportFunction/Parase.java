package com.jetec.Monitor.SupportFunction;

public class Parase {

    LogMessage logMessage = new LogMessage();

    public Parase(){
        super();
    }

    public String paraseString(String getstr){
        String str = "0123456789ABCDEF";
        char[] hexs = getstr.toCharArray();
        byte[] bytes = new byte[getstr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }

        return new String(bytes);
    }
}
