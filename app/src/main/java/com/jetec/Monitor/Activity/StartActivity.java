package com.jetec.Monitor.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.*;
import com.jetec.Monitor.SupportFunction.SQL.SaveLogSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_ENABLE_BT = 1;
    private String TAG = "StartActivity";
    private Vibrator vibrator;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //未取得權限，向使用者要求允許權限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            } else {    //已有權限，可進行檔案存取
                startmenu();
            }
        }
        else {
            startmenu();
        }
    }

    private void startmenu(){
        setContentView(R.layout.firstpage);

        Button btn = findViewById(R.id.button);
        ButtonStyle buttonStyle = new ButtonStyle(this);
        buttonStyle.setStyle(btn);

        btn.setOnClickListener(v -> {
            vibrator.vibrate(100);
            requeststorage();
        });
    }

    private void scan_ble(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isLocationEnable(this))  {
                final AlertDialog.Builder show_mess = new AlertDialog.Builder(this);
                final AlertDialog alertDialog = show_mess.show();
                show_mess.setTitle(getString(R.string.mes_title));
                show_mess.setMessage(getString(R.string.mes_mess));
                show_mess.setPositiveButton(getString(R.string.mes_yes), (dialog, which) -> {
                    Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
                    alertDialog.dismiss();
                });
                show_mess.setNegativeButton(getString(R.string.mes_no), (dialog, which) -> alertDialog.dismiss());
                show_mess.show();
            }
            else {
                BluetoothManager bluetoothManager = getManager(this);
                if (bluetoothManager != null) {
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                }
                if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled())) {
                    Toast.makeText(this, getString(R.string.BLE_adp), Toast.LENGTH_SHORT).show();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                else {
                    Toast.makeText(this, getString(R.string.search_device), Toast.LENGTH_SHORT).show();
                    show_device();
                }
            }
        }
        else {
            BluetoothManager bluetoothManager = getManager(this);
            if (bluetoothManager != null) {
                mBluetoothAdapter = bluetoothManager.getAdapter();
            }
            if ((mBluetoothAdapter == null) || (!mBluetoothAdapter.isEnabled())) {
                Toast.makeText(this, getString(R.string.BLE_adp), Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                Toast.makeText(this, getString(R.string.search_device), Toast.LENGTH_LONG).show();
                show_device();
            }
        }
    }

    private void show_device(){
        Intent intent = new Intent(this, FirstActivity.class);  //FirstActivity
        /*Value.device = "BT-2-THL-0";
        Value.model_num = 2;
        Value.model_name = "THL";
        Value.model_relay = 0;
        List<String> simulatList = new ArrayList<>();
        simulatList.clear();
        simulatList.add("9");
        simulatList.add("0");
        simulatList.add("10˚C");
        simulatList.add("10˚C");
        simulatList.add("11");
        simulatList.add("0");
        simulatList.add("10%");
        simulatList.add("10%");
        Value.getviewlist = simulatList;
        Value.device_name = "研發";
        //以上均為模擬資料
        */
        intent.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(intent);
        finish();
    }

    private BluetoothManager getManager(Context context) {    //獲取此設備默認藍芽適配器
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    private boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return networkProvider || gpsProvider;
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK: {
                vibrator.vibrate(100);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        //.setIcon(R.drawable.icon)
                        .setMessage(R.string.app_message)
                        .setPositiveButton(R.string.app_message_b1, (dialog, which) -> finish())
                        .setNegativeButton(R.string.app_message_b2, (dialog, which) -> {
                            // TODO Auto-generated method stub
                        }).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startmenu();
                    //取得聯絡人權限，進行工作
                } else {
                    finish();
                    //使用者拒絕權限，顯示對話框告知
                }
            }
            break;
        }
    }

    private void requeststorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //未取得權限，向使用者要求允許權限
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_EXTERNAL_STORAGE);
            } else {
                scan_ble();
                //已有權限，可進行工作
            }
        } else {
            scan_ble();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
