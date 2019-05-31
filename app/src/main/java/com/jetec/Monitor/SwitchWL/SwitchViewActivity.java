package com.jetec.Monitor.SwitchWL;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jetec.Monitor.Activity.StartActivity;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Parase;
import com.jetec.Monitor.SupportFunction.SendValue;
import com.jetec.Monitor.SupportFunction.Value;
import com.jetec.Monitor.SwitchWL.Dialog.DataDialog;
import com.jetec.Monitor.SwitchWL.Dialog.LoadDialog;
import com.jetec.Monitor.SwitchWL.Dialog.NameDialog;
import com.jetec.Monitor.SwitchWL.Listener.BlueServiceListener;
import com.jetec.Monitor.SwitchWL.Listener.GetBlueService;
import com.jetec.Monitor.SwitchWL.Listener.GetStatus;
import com.jetec.Monitor.SwitchWL.Listener.LoadListener;
import com.jetec.Monitor.SwitchWL.SQL.SQLData;
import org.json.JSONArray;
import org.json.JSONException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class SwitchViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        BlueServiceListener, LoadListener {

    private String TAG = "SwitchViewActivity";
    private LogMessage logMessage = new LogMessage();
    private SwitchView switchView = new SwitchView();
    private Parase parase = new Parase();
    private GetBlueService getBlueService = new GetBlueService();
    private GetStatus getStatus = new GetStatus();
    private SQLData sqlData = new SQLData(this);
    private Vibrator vibrator;
    private Intent intents;
    private String BID;
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private ArrayList<String> ItemList;
    private SendValue sendValue;
    private TextView textView2;
    private LinearLayout linearLayout, linearLayout2;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getBlueService.setListener(this);
        get_intent();

        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            s_connect = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            if (s_connect)
                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            else
                Log.e(TAG, "連線失敗");
        }

        show_device();
    }

    private void get_intent() {
        ItemList = new ArrayList<>();
        ItemList.clear();
        mHandler = new Handler();

        Intent intent = getIntent();
        BID = intent.getStringExtra("BID");
        ItemList = intent.getStringArrayListExtra("selectItem");
    }

    private void show_device() {
        setContentView(R.layout.switchdrawerlayout);

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

        if (!Value.downlog) {
            navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.start) + getString(R.string.LOG));
        } else {
            navigationView.getMenu().findItem(R.id.nav_share).setTitle(getString(R.string.end) + getString(R.string.LOG));
        }

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

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout2 = findViewById(R.id.linearLayout2);
        textView2 = findViewById(R.id.textView2);

        String name = ItemList.get(0);
        name = name.replace("NAME", "");
        textView2.setText(name);
        logMessage.showmessage(TAG, "BID = " + BID);
        logMessage.showmessage(TAG, "ItemList = " + ItemList);
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
                    if (text.contains("NAME")) {
                        ItemList.set(0, text);
                        text = text.replace("NAME", "");
                        textView2.setText(text);
                    } else {
                        String[] hexvalue = parase.hexstring(txValue);
                        StringBuilder str = new StringBuilder();
                        for (String aHexvalue : hexvalue) {
                            str.append(aHexvalue);
                        }
                        int row = Integer.valueOf(str.toString().substring(0, 2));
                        ItemList.set(row, str.toString());
                        switchView.resetView(SwitchViewActivity.this, vibrator, str.toString(), sendValue);
                    }
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
            sendValue = new SendValue(mBluetoothLeService);
            getBlueService.getbluetooth();
            logMessage.showmessage(TAG, "進入連線");
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            logMessage.showmessage(TAG, "失去連線端");
        }
    };

    private void saveList() {
        String devicename = Value.device;
        JSONArray savejson = new JSONArray(ItemList);
        logMessage.showmessage(TAG, "devicename = " + devicename);
        logMessage.showmessage(TAG, "savejson = " + savejson.toString());
        DataDialog dataDialog = new DataDialog();
        dataDialog.setDialog(this, vibrator, devicename, savejson.toString(), sqlData);
    }

    private void loadList() {
        String devicename = Value.device;
        getStatus.setListener(this);
        LoadDialog loadDialog = new LoadDialog();
        loadDialog.setDialog(this, vibrator, getStatus, sqlData, devicename);
    }

    private void sendloadlist(JSONArray loadlist, int count) {
        mHandler.postDelayed(() -> {
            try {
                String hexstr = loadlist.get(count).toString();
                String headstr = hexstr.substring(0, 4);
                String namestr = hexstr.substring(4);
                byte[] head = parase.hex2Byte(headstr);
                byte[] name = parase.hex2Byte(namestr);
                if (name.length != 18) {
                    byte[] check = new byte[18];
                    for (int i = 0; i < 18; i++) {
                        if (i < name.length) {
                            check[i] = name[i];
                        } else {
                            check[i] = 0x00;
                        }
                    }
                    name = check;
                }
                byte[] ready = new byte[head.length + name.length];
                System.arraycopy(head, 0, ready, 0, head.length);
                System.arraycopy(name, 0, ready, head.length, name.length);
                sendValue.sendbyte(ready);
                mHandler.removeCallbacksAndMessages(null);
                sendloadlist(loadlist, (count + 1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, 100);

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
            Value.connected = false;
            disconnect();
            return true;
        }
        return true;
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
    protected void onStop() {
        super.onStop();
        logMessage.showmessage(TAG, "onStop");
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
        mHandler.removeCallbacksAndMessages(null);
        sqlData.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMessage.showmessage(TAG, "onPause");
        if (s_connect)
            unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logMessage.showmessage(TAG, "onResume");
        logMessage.showmessage(TAG, "s_connect = " + s_connect);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(BID);
            logMessage.showmessage(TAG, "Connect request result=" + result);
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
        } else if (id == R.id.showdialog) {
            vibrator.vibrate(100);
        } else if (id == R.id.nav_share) {
            vibrator.vibrate(100);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void stay() {
        linearLayout.setOnClickListener(v -> {
            vibrator.vibrate(100);
            NameDialog nameDialog = new NameDialog();
            nameDialog.setDialog(SwitchViewActivity.this, vibrator, sendValue);
        });

        switchView.setView(this, vibrator, linearLayout2, ItemList, sendValue);
    }

    @Override
    public void update(String savelist) {
        try {
            logMessage.showmessage(TAG, "ItemList = " + ItemList);
            JSONArray listjson = new JSONArray(savelist);
            int count = 1;
            sendloadlist(listjson, count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
