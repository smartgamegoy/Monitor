package com.jetec.Monitor.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.SQL.SaveLogSQL;
import com.jetec.Monitor.SupportFunction.Value;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogChartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = "LogChartActivity";
    private LogMessage logMessage = new LogMessage();
    private Vibrator vibrator;
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private Intent intents;
    private NavigationView navigationView;
    private String BID, modelName;
    private ArrayList<String> selectItem;
    private ArrayList<String> reList;
    private ArrayList<String> dataList;
    private ArrayList<String> SQLList;
    private ArrayList<String> timeList;
    private String saveDate, saveTime, saveInter;
    private JSONArray savejson;
    private SaveLogSQL saveLogSQL = new SaveLogSQL(this);

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
        Intent intent = getIntent();

        selectItem = new ArrayList<>();
        reList = new ArrayList<>();
        dataList = new ArrayList<>();
        SQLList = new ArrayList<>();
        timeList = new ArrayList<>();

        BID = intent.getStringExtra("BID");
        selectItem = intent.getStringArrayListExtra("selectItem");
        reList = intent.getStringArrayListExtra("reList");
        dataList = intent.getStringArrayListExtra("dataList");
        selectItem.clear();
        reList.clear();
        dataList.clear();
        SQLList.clear();
        timeList.clear();

        logMessage.showmessage(TAG, "selectItem = " + selectItem);
        logMessage.showmessage(TAG, "reList = " + reList);
        logMessage.showmessage(TAG, "dataList = " + dataList);

        modelName = Value.model_name;
        showChart();
    }

    private void showChart() {
        try {
            setContentView(R.layout.logview);
            SQLList = saveLogSQL.getsaveLog(Value.device);    //time, date, list
            saveTime = SQLList.get(0);
            saveDate = SQLList.get(1);
            saveInter = SQLList.get(2);
            String savelist = SQLList.get(3);
            savejson = new JSONArray(savelist);
            maketimeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(packagecsv).start();
        new Thread(makepdf).start();
    }

    private Runnable packagecsv = new Runnable() {
        @Override
        public void run() {
            ArrayList<String> data = new ArrayList<>();
            data.clear();
            data.add("id");
            data.add("dateTime");

        }
    };

    private Runnable makepdf = new Runnable() {
        @Override
        public void run() {

        }
    };

    private void maketimeList() {
        try {
            String formattime;
            int inter = Integer.valueOf(saveInter.substring(5));
            String Date = saveDate.substring(4);
            String Time = saveTime.substring(4);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat log_date = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            Date = Date.substring(0, 2) + "-" +
                    Date.substring(2, 4) + "-" + Date.substring(4, 6);
            Time = Time.substring(0, 2) + ":" +
                    Time.substring(2, 4) + ":" + Time.substring(4, 6);
            String all_date = Date + " " + Time;
            Date date = log_date.parse(all_date);
            for (int i = 0; i < savejson.length(); i++) {
                formattime = log_date.format(date);
                timeList.add(formattime);
                date.setTime(date.getTime() + (inter * 1000));
            }
            logMessage.showmessage(TAG, "timeList = " + timeList);
        } catch (ParseException e) {
            e.printStackTrace();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
