package com.jetec.Monitor.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.SQL.DataListSQL;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.ViewAdapter.DataSaveView;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSaveDialog {

    private String TAG = "DataSaveDialog";
    private ArrayList<HashMap<String, String>> listData;
    private DataSaveView dataSaveView;
    private int select_item;
    private Vibrator vibrator;
    private ArrayList<String> SQLdata;
    private View view1;
    private Handler mHandler;

    public DataSaveDialog() {
        super();
    }

    public void setDialog(Context context, Vibrator vibrator, String devicename, String saveList, DataListSQL dataListSQL) {
        this.vibrator = vibrator;
        listData = new ArrayList<>();
        SQLdata = new ArrayList<>();
        listData.clear();
        SQLdata.clear();
        mHandler = new Handler();
        Dialog progressDialog = showDialog(context, vibrator, devicename, saveList, dataListSQL);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private Dialog showDialog(Context context, Vibrator vibrator, String devicename, String saveList, DataListSQL dataListSQL) {
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.savedatalist, null);
        Button close = v.findViewById(R.id.button1);
        EditText name = v.findViewById(R.id.editText);
        Button add = v.findViewById(R.id.button2);
        Button del = v.findViewById(R.id.button3);
        Button update = v.findViewById(R.id.button4);
        ListView list = v.findViewById(R.id.datalist1);
        TextView t1 = v.findViewById(R.id.no_list);

        setStatus(context, dataListSQL, list, t1, devicename);

        close.setOnClickListener(v14 -> {
            vibrator.vibrate(100);
            progressDialog.dismiss();
        });

        update.setOnClickListener(v1 -> {
            vibrator.vibrate(100);
            if (dataListSQL.getCount() != 0) {
                if (select_item != -1) {
                    UpdateDataDialog updateDateDialog = new UpdateDataDialog();
                    updateDateDialog.setDialog(context, vibrator, select_item, SQLdata, list, dataListSQL, devicename);
                }
            }
        });

        add.setOnClickListener(v13 -> {
            vibrator.vibrate(100);
            String listname = name.getText().toString().trim();
            if (listname.matches(""))
                Toast.makeText(context, context.getString(R.string.addblank), Toast.LENGTH_SHORT).show();
            else {
                if (dataListSQL.getCount(listname, devicename) == 0) {
                    dataListSQL.insert(saveList, devicename, listname);
                    SQLdata.clear();
                    list.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.GONE);
                    name.setText("");
                    mHandler.postDelayed(() -> {
                        listData = dataListSQL.fillList(devicename);
                        Log.e(TAG, "listData = " + listData);
                        dataSaveView = new DataSaveView(context, listData);
                        list.setAdapter(dataSaveView);
                        setStatus(context, dataListSQL, list, t1, devicename);
                    }, 100);
                } else {
                    Toast.makeText(context, context.getString(R.string.same), Toast.LENGTH_SHORT).show();
                }
            }
        });

        del.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            Log.e(TAG, "select = " + select_item);
            if (select_item != -1) {
                dataListSQL.delete(Integer.valueOf(SQLdata.get(select_item)));
                SQLdata.remove(select_item);
                Log.e(TAG, "SQLdata = " + SQLdata);
                if (dataListSQL.getCount() == 0) {
                    list.setVisibility(View.GONE);
                    t1.setVisibility(View.VISIBLE);
                    select_item = -1;
                } else {
                    if (dataListSQL.modelsearch(devicename) > 0) {
                        listData = dataListSQL.fillList(devicename);
                        dataSaveView = new DataSaveView(context, listData);
                        list.setAdapter(dataSaveView);
                        select_item = -1;
                    } else {
                        list.setVisibility(View.GONE);
                        t1.setVisibility(View.VISIBLE);
                        select_item = -1;
                    }
                }
            }
        });

        if (dm.heightPixels > dm.widthPixels) {
            progressDialog.setContentView(v, new ConstraintLayout.LayoutParams((4 * dm.widthPixels / 5),
                    (2 * dm.heightPixels / 3)));
        } else {
            progressDialog.setContentView(v, new ConstraintLayout.LayoutParams((2 * dm.widthPixels / 5),
                    (5 * dm.heightPixels / 6)));
        }

        return progressDialog;
    }

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            vibrator.vibrate(100);
            //一開始未選擇任何一個item所以為-1
            //======================
            //點選某個item並呈現被選取的狀態
            if ((select_item == -1) || (select_item == position)) {
                view.setBackgroundColor(Color.YELLOW); //為View加上選取效果
            } else {
                //noinspection deprecation
                view1.setBackgroundDrawable(null); //將上一次點選的View保存在view1
                view.setBackgroundColor(Color.YELLOW); //為View加上選取效果
            }
            view1 = view; //保存點選的View
            select_item = position;//保存目前的View位置
            //======================
            if (SQLdata.size() == 0) {
                for (HashMap<String, String> map : listData) {
                    SQLdata.add(map.get("id"));
                }
            } else {
                int i = 0;
                for (HashMap<String, String> map : listData) {
                    SQLdata.set(i, map.get("id"));
                    i++;
                }
            }
        }
    };

    private void setStatus(Context context, DataListSQL dataListSQL, ListView list, TextView t1, String devicename){
        if (dataListSQL.getCount() == 0) {
            list.setVisibility(View.GONE);
            t1.setVisibility(View.VISIBLE);
        } else {
            if (dataListSQL.modelsearch(devicename) > 0) {
                select_item = -1;
                list.setVisibility(View.VISIBLE);
                t1.setVisibility(View.GONE);
                listData = dataListSQL.fillList(devicename);
                dataSaveView = new DataSaveView(context, listData);
                list.setAdapter(dataSaveView);
                list.setOnItemClickListener(mListClickListener);
            } else {
                list.setVisibility(View.GONE);
                t1.setVisibility(View.VISIBLE);
            }
        }
    }
}
