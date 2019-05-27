package com.jetec.Monitor.SwitchWL;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.jetec.Monitor.Activity.FirstActivity;
import com.jetec.Monitor.Activity.StartActivity;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;
import com.jetec.Monitor.SupportFunction.RunningFlash;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SupportFunction.Value;
import com.jetec.Monitor.SwitchWL.DeviceList.SearchList;
import com.jetec.Monitor.SwitchWL.Listener.ResetListListener;
import com.jetec.Monitor.SwitchWL.Listener.StartReset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SwitchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ResetListListener {

    private String TAG = "SwitchActivity";
    private LogMessage logMessage = new LogMessage();
    private Vibrator vibrator;
    private Intent intents;
    private String BID;
    private String Jetec = "Jetec";
    private RunningFlash runningFlash = new RunningFlash(this);
    private Parase parase = new Parase();
    private SendValue sendValue;
    private boolean s_connect = false, engineer = false, leave = false;
    private List<BluetoothDevice> deviceList, checkdeviceList;
    private Map<Integer, List<String>> viewList;
    private List<byte[]> setrecord;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private StartReset startReset = new StartReset();
    private SearchList searchList;
    private View no_device;
    private ListView listView;
    private byte[] txValue;
    private String text;
    private Handler handler, connectHandler;
    private List<String> valueList;
    private ArrayList<String> selectItem;
    private byte[] getover = {0x4F, 0x56, 0x45, 0x52};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        runningFlash = new RunningFlash(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        BluetoothManager bluetoothManager = getManager(this);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        show_device();
    }

    private void show_device() {
        setContentView(R.layout.switchmain);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.switchs).setEnabled(false);

        no_device = findViewById(R.id.no_data);
        listView = findViewById(R.id.listview);
        no_device.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);//VISIBLE / GONE

        device_list();
    }

    @SuppressLint("UseSparseArrays")
    private void device_list() {
        handler = new Handler();
        connectHandler = new Handler();
        startReset.setListener(this);
        startReset.getContext(this);
        viewList = new HashMap<>();
        checkdeviceList = new ArrayList<>();
        deviceList = new ArrayList<>();
        setrecord = new ArrayList<>();
        valueList = new ArrayList<>();
        selectItem = new ArrayList<>();
        viewList.clear();
        checkdeviceList.clear();
        deviceList.clear();
        setrecord.clear();
        selectItem.clear();
        valueList.clear();
        scanLeDevice();
    }

    private void scanLeDevice() {
        //noinspection deprecation
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        startReset.startReset();
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

    private void checkConnected(){
        connectHandler.postDelayed(() -> {
            if (s_connect) {
                sendValue = new SendValue(mBluetoothLeService);
                sendValue.send(Jetec);
            }
        }, 500);
    }

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
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                logMessage.showmessage(TAG, "連線狀態改變");
                mBluetoothLeService.enableTXNotification();
                checkConnected();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(() -> {
                    txValue = intents.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    StringBuilder gethex = new StringBuilder();
                    String[] hexstr = parase.hexstring(txValue);
                    text = new String(txValue, StandardCharsets.UTF_8);
                    for (String aHexstr : hexstr) {
                        gethex.append(aHexstr);
                    }
                    logMessage.showmessage(TAG, "hexstr = " + gethex);
                    logMessage.showmessage(TAG, "text = " + text);
                    if (text.startsWith("OK")) {
                        selectItem.clear();
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
                        logMessage.showmessage(TAG, "model_num = " + Value.model_num);
                        logMessage.showmessage(TAG, "model_name = " + Value.model_name);
                        logMessage.showmessage(TAG, "model_relay = " + Value.model_relay);
                        selectItem.add("NAME" + valueList.get(0));
                        sendValue.send("get");
                    } else {
                        if (Arrays.equals(txValue, getover)) {
                            logMessage.showerror(TAG, "selectItem = " + selectItem);
                            if (selectItem.size() != (Integer.valueOf(valueList.get(3)) + 1)) {
                                checkConnected();
                            } else {
                                if (engineer) {
                                    //工程模式
                                } else {
                                    logMessage.showmessage(TAG, "做畫面囉");
                                    showview();
                                }
                            }
                        } else {
                            selectItem.add(gethex.toString());
                        }
                    }
                });
            }
        }
    };

    private void showview(){
        Intent intent = new Intent(this, SwitchViewActivity.class);
        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putExtra("BID", BID);
        Service_close();
        runningFlash.closeFlash();
        startActivity(intent);
        finish();
    }

    public static BluetoothManager getManager(Context context) {    //獲取此設備默認藍芽適配器
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
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

    private void backtofirst() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void monitoract() {
        Intent intent = new Intent(this, FirstActivity.class);
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

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
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

    private AdapterView.OnItemClickListener itemOnClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            vibrator.vibrate(100);
            valueList = viewList.get(position);
            BID = Objects.requireNonNull(valueList).get(1);
            Value.device_name = Objects.requireNonNull(valueList).get(0);
            Remote_connec();
        }
    };

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
        leave = true;
        handler.removeCallbacksAndMessages(null);
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
        unregisterReceiver(mGattUpdateReceiver);
        Service_close();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sensors) {
            vibrator.vibrate(100);
            monitoract();
        } else if (id == R.id.switchs) {
            vibrator.vibrate(100);
        } else if (id == R.id.url_phonecall) {
            vibrator.vibrate(100);
            Uri uri = Uri.parse("http://www.jetec.com.tw/wicloud/support.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void resetList() {
        if (!leave) {
            handler.postDelayed(() -> {
                //new Thread(checkList).start();
                for (int i = 0; i < deviceList.size(); i++) {
                    if (checkdeviceList.indexOf(deviceList.get(i)) == -1) {
                        //noinspection SuspiciousListRemoveInLoop
                        deviceList.remove(i);
                        //noinspection SuspiciousListRemoveInLoop
                        setrecord.remove(i);
                    }
                }
                checkdeviceList.clear();
                startReset.paraseList(deviceList, setrecord);
                handler.removeCallbacksAndMessages(null);
                startReset.startReset();
            }, 3000);
        }
    }

    @Override
    public void paraseList(Map<Integer, List<String>> setList) {
        logMessage.showerror(TAG, "setList = " + setList);
        this.viewList = setList;
        if (listView.getAdapter() == null) {
            searchList = new SearchList(this, setList);
            listView.setAdapter(searchList);
            listView.setOnItemClickListener(itemOnClick);
            if (setList.size() != 0) {
                no_device.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } else {
            if (setList.size() != 0) {
                no_device.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                no_device.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            searchList.resetList(setList);
            searchList.notifyDataSetChanged();
        }
    }
}
