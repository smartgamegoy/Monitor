package com.jetec.Monitor.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.jetec.Monitor.Dialog.InputDialog;
import com.jetec.Monitor.Dialog.RLDialog;
import com.jetec.Monitor.Dialog.SpkDialog;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.*;
import com.jetec.Monitor.SupportFunction.ViewAdapter.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import static com.jetec.Monitor.Activity.FirstActivity.makeGattUpdateIntentFilter;

public class DeviceEngineer extends AppCompatActivity {

    private String TAG = "DeviceEngineer";
    private LogMessage logMessage = new LogMessage();
    private GetDeviceNum getDeviceNum;
    private Vibrator vibrator;
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private Intent intents;
    private String BID;
    private ArrayList<String> selectItem, dataList, reList, deviceNameList, deviceNumList;
    private Function function;
    private InputDialog inputDialog = new InputDialog();
    private SpkDialog spkDialog = new SpkDialog();
    private CheckDeviceName checkDeviceName = new CheckDeviceName();
    private RLDialog rlDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            s_connect = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            if (s_connect)
                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            else
                Log.e(TAG, "連線失敗");
        }
        ConfigurationChange();
    }

    private void ConfigurationChange() {

        selectItem = new ArrayList<>();
        reList = new ArrayList<>();
        dataList = new ArrayList<>();
        deviceNameList = new ArrayList<>();
        deviceNumList = new ArrayList<>();
        selectItem.clear();
        reList.clear();
        dataList.clear();
        deviceNameList.clear();
        deviceNumList.clear();

        Intent intent = getIntent();

        BID = intent.getStringExtra("BID");
        selectItem = intent.getStringArrayListExtra("selectItem");
        reList = intent.getStringArrayListExtra("reList");
        dataList = intent.getStringArrayListExtra("dataList");

        logMessage.showmessage(TAG, "selectItem = " + selectItem);
        logMessage.showmessage(TAG, "reList = " + reList);
        logMessage.showmessage(TAG, "dataList = " + dataList);

        show_device_function();
    }

    private void show_device_function() {
        setContentView(R.layout.device_title);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ListView listname = findViewById(R.id.list_name_function);

        deviceNameList.add(getString(R.string.device_name));
        deviceNumList.add(Value.device_name);

        GetDeviceName getDeviceName = new GetDeviceName(this, Value.model_name);
        getDeviceNum = new GetDeviceNum(this);
        rlDialog = new RLDialog(this);

        logMessage.showmessage(TAG,"reList = " + reList);

        for (int i = 0; i < reList.size(); i++) {
            deviceNameList.add(getDeviceName.get(reList.get(i)));
            deviceNumList.add(getDeviceNum.get(reList.get(i)));
        }

        function = new Function(this, deviceNameList, deviceNumList);
        listname.setAdapter(function);
        listname.setOnItemClickListener(mSelectClickListener);

        logMessage.showmessage(TAG, "deviceNameList = " + deviceNameList);
        logMessage.showmessage(TAG, "deviceNumList = " + deviceNumList);
    }

    private AdapterView.OnItemClickListener mSelectClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            vibrator.vibrate(100);
            String select = selectItem.get(position);
            String title_name = deviceNameList.get(position);
            logMessage.showmessage(TAG,"select = " + select);
            //noinspection deprecation
            if(select.startsWith("SPK")){
                String num = deviceNumList.get(position);
                spkDialog.setAlert(DeviceEngineer.this, mBluetoothLeService, select,
                        title_name, num);
            }
            else if(select.startsWith("RL")){
                rlDialog.setAlert(mBluetoothLeService, select, title_name);
            }
            else {
                inputDialog.setAlert(DeviceEngineer.this, mBluetoothLeService, select,
                        title_name, selectItem, deviceNumList);
            }
        }
    };

    public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intents = intent;
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                logMessage.showmessage(TAG, "連線成功");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //s_connect = false;
                logMessage.showmessage(TAG, "連線中斷" + Value.connected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                logMessage.showmessage(TAG, "連線狀態改變");
                mBluetoothLeService.enableTXNotification();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(() -> {
                    byte[] txValue = intents.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    String text = new String(txValue, StandardCharsets.UTF_8);
                    logMessage.showmessage(TAG,"text = " + text);
                    if(text.startsWith("EH") || text.startsWith("EL") ||
                            text.startsWith("PR") || text.startsWith("RL") ||
                            text.startsWith("ADR") || text.startsWith("SPK")){
                        int i = selectItem.indexOf(checkDeviceName.setName(text));
                        reList.set((i - 1), text);
                        deviceNumList.set(i, getDeviceNum.get(reList.get(i - 1)));
                        logMessage.showmessage(TAG,"reList = " + reList);
                        logMessage.showmessage(TAG,"deviceNumList = " + deviceNumList);
                        function.notifyDataSetChanged();
                    }
                    else if(text.startsWith("NAME")){
                        deviceNumList.set(0, text.substring(4));
                        logMessage.showmessage(TAG,"deviceNumList = " + deviceNumList);
                        function.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
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

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK: {
                vibrator.vibrate(100);
                new AlertDialog.Builder(this)
                        .setTitle("結束連線")
                        .setMessage("斷開藍牙")
                        .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                            vibrator.vibrate(100);
                            Service_close();
                            Value.connected = false;
                            disconnect();
                        })
                        .setNegativeButton("工程模式", (dialog, which) -> {
                            Intent intent = new Intent(this, EngineerMode.class);
                            intent.putStringArrayListExtra("selectItem", selectItem);
                            intent.putStringArrayListExtra("reList", reList);
                            intent.putStringArrayListExtra("dataList", dataList);
                            intent.putExtra("BID", BID);
                            startActivity(intent);
                            finish();
                        })
                        .setNeutralButton(R.string.butoon_no, (dialog, which) -> {
                        })
                        .show();
            }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            default:
                return false;
        }
        return false;
    }

    public void Service_close() {
        if (mBluetoothLeService == null) {
            Log.e(TAG, "masaga");
            return;
        }
        mBluetoothLeService.disconnect();
    }

    private void disconnect() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMessage.showmessage(TAG, "onStop");
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        logMessage.showmessage(TAG, "onResume");
        logMessage.showmessage(TAG, "s_connect = " + s_connect);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(BID);
            Log.d(TAG, "Connect request result=" + result);
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
}
