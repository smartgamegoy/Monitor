package com.jetec.Monitor.SwitchWL.Listener;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;
import com.jetec.Monitor.SwitchWL.ParaseRecord.RegetRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class StartReset {

    private ResetListListener resetListListener;
    private RegetRecord regetRecord = new RegetRecord();
    private LogMessage logMessage = new LogMessage();
    private Parase parase = new Parase();
    private Map<Integer, Integer> checkdevice;
    private Context context;
    private int key = 0;
    private String None = "4E2F41"; //"N/A"
    private static final int EBLE_MANDATA = 0xFF;//«Manufacturer Specific Data»	Bluetooth Core Specification:
    private static final int EBLE_128BitUUIDCom = 0x07;//«Complete List of 128-bit Service Class UUIDs»	Bluetooth Core Specification:
    private static final int EBLE_LOCALNAME = 0x09;//«Complete Local Name»	Bluetooth Core Specification:
    private static final int EBLE_SHORTNAME = 0x08;//«Shortened Local Name»	Bluetooth Core Specification:

    public void setListener(ResetListListener mResetListListener){
        resetListListener = mResetListListener;
    }

    @SuppressLint("UseSparseArrays")
    public void getContext(Context context){
        this.context = context;
        checkdevice = new HashMap<>();
    }

    public void startReset(){
        if(resetListListener != null){
            resetListListener.resetList();
        }
    }

    public void paraseList(List<BluetoothDevice> deviceList, List<byte[]> setrecord){
        @SuppressLint("UseSparseArrays")
        Map<Integer, List<String>> setList = new HashMap<>();
        List<BluetoothDevice> newdevicesList = new ArrayList<>();
        List<byte[]> newrecord = new ArrayList<>();
        setList.clear();
        newdevicesList.clear();
        newrecord.clear();
        for (int i = 0; i < deviceList.size(); i++) {
            Map<Integer, String> parse = regetRecord.getRecord(setrecord.get(i));
            if (parse.containsKey(EBLE_MANDATA)) {
                String tmpString = parse.get(EBLE_MANDATA);
                assert tmpString != null;
                if (tmpString.substring(0, 4).matches("2028")) {
                    newdevicesList.add(deviceList.get(i));
                    newrecord.add(setrecord.get(i));
                }
            }
        }
        setList = parseList(newdevicesList, newrecord);
        if(resetListListener != null){
            resetListListener.paraseList(setList);
        }
    }

    private Map<Integer, List<String>> parseList(List<BluetoothDevice> devices, List<byte[]> record) {
        @SuppressLint("UseSparseArrays")
        Map<Integer, List<String>> newList = new HashMap<>();
        newList.clear();
        for (int i = 0; i < devices.size(); i++) {
            int k = 0;
            List<String> list = new ArrayList<>();
            list.clear();
            Map<Integer, String> parse = regetRecord.getRecord(record.get(i));
            logMessage.showmessage(TAG, "parse = " + parse);
            if (parse.containsKey(EBLE_MANDATA)) {
                String tmpString = parse.get(EBLE_MANDATA);
                assert tmpString != null;
                if (tmpString.substring(0, 4).matches("2028")) {    //jetec company id
                    if (parse.containsKey(EBLE_LOCALNAME)) {
                        String name = parse.get(EBLE_LOCALNAME);
                        if(name != null)
                            list.add(parase.paraseString(name));
                        else
                            list.add(parase.paraseString(None));
                    }
                    list.add(devices.get(i).getAddress());
                    tmpString = tmpString.substring(6);
                    if (parse.containsKey(EBLE_128BitUUIDCom)) {
                        String getuuid = parse.get(EBLE_128BitUUIDCom);
                        assert getuuid != null;
                        String power = String.valueOf(parase.byteToRealInt(parase.hex2Byte(getuuid.substring(getuuid.length() - 4))));
                        list.add(power);
                        String type = getuuid.substring(2, 4);
                        if(type.matches("01")) {
                            String count = getuuid.substring(0, 2);
                            getuuid = getuuid.substring(4);
                            list.add(count);
                            int quantity = Integer.valueOf(count);
                            if(tmpString.length() == (2 * quantity)) {
                                for (int j = 0; j < quantity; j++) {
                                    if(getuuid.substring(j * 4, (j * 4 + 4)).matches("1100")){
                                        list.add(context.getString(R.string.switchs) + (j + 1));
                                        if(tmpString.substring(j * 2, j * 2 + 2).matches("01"))
                                            list.add("On");
                                        else
                                            list.add("Off");
                                    }
                                }
                                if (newList.size() == 0) {
                                    key = 0;
                                    newList.put(key, list);
                                    if (checkdevice.size() != 0) {
                                        checkdevice.put(key, 0);
                                    } else {
                                        checkdevice.put(0, 0);
                                    }
                                    key++;
                                } else {
                                    boolean check = false;
                                    for (int n = 0; n < devices.size(); n++) {
                                        if (newList.get(n) != null && devices.get(i).getName() != null) {
                                            if (!Objects.requireNonNull(newList.get(n)).get(1).matches(devices.get(i).getAddress())) {
                                                check = true;
                                            }
                                        }
                                    }
                                    if (check) {
                                        newList.put(key, list);
                                        key++;
                                    } else {
                                        for (int n = 0; n < key; n++) {
                                            if (newList.get(n) != null && devices.get(i).getName() != null) {
                                                if (Objects.requireNonNull(newList.get(n)).get(0).matches(devices.get(i).getName())) {
                                                    newList.put(n, list);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                logMessage.showmessage(TAG,"格式錯誤");
                            }
                        }
                    }
                }
            }
        }
        return newList;
    }
}
