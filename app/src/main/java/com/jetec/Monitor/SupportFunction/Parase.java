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

    public byte[] hex2Byte(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i=0 ; i < bytes.length ; i++)
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        return bytes;
    }

    public String[] hexstring(byte[] revalue){
        int i = 0;
        String[] hex = new String[revalue.length];
        //StringBuilder sb = new StringBuilder();
        for (byte b : revalue) {
            hex[i] = String.format("%02X", b);
            //sb.append(String.format("%02X ", b));
            i++;
        }
        return hex;
    }

    public int byteToInt(byte[] b) {    //只有正數
        if (b.length == 4)
            return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
                    | (b[3] & 0xff);
        else if(b.length == 3)
            return b[0] << 16 | (b[1] & 0xff) << 8 | (b[2] & 0xff);
        else if (b.length == 2)
            return (b[0] & 0xff) << 8 | (b[1] & 0xff);
        else
            return b[0] & 0xff;
    }

    public int byteToRealInt(byte[] b) {    //含有負數
        if (b.length == 4)
            return b[0] << 24 | (b[1]) << 16 | (b[2]) << 8
                    | (b[3]);
        else if(b.length == 3)
            return b[0] << 16 | (b[1]) << 8 | (b[2]);
        else if (b.length == 2)
            return (b[0]) << 8 | (b[1]);
        else
            return b[0];
    }

    public int byteToInt(byte b) {
        return b & 0xFF;
    }

    public byte[] intToByteArray(int a) {
        byte[] ret = new byte[3];
        ret[2] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[0] = (byte) ((a >> 16) & 0xFF);
        return ret;
    }
}
