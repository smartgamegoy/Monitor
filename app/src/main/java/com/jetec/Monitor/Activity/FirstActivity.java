package com.jetec.Monitor.Activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.jetec.Monitor.R;
import com.jetec.Monitor.ScanRecord.DeviceParse;
import com.jetec.Monitor.SupportFunction.*;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.SQL.AlertRecord;
import com.jetec.Monitor.Thread.*;
import org.json.JSONArray;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.lang.Thread.sleep;

public class FirstActivity extends AppCompatActivity {

    private String TAG = "FirstActivity";
    private TimeCheck timeCheck = new TimeCheck();
    private static final long SCAN_PERIOD = 500; //scan time to process data
    private Vibrator vibrator;
    private Intent intents;
    private SendValue sendValue;
    private CheckDeviceName checkDeviceName = new CheckDeviceName();
    private CheckDeviceNum checkDeviceNum = new CheckDeviceNum();
    private RunningFlash runningFlash;
    private String BID;
    private String Jetec = "Jetec";
    private boolean s_connect = false, engineer = false, nextpage = false;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private View no_device;
    private ScrollView scrollView;
    private DeviceParse deviceParse;
    public List<BluetoothDevice> deviceList, checkdeviceList;
    public List<byte[]> setrecord;
    private byte[] txValue;
    private String text;
    private List<String> list = new ArrayList<>();
    private Map<Integer, List<String>> record, Record;
    private Handler mHandler, valueHandler;
    private Animation animation;
    private AlertRecord alertRecord;
    private LinearLayout linearLayout, linearLayout1, linearLayout2, linearLayout3,
            linearLayout4, linearLayout5, linearLayout6;
    private TextView text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12;
    private LogMessage logMessage = new LogMessage();
    private ArrayList<String> dataList, selectItem, reList;
    private final String[] T = {"EH", "EL"};
    private final String[] H = {"EH", "EL"};
    private final String[] C = {"EH", "EL"};
    private final String[] D = {"EH", "EL"};
    private final String[] E = {"EH", "EL"};
    private final String[] P = {"EH", "EL"};
    private final String[] M = {"EH", "EL"};
    private final String[] Z = {"EH", "EL", "PR"};  //PR比例
    private final String[] W = {"EH", "EL"};    //溢位
    private final String[] L = {"COUNT", "INTER", "DATE", "TIME", "LOG"};
    private final String[] SP = {"ADR", "SPK"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        runningFlash = new RunningFlash(this);
        alertRecord = new AlertRecord(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        BluetoothManager bluetoothManager = getManager(this);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        show_device();
    }

    private void show_device() {
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);



        no_device = findViewById(R.id.no_data);
        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.linearlayout);
        no_device.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);//VISIBLE / GONE

        device_list();
    }

    @SuppressLint("UseSparseArrays")
    private void device_list() {
        deviceList = new ArrayList<>();
        checkdeviceList = new ArrayList<>();
        setrecord = new ArrayList<>();
        record = new HashMap<>();
        Record = new HashMap<>();
        Record.clear();
        mHandler = new Handler();
        valueHandler = new Handler();
        list = new ArrayList<>();
        list.clear();
        deviceParse = new DeviceParse();
        dataList = new ArrayList<>();
        selectItem = new ArrayList<>();
        reList = new ArrayList<>();
        animation = AnimationUtils.loadAnimation(this, R.anim.animate);

        scanLeDevice();
        setList();
        settimer();
    }

    private void scanLeDevice() {
        //noinspection deprecation
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private void settimer() {
        mHandler.postDelayed(() -> {
            for (int i = 0; i < deviceList.size(); i++) {
                if (checkdeviceList.indexOf(deviceList.get(i)) == -1) {
                    //noinspection SuspiciousListRemoveInLoop
                    deviceList.remove(i);
                    //noinspection SuspiciousListRemoveInLoop
                    setrecord.remove(i);
                }
            }
            checkdeviceList.clear();
            mHandler.removeCallbacksAndMessages(null);
            settimer();
        }, 3000);
    }

    private void setList() {
        valueHandler.postDelayed(() -> {
            record = deviceParse.regetList(deviceList, setrecord);
            //record = deviceParse.parseList(deviceList, setrecord);
            if (record != null) {
                if (record.size() > 0) {
                    no_device.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    set_View(record);
                } else {
                    no_device.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }
            valueHandler.removeCallbacksAndMessages(null);
            setList();
        }, SCAN_PERIOD);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            (device, rssi, scanRecord) -> runOnUiThread(() -> runOnUiThread(() -> addDevice(device, scanRecord)));

    private void addDevice(BluetoothDevice device, byte[] scanRecord) {
        boolean deviceFound = false;
        checkdeviceList.add(device);
        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                int i = deviceList.indexOf(device);
                setrecord.set(i, scanRecord);
                break;
            }
        }
        if (!deviceFound) {
            deviceList.add(device);
            setrecord.add(scanRecord);
        }
    }

    public static BluetoothManager getManager(Context context) {    //獲取此設備默認藍芽適配器
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private void backtofirst() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void Service_close() {
        if (mBluetoothLeService == null) {
            Log.e(TAG, "service close!");
            return;
        }
        mBluetoothLeService.disconnect();
    }

    private void set_View(Map<Integer, List<String>> get_record) {
        boolean checklist = true;

        if (get_record.size() == 0) {
            Record.clear();
        }

        if (get_record.size() > 0 && get_record.size() != Record.size()) {
            Record.clear();
            checklist = false;
        }

        if (Record.size() > 0 && Record.size() == get_record.size()) {
            for (int i = 0; i < Record.size(); i++) {
                List<String> comparison = new ArrayList<>();
                comparison.clear();
                comparison = Record.get(i);
                assert comparison != null;
                if (!comparison.get(i).matches(Objects.requireNonNull(get_record.get(i)).get(i))) {
                    checklist = false;
                }
            }
        } else {
            checklist = false;
            for (int i = 0; i < get_record.size(); i++) {
                Record.put(i, Objects.requireNonNull(get_record.get(i)));
            }
        }

        if (!checklist) {
            linearLayout.removeAllViews();
            for (int i = 0; i < get_record.size(); i++) {
                View view = View.inflate(this, R.layout.search_device, null);
                TextView textView = view.findViewById(R.id.search_device);
                TextView textView2 = view.findViewById(R.id.device_address);

                linearLayout1 = view.findViewById(R.id.linearlayout1);
                linearLayout2 = view.findViewById(R.id.linearlayout2);
                linearLayout3 = view.findViewById(R.id.linearlayout3);
                linearLayout4 = view.findViewById(R.id.linearlayout4);
                linearLayout5 = view.findViewById(R.id.linearlayout5);
                linearLayout6 = view.findViewById(R.id.linearlayout6);

                text1 = view.findViewById(R.id.textView1);
                text2 = view.findViewById(R.id.textView2);
                text3 = view.findViewById(R.id.textView3);
                text4 = view.findViewById(R.id.textView4);
                text5 = view.findViewById(R.id.textView5);
                text6 = view.findViewById(R.id.textView6);
                text7 = view.findViewById(R.id.textView7);
                text8 = view.findViewById(R.id.textView8);
                text9 = view.findViewById(R.id.textView9);
                text10 = view.findViewById(R.id.textView10);
                text11 = view.findViewById(R.id.textView11);
                text12 = view.findViewById(R.id.textView12);

                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                linearLayout4.setVisibility(View.GONE);
                linearLayout5.setVisibility(View.GONE);
                linearLayout6.setVisibility(View.GONE);
                text1.setVisibility(View.GONE);
                text2.setVisibility(View.GONE);
                text3.setVisibility(View.GONE);
                text4.setVisibility(View.GONE);
                text5.setVisibility(View.GONE);
                text6.setVisibility(View.GONE);
                text7.setVisibility(View.GONE);
                text8.setVisibility(View.GONE);
                text9.setVisibility(View.GONE);
                text10.setVisibility(View.GONE);
                text11.setVisibility(View.GONE);
                text12.setVisibility(View.GONE);

                int k = 1;
                list = get_record.get(i);

                assert list != null;
                textView.setText(list.get(0));
                textView2.setText(list.get(1));
                String a = list.get(2);
                list = list.subList(list.indexOf(a) + 1, list.size());
                for (int j = 0; j < list.size(); j = j + 4) {
                    logMessage.showmessage(TAG, "k = " + k);
                    logMessage.showmessage(TAG, "list = " + list);
                    getvalue(k, list.get(j), list.get(j + 3), list.get(j + 1));
                    if (list.get(j + 3).matches("true")) {
                        saveRecord(Objects.requireNonNull(get_record.get(i)));
                    }
                    k++;
                }
                int view_id = i;

                view.setOnLongClickListener(v -> {
                    vibrator.vibrate(100);
                    engineer = true;
                    //noinspection deprecation
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    valueHandler.removeCallbacksAndMessages(null);
                    mHandler.removeCallbacksAndMessages(null);
                    BID = Objects.requireNonNull(get_record.get(view_id)).get(1);
                    Value.device_name = Objects.requireNonNull(get_record.get(view_id)).get(0);
                    Remote_connec();
                    return true;
                });

                view.setOnClickListener(v -> {
                    vibrator.vibrate(100);
                    engineer = false;
                    //noinspection deprecation
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    valueHandler.removeCallbacksAndMessages(null);
                    mHandler.removeCallbacksAndMessages(null);
                    BID = Objects.requireNonNull(get_record.get(view_id)).get(1);
                    Value.device_name = Objects.requireNonNull(get_record.get(view_id)).get(0);
                    Remote_connec();
                });
                linearLayout.addView(view);
            }
        } else {
            if (linearLayout.getChildCount() > 0) {
                for (int i = 0; i < get_record.size(); i++) {
                    View view = linearLayout.getChildAt(i);
                    linearLayout1 = view.findViewById(R.id.linearlayout1);
                    linearLayout2 = view.findViewById(R.id.linearlayout2);
                    linearLayout3 = view.findViewById(R.id.linearlayout3);
                    linearLayout4 = view.findViewById(R.id.linearlayout4);
                    linearLayout5 = view.findViewById(R.id.linearlayout5);
                    linearLayout6 = view.findViewById(R.id.linearlayout6);

                    text1 = view.findViewById(R.id.textView1);
                    text2 = view.findViewById(R.id.textView2);
                    text3 = view.findViewById(R.id.textView3);
                    text4 = view.findViewById(R.id.textView4);
                    text5 = view.findViewById(R.id.textView5);
                    text6 = view.findViewById(R.id.textView6);
                    text7 = view.findViewById(R.id.textView7);
                    text8 = view.findViewById(R.id.textView8);
                    text9 = view.findViewById(R.id.textView9);
                    text10 = view.findViewById(R.id.textView10);
                    text11 = view.findViewById(R.id.textView11);
                    text12 = view.findViewById(R.id.textView12);

                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout4.setVisibility(View.GONE);
                    linearLayout5.setVisibility(View.GONE);
                    linearLayout6.setVisibility(View.GONE);
                    text1.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                    text4.setVisibility(View.GONE);
                    text5.setVisibility(View.GONE);
                    text6.setVisibility(View.GONE);
                    text7.setVisibility(View.GONE);
                    text8.setVisibility(View.GONE);
                    text9.setVisibility(View.GONE);
                    text10.setVisibility(View.GONE);
                    text11.setVisibility(View.GONE);
                    text12.setVisibility(View.GONE);

                    int k = 1;
                    list = get_record.get(i);
                    assert list != null;
                    String a = list.get(2);
                    list = list.subList(list.indexOf(a) + 1, list.size());
                    for (int j = 0; j < list.size(); j = j + 4) {
                        setValue(k, list.get(j + 3), list.get(j + 1));
                        if (list.get(j + 3).matches("true")) {
                            saveRecord(Objects.requireNonNull(get_record.get(i)));
                        }
                        k++;
                    }
                }
            }
        }
    }

    private void Remote_connec() {
        if (!runningFlash.isCheck()) {
            runningFlash.startFlash(getString(R.string.connecting));
        }
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        s_connect = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if (s_connect) {
            logMessage.showmessage(TAG, "開始訂閱");
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        } else {
            logMessage.showmessage(TAG, "服務綁訂狀態  = " + false);
        }
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            logMessage.showmessage(TAG, "連線中");
            //https://github.com/googlesamples/android-BluetoothLeGatt/tree/master/Application/src/main/java/com/example/android/bluetoothlegatt
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                logMessage.showmessage(TAG, "初始化失敗");
            }
            mBluetoothLeService.connect(BID);
            logMessage.showmessage(TAG, "進入連線");
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            logMessage.showmessage(TAG, "失去連線端");
        }
    };

    public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intents = intent;
            final String action = intent.getAction();
            Log.d(TAG, "action = " + action);
            logMessage.showmessage(TAG, "action = " + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                logMessage.showmessage(TAG, "連線成功");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                s_connect = false;
                if (mBluetoothLeService != null)
                    unbindService(mServiceConnection);
                logMessage.showmessage(TAG, "連線中斷");
                if (Value.connected) {
                    try {
                        Service_close();
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logMessage.showmessage(TAG, "重新連線");
                    ConnectThread newThread = new ConnectThread(connectHandler);
                    newThread.run();
                } else {
                    runningFlash.closeFlash();
                    Service_close();
                }
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                logMessage.showmessage(TAG, "連線狀態改變");
                mBluetoothLeService.enableTXNotification();
                if (!Value.connected) {
                    new Thread(sendcheck).start();
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(() -> {
                    try {
                        txValue = intents.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        text = new String(txValue, "UTF-8");
                        logMessage.showmessage(TAG, "text = " + text);
                        if (text.startsWith("OK")) {
                            dataList.clear();
                            selectItem.clear();
                            reList.clear();
                        } else if (text.startsWith("BT")) {
                            String model = text;
                            Value.device = text;
                            String[] arr = model.split("-");
                            String num = arr[1];
                            String name = arr[2];
                            String relay = arr[3];
                            Value.model_num = Integer.valueOf(num);
                            Value.model_name = name;
                            Value.model_relay = Integer.valueOf(relay);
                            selectItem.add("NAME");
                            logMessage.showmessage(TAG, "model_num = " + Value.model_num);
                            logMessage.showmessage(TAG, "model_name = " + Value.model_name);
                            logMessage.showmessage(TAG, "model_relay = " + Value.model_relay);
                            ArrayList<String> newList = new ArrayList<>();
                            newList.clear();
                            for (int i = 0; i < name.length(); i++) {
                                if (name.charAt(i) == 'T') {
                                    for (String aT : T) {
                                        String str = aT + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'H') {
                                    for (String aH : H) {
                                        String str = aH + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'C') {
                                    for (String aC : C) {
                                        String str = aC + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'D') {
                                    for (String aD : D) {
                                        String str = aD + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'E') {
                                    for (String aE : E) {
                                        String str = aE + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'P') {
                                    for (String aP : P) {
                                        String str = aP + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'M') {
                                    for (String aM : M) {
                                        String str = aM + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'Z') {
                                    for (String aZ : Z) {
                                        String str = aZ + (i + 1);
                                        newList.add(str);
                                    }
                                } else if (name.charAt(i) == 'W') {
                                    for (String aW : W) {
                                        String str = aW + (i + 1);
                                        newList.add(str);
                                    }
                                }
                                /*else if(name.charAt(i) == 'I'){
                                    for(int j = 0; j < I.length; j++){
                                        String str = I[j] + (i + 1);
                                        newList.add(str);
                                    }
                                }
                                else if(name.charAt(i) == 'L'){
                                    newList.addAll(Arrays.asList(L));
                                }*/
                            }
                            for (int j = 0; j < Value.model_relay; j++) {
                                String r = "RL" + (j + 1);
                                newList.add(r);
                            }
                            newList.addAll(Arrays.asList(SP));
                            Value.modelList = newList;
                            logMessage.showmessage(TAG, "modelList = " + Value.modelList);
                            sendValue = new SendValue(mBluetoothLeService);
                            Value.connected = true;
                            sendValue.send("get");
                        } else if (text.matches("OVER")) {
                            logMessage.showmessage(TAG, "selectItem = " + selectItem);
                            logMessage.showmessage(TAG, "reList = " + reList);
                            logMessage.showmessage(TAG, "dataList = " + dataList);
                            if (Value.modelList.size() == reList.size() && Value.modelList.size() == dataList.size()) {
                                if (engineer) {
                                    checkpassword();
                                } else {
                                    device_function();
                                }
                            } else {
                                selectItem.clear();
                                reList.clear();
                                dataList.clear();
                                sendValue = new SendValue(mBluetoothLeService);
                                sendValue.send("get");
                            }
                        } else {
                            if (text.startsWith("EH") || text.startsWith("EL") || text.startsWith("RL") ||
                                    text.startsWith("ADR") || text.startsWith("SPK") || text.startsWith("PR")) {
                                selectItem.add(checkDeviceName.setName(text));
                                reList.add(text);
                                dataList.add(checkDeviceNum.get(text));
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    };

    @SuppressLint("SetTextI18n")
    private void checkpassword() {
        setContentView(R.layout.checkpassword);

        Button by = findViewById(R.id.button2);
        Button bn = findViewById(R.id.button1);
        TextView t1 = findViewById(R.id.textView3);
        EditText e1 = findViewById(R.id.editText1);

        runningFlash.closeFlash();

        t1.setText(getString(R.string.device_name) + "： " + Value.device);
        e1.setKeyListener(DigitsKeyListener.getInstance(".,$%&^!()-_=+';:|}{[]*→←↘↖、，。?~～#€￠" +
                "￡￥abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@>/<"));
        //e1.setKeyListener(DigitsKeyListener.getInstance("abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789."));
        e1.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        by.setOnClickListener(v -> {
            vibrator.vibrate(100);
            if (e1.getText().toString().length() == 6) {
                Log.e(TAG, "e1 = " + e1.getText().toString().trim());
                if (e1.getText().toString().trim().matches(Value.E_word)) {
                    engineer_function();
                }
            } else {
                Toast.makeText(this, getString(R.string.passworderror), Toast.LENGTH_SHORT).show();
            }
        });

        bn.setOnClickListener(v -> {
            vibrator.vibrate(100);
            if (Value.connected) {
                Value.connected = false;
            }
            if (mBluetoothAdapter != null)
                //noinspection deprecation
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            if (mBluetoothLeService != null) {
                if (s_connect) {
                    unbindService(mServiceConnection);
                    s_connect = false;
                }
                mBluetoothLeService.stopSelf();
                mBluetoothLeService = null;
            }
            valueHandler.removeCallbacksAndMessages(null);
            mHandler.removeCallbacksAndMessages(null);
            show_device();
        });
    }

    private void engineer_function() {
        nextpage = true;
        Intent intent = new Intent(FirstActivity.this, EngineerMode.class);

        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putStringArrayListExtra("reList", reList);
        intent.putStringArrayListExtra("dataList", dataList);
        intent.putExtra("BID", BID);

        startActivity(intent);
        finish();
    }

    private void device_function() {
        nextpage = true;
        Intent intent = new Intent(FirstActivity.this, DeviceFunction.class);

        intent.putExtra("BID", BID);
        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putStringArrayListExtra("reList", reList);
        intent.putStringArrayListExtra("dataList", dataList);

        startActivity(intent);
        runningFlash.closeFlash();
        finish();
    }

    private void getvalue(int count, String unit, String boolean_value, String text) {
        switch (count) {
            case 1: {
                linearLayout1.setVisibility(View.VISIBLE);
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.VISIBLE);
                text2.setText(text);
                if (unit.matches("0")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("1")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("2")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("3")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("4")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("5")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("6")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("7")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("8")) {
                    text1.setText(getString(R.string.pressure));
                    linearLayout1.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("9")) {
                    text1.setText(getString(R.string.temperature));
                    linearLayout1.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("10")) {
                    text1.setText(getString(R.string.temperature));
                    linearLayout1.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("11")) {
                    text1.setText(getString(R.string.humidity));
                    linearLayout1.setBackgroundResource(R.drawable.humidity);
                } else if (unit.matches("12")) {
                    text1.setText(getString(R.string.co));
                    linearLayout1.setBackgroundResource(R.drawable.co);
                } else if (unit.matches("13")) {
                    text1.setText(getString(R.string.co2));
                    linearLayout1.setBackgroundResource(R.drawable.co2);
                } else if (unit.matches("14")) {
                    text1.setText(getString(R.string.pm));
                    linearLayout1.setBackgroundResource(R.drawable.pm);
                } else if (unit.matches("15")) {
                    text1.setText(getString(R.string.percent));
                    linearLayout1.setBackgroundResource(R.drawable.humidity);
                }
                if (boolean_value.matches("true")) {
                    linearLayout1.setAnimation(animation);
                    //saveRecord();
                }
            }
            break;
            case 2: {
                linearLayout2.setVisibility(View.VISIBLE);
                text3.setVisibility(View.VISIBLE);
                text4.setVisibility(View.VISIBLE);
                text4.setText(text);
                if (unit.matches("0")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("1")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("2")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("3")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("4")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("5")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("6")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("7")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("8")) {
                    text3.setText(getString(R.string.pressure));
                    linearLayout2.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("9")) {
                    text3.setText(getString(R.string.temperature));
                    linearLayout2.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("10")) {
                    text3.setText(getString(R.string.temperature));
                    linearLayout2.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("11")) {
                    text3.setText(getString(R.string.humidity));
                    linearLayout2.setBackgroundResource(R.drawable.humidity);
                } else if (unit.matches("12")) {
                    text3.setText(getString(R.string.co));
                    linearLayout2.setBackgroundResource(R.drawable.co);
                } else if (unit.matches("13")) {
                    text3.setText(getString(R.string.co2));
                    linearLayout2.setBackgroundResource(R.drawable.co2);
                } else if (unit.matches("14")) {
                    text3.setText(getString(R.string.pm));
                    linearLayout2.setBackgroundResource(R.drawable.pm);
                } else if (unit.matches("15")) {
                    text3.setText(getString(R.string.percent));
                    linearLayout2.setBackgroundResource(R.drawable.humidity);
                }
                if (boolean_value.matches("true")) {
                    linearLayout2.setAnimation(animation);
                    //saveRecord();
                }
            }
            break;
            case 3: {
                logMessage.showmessage(TAG, "近來");
                logMessage.showmessage(TAG, "unit = " + unit);
                linearLayout3.setVisibility(View.VISIBLE);
                text5.setVisibility(View.VISIBLE);
                text6.setVisibility(View.VISIBLE);
                text6.setText(text);
                if (unit.matches("0")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("1")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("2")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("3")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("4")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("5")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("6")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("7")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("8")) {
                    text5.setText(getString(R.string.pressure));
                    linearLayout3.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("9")) {
                    text5.setText(getString(R.string.temperature));
                    linearLayout3.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("10")) {
                    text5.setText(getString(R.string.temperature));
                    linearLayout3.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("11")) {
                    text5.setText(getString(R.string.humidity));
                    linearLayout3.setBackgroundResource(R.drawable.humidity);
                } else if (unit.matches("12")) {
                    text5.setText(getString(R.string.co));
                    linearLayout3.setBackgroundResource(R.drawable.co);
                } else if (unit.matches("13")) {
                    text5.setText(getString(R.string.co2));
                    linearLayout3.setBackgroundResource(R.drawable.co2);
                } else if (unit.matches("14")) {
                    text5.setText(getString(R.string.pm));
                    linearLayout3.setBackgroundResource(R.drawable.pm);
                } else if (unit.matches("15")) {
                    text5.setText(getString(R.string.percent));
                    linearLayout3.setBackgroundResource(R.drawable.humidity);
                }
                if (boolean_value.matches("true")) {
                    linearLayout3.setAnimation(animation);
                    //saveRecord();
                }
            }
            break;
            case 4: {
                linearLayout4.setVisibility(View.VISIBLE);
                text7.setVisibility(View.VISIBLE);
                text8.setVisibility(View.VISIBLE);
                text8.setText(text);
                if (unit.matches("0")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("1")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("2")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("3")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("4")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("5")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("6")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("7")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("8")) {
                    text7.setText(getString(R.string.pressure));
                    linearLayout4.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("9")) {
                    text7.setText(getString(R.string.temperature));
                    linearLayout4.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("10")) {
                    text7.setText(getString(R.string.temperature));
                    linearLayout4.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("11")) {
                    text7.setText(getString(R.string.humidity));
                    linearLayout4.setBackgroundResource(R.drawable.humidity);
                } else if (unit.matches("12")) {
                    text7.setText(getString(R.string.co));
                    linearLayout4.setBackgroundResource(R.drawable.co);
                } else if (unit.matches("13")) {
                    text7.setText(getString(R.string.co2));
                    linearLayout4.setBackgroundResource(R.drawable.co2);
                } else if (unit.matches("14")) {
                    text7.setText(getString(R.string.pm));
                    linearLayout4.setBackgroundResource(R.drawable.pm);
                } else if (unit.matches("15")) {
                    text7.setText(getString(R.string.percent));
                    linearLayout4.setBackgroundResource(R.drawable.humidity);
                }
                if (boolean_value.matches("true")) {
                    linearLayout4.setAnimation(animation);
                    //saveRecord();
                }
            }
            break;
            case 5: {
                linearLayout5.setVisibility(View.VISIBLE);
                text9.setVisibility(View.VISIBLE);
                text10.setVisibility(View.VISIBLE);
                text10.setText(text);
                if (unit.matches("0")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("1")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("2")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("3")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("4")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("5")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("6")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("7")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("8")) {
                    text9.setText(getString(R.string.pressure));
                    linearLayout5.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("9")) {
                    text9.setText(getString(R.string.temperature));
                    linearLayout5.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("10")) {
                    text9.setText(getString(R.string.temperature));
                    linearLayout5.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("11")) {
                    text9.setText(getString(R.string.humidity));
                    linearLayout5.setBackgroundResource(R.drawable.humidity);
                } else if (unit.matches("12")) {
                    text9.setText(getString(R.string.co));
                    linearLayout5.setBackgroundResource(R.drawable.co);
                } else if (unit.matches("13")) {
                    text9.setText(getString(R.string.co2));
                    linearLayout5.setBackgroundResource(R.drawable.co2);
                } else if (unit.matches("14")) {
                    text9.setText(getString(R.string.pm));
                    linearLayout5.setBackgroundResource(R.drawable.pm);
                } else if (unit.matches("15")) {
                    text9.setText(getString(R.string.percent));
                    linearLayout5.setBackgroundResource(R.drawable.humidity);
                }
                if (boolean_value.matches("true")) {
                    linearLayout5.setAnimation(animation);
                    //saveRecord();
                }
            }
            break;
            case 6: {
                linearLayout6.setVisibility(View.VISIBLE);
                text11.setVisibility(View.VISIBLE);
                text12.setVisibility(View.VISIBLE);
                text12.setText(text);
                if (unit.matches("0")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("1")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("2")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("3")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("4")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("5")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("6")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("7")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("8")) {
                    text11.setText(getString(R.string.pressure));
                    linearLayout6.setBackgroundResource(R.drawable.pressure);
                } else if (unit.matches("9")) {
                    text11.setText(getString(R.string.temperature));
                    linearLayout6.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("10")) {
                    text11.setText(getString(R.string.temperature));
                    linearLayout6.setBackgroundResource(R.drawable.temperature);
                } else if (unit.matches("11")) {
                    text11.setText(getString(R.string.humidity));
                    linearLayout6.setBackgroundResource(R.drawable.humidity);
                } else if (unit.matches("12")) {
                    text11.setText(getString(R.string.co));
                    linearLayout6.setBackgroundResource(R.drawable.co);
                } else if (unit.matches("13")) {
                    text11.setText(getString(R.string.co2));
                    linearLayout6.setBackgroundResource(R.drawable.co2);
                } else if (unit.matches("14")) {
                    text11.setText(getString(R.string.pm));
                    linearLayout6.setBackgroundResource(R.drawable.pm);
                } else if (unit.matches("15")) {
                    text11.setText(getString(R.string.percent));
                    linearLayout6.setBackgroundResource(R.drawable.humidity);
                }
                if (boolean_value.matches("true")) {
                    linearLayout6.setAnimation(animation);
                    //saveRecord();
                }
            }
            break;
        }
    }

    private void setValue(int count, String boolean_value, String text) {
        switch (count) {
            case 1: {
                linearLayout1.setVisibility(View.VISIBLE);
                text1.setVisibility(View.VISIBLE);
                text2.setVisibility(View.VISIBLE);
                text2.setText(text);
                if (boolean_value.matches("true")) {
                    if (linearLayout1.getAnimation() == null) {
                        linearLayout1.setAnimation(animation);
                    }
                    //saveRecord();
                } else {
                    linearLayout1.setAnimation(null);
                }
            }
            break;
            case 2: {
                linearLayout2.setVisibility(View.VISIBLE);
                text3.setVisibility(View.VISIBLE);
                text4.setVisibility(View.VISIBLE);
                text4.setText(text);
                if (boolean_value.matches("true")) {
                    if (linearLayout2.getAnimation() == null) {
                        linearLayout2.setAnimation(animation);
                    }
                    //saveRecord();
                } else {
                    linearLayout2.setAnimation(null);
                }
            }
            break;
            case 3: {
                linearLayout3.setVisibility(View.VISIBLE);
                text5.setVisibility(View.VISIBLE);
                text6.setVisibility(View.VISIBLE);
                text6.setText(text);
                if (boolean_value.matches("true")) {
                    if (linearLayout3.getAnimation() == null) {
                        linearLayout3.setAnimation(animation);
                    }
                    //saveRecord();
                } else {
                    linearLayout3.setAnimation(null);
                }
            }
            break;
            case 4: {
                linearLayout4.setVisibility(View.VISIBLE);
                text7.setVisibility(View.VISIBLE);
                text8.setVisibility(View.VISIBLE);
                text8.setText(text);
                if (boolean_value.matches("true")) {
                    if (linearLayout4.getAnimation() == null) {
                        linearLayout4.setAnimation(animation);
                    }
                    //saveRecord();
                } else {
                    linearLayout4.setAnimation(null);
                }
            }
            break;
            case 5: {
                linearLayout5.setVisibility(View.VISIBLE);
                text9.setVisibility(View.VISIBLE);
                text10.setVisibility(View.VISIBLE);
                text10.setText(text);
                if (boolean_value.matches("true")) {
                    if (linearLayout5.getAnimation() == null) {
                        linearLayout5.setAnimation(animation);
                    }
                    //saveRecord();
                } else {
                    linearLayout5.setAnimation(null);
                }
            }
            break;
            case 6: {
                linearLayout6.setVisibility(View.VISIBLE);
                text11.setVisibility(View.VISIBLE);
                text12.setVisibility(View.VISIBLE);
                text12.setText(text);
                if (boolean_value.matches("true")) {
                    if (linearLayout6.getAnimation() == null) {
                        linearLayout6.setAnimation(animation);
                    }
                    //saveRecord();
                } else {
                    linearLayout6.setAnimation(null);
                }
            }
            break;
        }
    }

    private void saveRecord(List<String> list) {
        String address = list.get(1);
        String devicename = list.get(0);
        ArrayList<String> data_record = new ArrayList<>();
        data_record.clear();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat get_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String time = get_time.format(date);

        String a = list.get(2);
        list = list.subList(list.indexOf(a) + 1, list.size());

        data_record.addAll(list);
        JSONArray jsonArray = new JSONArray(data_record);

        if (alertRecord.getCount(address) == 0) {
            alertRecord.insert(address, devicename, time, jsonArray);
        } else {
            List<String> checklist = new ArrayList<>();
            checklist.clear();

            checklist = alertRecord.getlist(address);

            if (checklist.size() != 0) {
                String recordtime = checklist.get(1);
                logMessage.showmessage(TAG, "recordtime = " + recordtime);
                logMessage.showmessage(TAG, "nowtime = " + time);
                if (timeCheck.checktime(recordtime, time)) {
                    alertRecord.insert(address, devicename, time, jsonArray);
                }
            }
        }

        logMessage.showmessage(TAG, "address = " + address);
        logMessage.showmessage(TAG, "alertRecord.getCount = " + alertRecord.getCount(address));

        ArrayList<List<String>> data = new ArrayList<>();
        data.clear();
        data = alertRecord.getRecordList(address);
        logMessage.showmessage(TAG, "data = " + data);
    }

    @SuppressLint("HandlerLeak")
    private Handler connectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Remote_connec();
        }
    };

    private Runnable sendcheck = () -> {
        try {
            sleep(500);
            logMessage.showmessage(TAG, "sends = " + Jetec);
            if (s_connect) {
                //Value.bluetoothLeService = mBluetoothLeService;
                sendValue = new SendValue(mBluetoothLeService);
                sendValue.send(Jetec);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    private void recordlist() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //toolbar menu item
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            vibrator.vibrate(100);
            if (alertRecord.size() > 0) {
                recordlist();
            } else {
                Toast.makeText(this, getString(R.string.no_record), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return true;
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK:
                vibrator.vibrate(100);
                backtofirst();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage.showmessage(TAG, "onDestroy()");
        if (Value.connected) {
            Value.connected = false;
        }
        if (mBluetoothLeService != null) {
            if (s_connect) {
                unbindService(mServiceConnection);
                s_connect = false;
            }
            mBluetoothLeService.stopSelf();
            mBluetoothLeService = null;
        }
        if (mBluetoothAdapter != null)
            //noinspection deprecation
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        valueHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
        alertRecord.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMessage.showmessage(TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logMessage.showmessage(TAG, "onResume");
        if (s_connect) {
            backtofirst();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMessage.showmessage(TAG, "onPause");
        if (s_connect && nextpage)
            unregisterReceiver(mGattUpdateReceiver);
        if (mBluetoothAdapter != null) {
            //noinspection deprecation
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            s_connect = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }
    }
}