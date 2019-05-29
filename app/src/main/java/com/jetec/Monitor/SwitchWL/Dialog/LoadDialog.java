package com.jetec.Monitor.SwitchWL.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SwitchWL.DeviceList.DataListView;
import com.jetec.Monitor.SwitchWL.Listener.GetStatus;
import com.jetec.Monitor.SwitchWL.SQL.SQLData;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadDialog {

    private String TAG = "LoadDialog";
    private Vibrator vibrator;
    private int select_item;
    private View view1;
    private ArrayList<HashMap<String, String>> listData;
    private ArrayList<String> SQLdata;

    public LoadDialog(){
        super();
    }

    public void setDialog(Context context, Vibrator vibrator, GetStatus getStatus, SQLData sqlData, String devicename){
        this.vibrator = vibrator;
        listData = new ArrayList<>();
        SQLdata = new ArrayList<>();
        listData.clear();
        SQLdata.clear();
        Dialog progressDialog = showDialog(context, vibrator, getStatus, sqlData, devicename);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private Dialog showDialog(Context context, Vibrator vibrator, GetStatus getStatus, SQLData sqlData, String devicename){
        Screen screen = new Screen(context);
        DisplayMetrics dm = screen.size();
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.loading, null);
        ConstraintLayout setlinear = v.findViewById(R.id.loadlist);
        Button by = v.findViewById(R.id.button2);
        Button bn = v.findViewById(R.id.button1);
        ListView list = v.findViewById(R.id.loading);
        TextView t1 = v.findViewById(R.id.no_list);

        if (sqlData.getCount() == 0) {
            list.setVisibility(View.GONE);
            t1.setVisibility(View.VISIBLE);
        } else {
            if (sqlData.modelsearch(devicename) > 0) {
                list.setVisibility(View.VISIBLE);
                t1.setVisibility(View.GONE);
                select_item = -1;
                listData = sqlData.fillList(devicename);
                DataListView dataListView = new DataListView(context, listData);
                list.setAdapter(dataListView);
                list.setOnItemClickListener(mLoadClickListener);
            } else {
                list.setVisibility(View.GONE);
                t1.setVisibility(View.VISIBLE);
            }
        }

        bn.setOnClickListener(v1 -> {
            vibrator.vibrate(100);
            progressDialog.dismiss();
        });

        by.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            if(select_item != -1) {
                vibrator.vibrate(100);
                listData = sqlData.fillList(devicename);
                HashMap<String, String> getitem = new HashMap<>();
                getitem.clear();
                getitem = listData.get(select_item);
                String savelist = getitem.get("savelist");
                getStatus.readytointent(savelist);
                progressDialog.dismiss();
                /*new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.warning))
                        .setMessage(context.getString(R.string.warn))
                        .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                            vibrator.vibrate(100);
                            listData = sqlData.fillList(devicename);
                            HashMap<String, String> getitem = new HashMap<>();
                            getitem.clear();
                            getitem = listData.get(select_item);
                            String savelist = getitem.get("savelist");
                            String numlist = getitem.get("numlist");
                            getStatus.readytointent(savelist, numlist);
                            progressDialog.dismiss();
                        })
                        .setNeutralButton(R.string.butoon_no, (dialog, which) -> {
                            vibrator.vibrate(100);
                        })
                        .show();*/
            }
        });

        if (dm.heightPixels > dm.widthPixels) {
            progressDialog.setContentView(setlinear, new ConstraintLayout.LayoutParams((3 * dm.widthPixels / 4),
                    (dm.heightPixels / 2)));
        } else {
            progressDialog.setContentView(setlinear, new ConstraintLayout.LayoutParams((dm.widthPixels / 2),
                    (5 * dm.heightPixels / 6)));
        }

        return progressDialog;
    }

    private AdapterView.OnItemClickListener mLoadClickListener = new AdapterView.OnItemClickListener() {

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
            Log.e(TAG, "select_item = " + select_item);
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
}
