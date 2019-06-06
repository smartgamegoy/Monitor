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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.jetec.Monitor.Listener.GetSearchList;
import com.jetec.Monitor.Listener.SearchListener;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.GetUnit;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.SQL.SaveLogSQL;
import com.jetec.Monitor.SupportFunction.SearchNewList;
import com.jetec.Monitor.SupportFunction.SetDateTime;
import com.jetec.Monitor.SupportFunction.SetEditHint;
import com.jetec.Monitor.SupportFunction.Value;
import org.json.JSONArray;
import org.json.JSONException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchLogDataActivity extends AppCompatActivity implements SearchListener {

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
    private List<String> nameList;
    private ArrayList<List<String>> valueList;
    private SaveLogSQL saveLogSQL = new SaveLogSQL(this);
    private TextView t2, t3;
    private List<String> spinnerList, spinnerList2;
    private GetUnit getUnit = new GetUnit();
    private String chose1 = "", chose2 = "";
    private SetEditHint setEditHint = new SetEditHint();
    private SetDateTime setDateTime = new SetDateTime();
    private GetSearchList getSearchList = new GetSearchList();
    private SearchNewList searchNewList = new SearchNewList();

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
        timeList = new ArrayList<>();
        SQLList = new ArrayList<>();
        valueList = new ArrayList<>();
        nameList = new ArrayList<>();
        spinnerList = new ArrayList<>();
        spinnerList2 = new ArrayList<>();

        selectItem.clear();
        reList.clear();
        dataList.clear();
        timeList.clear();
        SQLList.clear();
        valueList.clear();
        nameList.clear();
        spinnerList.clear();
        spinnerList2.clear();

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

        getSearchList.setListener(this);
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
            logMessage.showmessage(TAG, "valueList = " + valueList);
            logMessage.showmessage(TAG, "valueList.size = " + valueList.size());
            showLogList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showLogList(){
        setContentView(R.layout.searchdatalist);

        Spinner s1 = findViewById(R.id.spinner1);
        Spinner s2 = findViewById(R.id.spinner2);
        EditText e1 = findViewById(R.id.editText1);
        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        t2 = findViewById(R.id.textView6);
        t3 = findViewById(R.id.textView7);
        LinearLayout l1 = findViewById(R.id.textlinear);

        spinnerList.add(getString(R.string.condition1));    //項目，條件，數值
        spinnerList.add(getString(R.string.datetime));
        spinnerList.add(getString(R.string.size));

        spinnerList2.add(getString(R.string.condition2));
        spinnerList2.add("＞");
        spinnerList2.add("≧");
        spinnerList2.add("＝");
        spinnerList2.add("≦");
        spinnerList2.add("＜");

        for (int i = 0; i < valueList.size(); i++) {
            nameList.add(Value.getviewlist.get(i * 4));
            spinnerList.add(getUnit.getName(this, Value.getviewlist.get(i * 4)));
        }

        new Thread(timecheck).start();

        s2.setEnabled(false);
        t2.setText("");
        t3.setVisibility(View.GONE);
        e1.setVisibility(View.GONE);
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_style, spinnerList) {    //android.R.layout.simple_spinner_item
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);    //R.layout.spinner_style
        s1.setAdapter(spinnerArrayAdapter);
        s1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vibrator.vibrate(100);
                e1.setText("");
                Log.e("myLog", "position = " + position);
                if (position == 0) {
                    s2.setEnabled(false);
                    t3.setVisibility(View.GONE);
                    e1.setVisibility(View.GONE);
                    b1.setVisibility(View.GONE);
                    b2.setVisibility(View.GONE);
                } else if (position == 1) {
                    s2.setEnabled(true);
                    e1.setVisibility(View.GONE);
                    t3.setVisibility(View.VISIBLE);
                    b1.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);
                    chose1 = spinnerList.get(position);
                } else if (position == 2) {
                    s2.setEnabled(true);
                    e1.setVisibility(View.VISIBLE);
                    e1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    e1.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                    e1.setHint("1 ~ " + timeList.size());
                    t3.setVisibility(View.GONE);
                    b1.setVisibility(View.GONE);
                    b2.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                    chose1 = spinnerList.get(position);
                } else {
                    s2.setEnabled(true);
                    e1.setVisibility(View.VISIBLE);
                    e1.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED |
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    //chose_position = position;
                    t3.setVisibility(View.GONE);
                    b1.setVisibility(View.GONE);
                    b2.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                    chose1 = spinnerList.get(position);
                    setEditHint.seteditHint(SearchLogDataActivity.this, e1, position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                this, R.layout.spinner_style, spinnerList2) {    //android.R.layout.simple_spinner_item
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
        };

        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_style);    //R.layout.spinner_style
        s2.setAdapter(spinnerArrayAdapter2);
        s2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vibrator.vibrate(100);
                Log.e("myLog", "position2 = " + position);
                chose2 = spinnerList2.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        b1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            setDateTime.datechose(this, t2);
        });

        b2.setOnClickListener(v -> {
            vibrator.vibrate(100);
            setDateTime.timechose(this, t2);
        });

        b3.setOnClickListener(v -> {
            vibrator.vibrate(100);

            if (chose1.matches(getString(R.string.datetime))) {
                if (!chose2.matches("") && !t2.getText().toString().trim().matches("")) {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                    String timecomparison = t2.getText().toString().trim().substring(2) + ":00";
                    Log.e(TAG, "timecomparison = " + timecomparison);
                    searchNewList.timeSearchList(this, getSearchList, chose2, sdf, timecomparison, timeList, valueList, nameList);
                } else
                    Toast.makeText(this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
            } else /*if (chose1.matches(record))*/ {
                if (!chose2.matches(getString(R.string.condition2)) && !e1.getText().toString().trim().matches("")) {
                    String text = e1.getText().toString().trim();
                    searchNewList.idSearchList(this, getSearchList, chose1, chose2, text, nameList, spinnerList, timeList, valueList);
                    //calculate(chose1, chose2, e1.getText().toString().trim());
                } else
                    Toast.makeText(this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Runnable timecheck = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            String f_time = timeList.get(0);
            String e_time = timeList.get(timeList.size() - 1);
            t3.setText(getString(R.string.timerange) + ": " + f_time + " ~ " + e_time);
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
        Intent intent = new Intent(this, LogListActivity.class);

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
            //searchdata();
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

    @Override
    public void checkList(String list) {
        Intent intent = new Intent(this, ShowSearchListActivity.class);

        intent.putExtra("BID", BID);
        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putStringArrayListExtra("reList", reList);
        intent.putStringArrayListExtra("dataList", dataList);
        intent.putStringArrayListExtra("timeList", timeList);
        intent.putExtra("list", list);

        startActivity(intent);
        finish();
    }

    @Override
    public void setList(List<List<String>> showlist) {

    }
}
