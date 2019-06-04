package com.jetec.Monitor.SupportFunction;

import org.json.JSONArray;

import java.util.ArrayList;

public class Value {
    public static boolean connected = false;
    public static boolean downlog = false;
    public static boolean catchL = false;
    public static boolean downloading = false;
    public static int model_num;
    public static String model_name, device_name;
    public static int model_relay;
    public static ArrayList<String> modelList, saveLoglIst;
    public static String device;
    public static String Service_uuid;
    public static String Characteristic_uuid_TX;
    public static String Characteristic_uuid_RX;
    public static String saveDate;
    public static String saveTime;
    public static String saveInter;
    public final static String E_word = "@JETEC";

}