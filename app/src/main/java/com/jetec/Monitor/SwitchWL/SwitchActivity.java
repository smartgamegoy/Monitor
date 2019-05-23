package com.jetec.Monitor.SwitchWL;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
import com.jetec.Monitor.Activity.FirstActivity;
import com.jetec.Monitor.Activity.StartActivity;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.RunningFlash;
import com.jetec.Monitor.SupportFunction.Value;
import com.jetec.Monitor.SwitchWL.Listener.ResetListListener;
import com.jetec.Monitor.SwitchWL.Listener.StartReset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private RunningFlash runningFlash;
    private boolean s_connect = false, engineer = false, leave = false;
    public List<BluetoothDevice> deviceList, checkdeviceList;
    public List<byte[]> setrecord;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    private StartReset startReset = new StartReset();
    private View no_device;
    private ListView listView;
    private byte[] txValue;
    private String text;
    private Handler handler;

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
        startReset.setListener(this);
        startReset.getContext(this);
        checkdeviceList = new ArrayList<>();
        deviceList = new ArrayList<>();
        setrecord = new ArrayList<>();
        checkdeviceList.clear();
        deviceList.clear();
        setrecord.clear();
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
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(() -> {
                    txValue = intents.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    text = new String(txValue, StandardCharsets.UTF_8);
                    logMessage.showmessage(TAG, "text = " + text);
                });
            }
        }
    };

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

    private Runnable checkList = new Runnable() {
        @Override
        public void run() {
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
            startReset.startReset();
        }
    };

    private void backtofirst() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void monitoract(){
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
        if (s_connect)
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
        if(!leave) {
            handler.postDelayed(() -> {
                handler.removeCallbacksAndMessages(null);
                new Thread(checkList).start();
            }, 3000);
        }
    }

    @Override
    public void paraseList(Map<Integer, List<String>> setList) {
        logMessage.showerror(TAG,"setList = " + setList);
    }
}
