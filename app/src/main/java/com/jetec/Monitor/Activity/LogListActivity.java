package com.jetec.Monitor.Activity;

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
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.SQL.SaveLogSQL;
import com.jetec.Monitor.SupportFunction.Value;
import com.jetec.Monitor.SupportFunction.ViewAdapter.LogListData;
import org.json.JSONArray;
import org.json.JSONException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogListActivity extends AppCompatActivity {

    private String TAG = "LogListActivity";
    private LogMessage logMessage = new LogMessage();
    private Vibrator vibrator;
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private Intent intents;
    private String BID;
    private ArrayList<String> selectItem;
    private ArrayList<String> reList;
    private ArrayList<String> dataList;
    private ArrayList<String> timeList;
    private ArrayList<String> SQLList;
    private ArrayList<List<String>> valueList;
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
        valueList = new ArrayList<>();
        timeList = new ArrayList<>();

        selectItem.clear();
        reList.clear();
        dataList.clear();
        SQLList.clear();
        valueList.clear();
        timeList.clear();

        BID = intent.getStringExtra("BID");
        selectItem = intent.getStringArrayListExtra("selectItem");
        reList = intent.getStringArrayListExtra("reList");
        dataList = intent.getStringArrayListExtra("dataList");
        timeList = intent.getStringArrayListExtra("timeList");
        //BID、selectItem、reList、dataList僅暫存，回歸devicefunction生命週期時使用，須將各設定值回傳
        logMessage.showmessage(TAG, "selectItem = " + selectItem);
        logMessage.showmessage(TAG, "reList = " + reList);
        logMessage.showmessage(TAG, "dataList = " + dataList);
        logMessage.showmessage(TAG, "timeList = " + timeList);

        getSQLList();
    }

    private void getSQLList() {
        try {
            SQLList = saveLogSQL.getsaveLog(BID);    //time, date, list
            String savelist = SQLList.get(3);   //savelist內存有幾排之數值ex:TH，即有2排數值，此處僅取資料列表
            JSONArray savejson = new JSONArray(savelist);
            for (int i = 0; i < savejson.length(); i++) {
                List<String> newList = new ArrayList<>();
                newList.clear();
                String newvalue = savejson.get(i).toString();
                JSONArray jsonArray = new JSONArray(newvalue);
                for (int j = 0; j < jsonArray.length(); j++) {
                    newList.add(jsonArray.get(j).toString());
                }
                valueList.add(newList);
            }

            showLogList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showLogList(){
        setContentView(R.layout.loglistview);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ListView listView = findViewById(R.id.datalist1);

        LogListData logListData = new LogListData(this, valueList, timeList);
        listView.setAdapter(logListData);
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

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

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

    private void backfunction(){
        Intent intent = new Intent(this, LogChartActivity.class);

        intent.putExtra("BID", BID);
        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putStringArrayListExtra("reList", reList);
        intent.putStringArrayListExtra("dataList", dataList);

        startActivity(intent);
        finish();
    }

    private void searchdata(){
        Intent intent = new Intent(this, SearchLogDataActivity.class);

        intent.putExtra("BID", BID);
        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putStringArrayListExtra("reList", reList);
        intent.putStringArrayListExtra("dataList", dataList);
        intent.putStringArrayListExtra("timeList", timeList);

        startActivity(intent);
        finish();
    }

    private void disconnect() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    public void Service_close() {
        if (mBluetoothLeService == null) {
            return;
        }
        mBluetoothLeService.disconnect();
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK: {
                vibrator.vibrate(100);
                backfunction();
            }
            break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chart, menu);
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
            searchdata();
            return true;
        }else if(id == R.id.disconnected){
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
}
