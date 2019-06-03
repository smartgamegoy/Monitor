package com.jetec.Monitor.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.jetec.Monitor.Dialog.*;
import com.jetec.Monitor.Listener.DownloadLogListener;
import com.jetec.Monitor.Listener.GetDownloadLog;
import com.jetec.Monitor.Listener.GetLoadList;
import com.jetec.Monitor.Listener.LoadListListener;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.CheckDeviceName;
import com.jetec.Monitor.SupportFunction.GetDeviceName;
import com.jetec.Monitor.SupportFunction.GetDeviceNum;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.RunningFlash;
import com.jetec.Monitor.SupportFunction.SQL.DataListSQL;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SupportFunction.Value;
import com.jetec.Monitor.SupportFunction.ViewAdapter.Function;
import org.json.JSONArray;
import org.json.JSONException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DeviceFunction extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LoadListListener, DownloadLogListener {

    private String TAG = "DeviceFunction";
    private LogMessage logMessage = new LogMessage();
    private GetDeviceNum getDeviceNum;
    private Vibrator vibrator;
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private Intent intents;
    private NavigationView navigationView;
    private String BID, modelName;
    private ArrayList<String> selectItem;
    private ArrayList<String> reList;
    private ArrayList<String> deviceNameList;
    private ArrayList<String> deviceNumList;
    private Function function;
    private InputDialog inputDialog = new InputDialog();
    private SpkDialog spkDialog = new SpkDialog();
    private RLDialog rlDialog;
    private CheckDeviceName checkDeviceName = new CheckDeviceName();
    private DataListSQL dataListSQL = new DataListSQL(this);
    private GetLoadList getLoadList = new GetLoadList();
    private GetDownloadLog getDownloadLog = new GetDownloadLog();
    private Handler mHandler, startlogHandler;
    private SendValue sendValue;
    private int datacount, sc = 0;

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

        getDownloadLog.setListener(this);
        mHandler = new Handler();
        startlogHandler = new Handler();
        selectItem = new ArrayList<>();
        reList = new ArrayList<>();
        ArrayList<String> dataList = new ArrayList<>();
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

        modelName = Value.model_name;
        if (modelName.contains("L")) {
            Value.catchL = true;
        } else {
            Value.catchL = false;
        }

        show_device_function();
    }

    private void show_device_function() {
        setContentView(R.layout.devicedrawerlayout);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!Value.downlog) {
            navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.start) + getString(R.string.LOG));
        } else {
            navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.end) + getString(R.string.LOG));
        }

        if (!Value.catchL) {
            navigationView.getMenu().findItem(R.id.datadownload).setEnabled(false);
            SpannableString spanString1 = new SpannableString(navigationView.getMenu().
                    findItem(R.id.datadownload).getTitle().toString());
            spanString1.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spanString1.length(), 0);
            navigationView.getMenu().findItem(R.id.datadownload).setTitle(spanString1);
            navigationView.getMenu().findItem(R.id.showdialog).setEnabled(false);
            SpannableString spanString2 = new SpannableString(navigationView.getMenu().
                    findItem(R.id.showdialog).getTitle().toString());
            spanString2.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spanString2.length(), 0);
            navigationView.getMenu().findItem(R.id.showdialog).setTitle(spanString2);
            navigationView.getMenu().findItem(R.id.nav_share).setEnabled(false);
            SpannableString spanString3 = new SpannableString(navigationView.getMenu().
                    findItem(R.id.nav_share).getTitle().toString());
            spanString3.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spanString3.length(), 0);
            navigationView.getMenu().findItem(R.id.nav_share).setTitle(spanString3);
        }

        ListView listname = findViewById(R.id.list_name_function);

        deviceNameList.add(getString(R.string.device_name));
        deviceNumList.add(Value.device_name);

        GetDeviceName getDeviceName = new GetDeviceName(this, Value.model_name);
        getDeviceNum = new GetDeviceNum(this);
        rlDialog = new RLDialog(this);

        logMessage.showmessage(TAG, "reList = " + reList);

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
            logMessage.showmessage(TAG, "select = " + select);
            //noinspection deprecation
            if (select.startsWith("SPK")) {
                String num = deviceNumList.get(position);
                spkDialog.setAlert(DeviceFunction.this, mBluetoothLeService, select,
                        title_name, num);
            } else if (select.startsWith("RL")) {
                rlDialog.setAlert(mBluetoothLeService, select, title_name);
            } else if (select.startsWith("INTER")) {
                String description = getString(R.string.description);
                InterDialog interDialog = new InterDialog();
                interDialog.setDialog(DeviceFunction.this, vibrator, mBluetoothLeService, description);
            } else {
                inputDialog.setAlert(DeviceFunction.this, mBluetoothLeService, select,
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
                    logMessage.showmessage(TAG, "text = " + text);
                    if (text.startsWith("EH") || text.startsWith("EL") ||
                            text.startsWith("PR") || text.startsWith("RL") ||
                            text.startsWith("ADR") || text.startsWith("SPK") ||
                            text.startsWith("PV") || text.startsWith("INTER")) {
                        int i = selectItem.indexOf(checkDeviceName.setName(text));
                        reList.set((i - 1), text);
                        deviceNumList.set(i, getDeviceNum.get(reList.get(i - 1)));
                        logMessage.showmessage(TAG, "reList = " + reList);
                        logMessage.showmessage(TAG, "deviceNumList = " + deviceNumList);
                        function.notifyDataSetChanged();
                    } else if (text.startsWith("NAME")) {
                        deviceNumList.set(0, text.substring(4));
                        logMessage.showmessage(TAG, "deviceNumList = " + deviceNumList);
                        function.notifyDataSetChanged();
                    } else if (text.startsWith("COUNT")) {
                        String value = text.replace("COUNT", "");
                        datacount = Integer.valueOf(value);
                        logMessage.showmessage(TAG, "datacount = " + datacount);
                        if(Value.downloading){
                            sendValue.send("DOWNLOAD");
                            Value.downloading = false;
                        }
                    }else if(text.startsWith("END")){
                        if(Value.downloading){
                            sendValue.send("Delay00025");
                        }
                    }else {
                        StringBuilder hex = new StringBuilder(txValue.length * 2);
                        // the data appears to be there backwards
                        for (byte aData : txValue) {
                            hex.append(String.format("%02X", aData));
                        }
                        String gethex = hex.toString();
                        logMessage.showmessage(TAG, "gethex = " + gethex);
                        if(gethex.substring(0, 2).matches("09")){
                            getDownloadLog.addLogList(gethex);
                        }
                        else if(gethex.matches("4F564552")){    //byte[] over = {0x4F, 0x56, 0x45, 52}
                            getDownloadLog.getValue();
                        }
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
            sendValue = new SendValue(mBluetoothLeService);
            logMessage.showmessage(TAG, "進入連線");
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            logMessage.showmessage(TAG, "失去連線端");
        }
    };

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void saveList() {
        String devicename = Value.device;
        JSONArray savejson = new JSONArray(reList);
        logMessage.showmessage(TAG, "devicename = " + devicename);
        logMessage.showmessage(TAG, "savejson = " + savejson.toString());
        DataSaveDialog dataSaveDialog = new DataSaveDialog();
        dataSaveDialog.setDialog(this, vibrator, devicename, savejson.toString(), dataListSQL);
    }

    private void loadList() {
        String devicename = Value.device;
        getLoadList.setListener(this);
        LoadListDialog loadListDialog = new LoadListDialog();
        loadListDialog.setDialog(this, vibrator, getLoadList, dataListSQL, devicename);
    }

    private void sendloadlist(JSONArray loadlist, int count) {
        mHandler.postDelayed(() -> {
            try {
                String str = loadlist.get(count).toString();
                sendValue.send(str);
                mHandler.removeCallbacksAndMessages(null);
                sendloadlist(loadlist, (count + 1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, 100);
    }

    private void startLog(List<String> sendLogList, int i, RunningFlash runningFlash) {
        startlogHandler.postDelayed(() -> {
            if (i < sendLogList.size()) {
                sendValue.send(sendLogList.get(i));
                startlogHandler.removeCallbacksAndMessages(null);
                startLog(sendLogList, (i + 1), runningFlash);
            } else {
                startlogHandler.removeCallbacksAndMessages(null);
                runningFlash.closeFlash();
            }
        }, 500);
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK: {
                vibrator.vibrate(100);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //toolbar menu item
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            vibrator.vibrate(100);
            Service_close();
            disconnect();
            return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage.showmessage(TAG, "onDestroy()");
        if (mBluetoothLeService != null) {
            if (s_connect) {
                unbindService(mServiceConnection);
                s_connect = false;
            }
            mBluetoothLeService.stopSelf();
            mBluetoothLeService = null;
        }
        Value.downlog = false;
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
        if (s_connect)
            unregisterReceiver(mGattUpdateReceiver);
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
        if (id == R.id.savedialog) {
            vibrator.vibrate(100);
            saveList();
        } else if (id == R.id.loadbar) {
            vibrator.vibrate(100);
            loadList();
        } else if (id == R.id.datadownload) {
            vibrator.vibrate(100);
            new AlertDialog.Builder(DeviceFunction.this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.stoprecord)
                    .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                        Value.downlog = false;
                        Value.downloading = true;
                        navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.start) + getString(R.string.LOG));
                        getDownloadLog.clearList();
                        sendValue.send("END");
                    })
                    .setNegativeButton(R.string.butoon_no, (dialog, which) -> {
                        Log.e(TAG, "取消下載");
                    })
                    .show();
        } else if (id == R.id.showdialog) {
            vibrator.vibrate(100);
        } else if (id == R.id.nav_share) {
            vibrator.vibrate(100);
            if (!Value.downlog) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.restart)
                        .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                            Value.downlog = true;
                            navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.end) + getString(R.string.LOG));
                            sendValue.send("END");
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat get_date = new SimpleDateFormat("yyMMdd");
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat get_time = new SimpleDateFormat("HHmmss");
                            Date date = new Date();
                            String strDate = "DATE" + get_date.format(date);
                            String strtime = "TIME" + get_time.format(date);
                            String getinter = reList.get(selectItem.indexOf("INTER") - 1);
                            List<String> sendLogList = new ArrayList<>();
                            sendLogList.clear();
                            sendLogList.add(strDate);
                            sendLogList.add(strtime);
                            sendLogList.add("INTER00020");  //getinter
                            sendLogList.add("START");
                            RunningFlash runningFlash = new RunningFlash(this);
                            if (!runningFlash.isCheck()) {
                                runningFlash.startFlash(getString(R.string.intervalset));
                            }
                            startLog(sendLogList, 0, runningFlash);
                        })
                        .setNegativeButton(R.string.butoon_no, (dialog, which) -> {
                            vibrator.vibrate(100);
                        })
                        .show();
            } else {
                Value.downlog = false;
                navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.start) + getString(R.string.LOG));
                sendValue.send("END");
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void update(String savelist) {
        try {
            logMessage.showmessage(TAG, "reList = " + reList);
            logMessage.showmessage(TAG, "savelist = " + savelist);
            JSONArray listjson = new JSONArray(savelist);
            int count = 0;
            sendloadlist(listjson, count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
