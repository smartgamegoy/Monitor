package com.jetec.Monitor.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.jetec.Monitor.ENGIN;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.*;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import static com.jetec.Monitor.Activity.FirstActivity.getManager;
import static java.lang.Thread.sleep;

public class EngineerMode extends AppCompatActivity {

    private Vibrator vibrator;
    private String TAG = "EngineerMode";
    private BluetoothAdapter mBluetoothAdapter;
    private LogMessage logMessage = new LogMessage();
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private String BID;
    private Intent intents;
    private SendValue sendValue;
    private ArrayList<String> selectItem, reList, dataList;
    private ArrayAdapter<String> listAdapter;
    private ListView list1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        BluetoothManager bluetoothManager = getManager(this);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            s_connect = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            if (s_connect)
                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            else
                logMessage.showmessage(TAG, "連線失敗");
        }

        get_intent();
        startEngineer();
    }

    private void startEngineer(){
        setContentView(R.layout.engineer_title);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ENGIN engin = new ENGIN(this);
        list1 = findViewById(R.id.listMessage);
        EditText e1 = findViewById(R.id.sendText);
        Button b1 = findViewById(R.id.sendButton);
        Button fix = findViewById(R.id.button);
        Button SP1 = findViewById(R.id.button2);
        Button ER1 = findViewById(R.id.button3);
        Button SV1 = findViewById(R.id.button4);
        Button GET = findViewById(R.id.button5);
        Button SP2 = findViewById(R.id.button6);
        Button ER2 = findViewById(R.id.button7);
        Button SV2 = findViewById(R.id.button8);
        Button init = findViewById(R.id.button9);
        Button SP3 = findViewById(R.id.button10);
        Button ER3 = findViewById(R.id.button11);
        Button SV3 = findViewById(R.id.button12);

        listAdapter = new ArrayAdapter<>(this, R.layout.message_detail);
        list1.setAdapter(listAdapter);
        list1.setDivider(null);

        b1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        fix.setOnClickListener(v -> {
            vibrator.vibrate(100);
            try {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                sendValue = new SendValue(mBluetoothLeService);
                sendValue.send("FIX");
                sleep(100);
                listAdapter.add("[" + currentDateTimeString + "] send: " + "FIX");
                list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                e1.setText("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        SP1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "SP1", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ER1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "ER1", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        SV1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "SV1", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        GET.setOnClickListener(v -> {
            vibrator.vibrate(100);
            try {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                logMessage.showmessage(TAG,"mBluetoothLeService = " + mBluetoothLeService);
                sendValue = new SendValue(mBluetoothLeService);
                sendValue.send("get");
                sleep(100);
                listAdapter.add("[" + currentDateTimeString + "] send: " + "get");
                list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                e1.setText("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        SP2.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "SP2", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ER2.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "ER2", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        SV2.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "SV2", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        init.setOnClickListener(v -> {
            vibrator.vibrate(100);
            /*Initialization initialization = new Initialization(Value.deviceModel, mBluetoothLeService);
            try {
                initialization.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        });

        SP3.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "SP3", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ER3.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "ER3", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        SV3.setOnClickListener(v -> {
            vibrator.vibrate(100);
            String message = e1.getText().toString().trim();
            if(!message.matches("")){
                try {
                    message = engin.todo(Float.valueOf(message), "SV3", message);
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    sendValue = new SendValue(mBluetoothLeService);
                    sendValue.send(message);
                    sleep(100);
                    listAdapter.add("[" + currentDateTimeString + "] send: " + message);
                    list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    e1.setText("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                listAdapter.add("[" + currentDateTimeString + "] 連線中斷: " + "自動重連中...");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                logMessage.showmessage(TAG, "連線狀態改變");
                mBluetoothLeService.enableTXNotification();
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                listAdapter.add("[" + currentDateTimeString + "] 連線狀態: " + "已重新連線");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(() -> {
                    try {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        byte[] txValue = intents.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        String text = new String(txValue, "UTF-8");
                        listAdapter.add("[" + currentDateTimeString + "] RE: " + text);
                        list1.smoothScrollToPosition(listAdapter.getCount() - 1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    };

    private void get_intent(){

        selectItem = new ArrayList<>();
        reList = new ArrayList<>();
        dataList = new ArrayList<>();

        selectItem.clear();
        reList.clear();
        dataList.clear();

        Intent intent = getIntent();

        BID = intent.getStringExtra("BID");
        selectItem = intent.getStringArrayListExtra("selectItem");
        reList = intent.getStringArrayListExtra("reList");
        dataList = intent.getStringArrayListExtra("dataList");
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

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            (device, rssi, scanRecord) -> runOnUiThread(() -> runOnUiThread(this::addDevice));

    private void addDevice() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
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
    protected void onPause() {
        super.onPause();
        if (s_connect)
            unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (s_connect) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            if (mBluetoothLeService != null) {
                final boolean result = mBluetoothLeService.connect(BID);
                Log.d(TAG, "Connect request result=" + result);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK: {
                vibrator.vibrate(100);
                new AlertDialog.Builder(EngineerMode.this)
                        .setTitle("結束連線")
                        .setMessage("斷開藍牙")
                        .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                            vibrator.vibrate(100);
                            if (mBluetoothAdapter != null)
                                //noinspection deprecation
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            Service_close();
                            Value.connected = false;
                            disconnect();
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

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.engin, menu);
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
            Intent intent = new Intent(EngineerMode.this, DeviceEngineer.class);

            intent.putExtra("BID", BID);
            intent.putStringArrayListExtra("selectItem", selectItem);
            intent.putStringArrayListExtra("reList", reList);
            intent.putStringArrayListExtra("dataList", dataList);

            startActivity(intent);
            finish();
            return true;
        }
        return true;
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
        //Value.Jsonlist.clear();
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
            //show_device();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
            //show_device();
        }
    }
}
