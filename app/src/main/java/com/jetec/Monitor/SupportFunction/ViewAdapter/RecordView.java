package com.jetec.Monitor.SupportFunction.ViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.LogMessage;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class RecordView extends BaseAdapter {

    private Context context;
    private LogMessage logMessage = new LogMessage();
    private String TAG = "RecordView";
    private LayoutInflater inflater;
    private ArrayList<List<String>> recordList;

    public RecordView(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setRecordList(ArrayList<List<String>> recordList) {
        this.recordList = new ArrayList<>();
        this.recordList.clear();
        this.recordList = recordList;
        logMessage.showmessage(TAG,"recordList = " + this.recordList);
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewGroup view;

        if (convertView != null) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) inflater.inflate(R.layout.recordlist, null);
        }

        try {
            int count;

            TextView textView1 = view.findViewById(R.id.textView1); //device name
            TextView textView2 = view.findViewById(R.id.textView2); //record time
            TextView textView3_1 = view.findViewById(R.id.textView3_1); //unit
            TextView textView3_2 = view.findViewById(R.id.textView3_2); //value
            TextView textView3 = view.findViewById(R.id.textView3); //alert value
            TextView textView4_1 = view.findViewById(R.id.textView4_1); //unit
            TextView textView4_2 = view.findViewById(R.id.textView4_2); //value
            TextView textView4_3 = view.findViewById(R.id.textView4_3); //title
            TextView textView4 = view.findViewById(R.id.textView4); //alert value
            TextView textView5_1 = view.findViewById(R.id.textView5_1); //unit
            TextView textView5_2 = view.findViewById(R.id.textView5_2); //value
            TextView textView5_3 = view.findViewById(R.id.textView5_3); //title
            TextView textView5 = view.findViewById(R.id.textView5); //alert value
            TextView textView6_1 = view.findViewById(R.id.textView6_1); //unit
            TextView textView6_2 = view.findViewById(R.id.textView6_2); //value
            TextView textView6_3 = view.findViewById(R.id.textView6_3); //title
            TextView textView6 = view.findViewById(R.id.textView6); //alert value
            TextView textView7_1 = view.findViewById(R.id.textView7_1); //unit
            TextView textView7_2 = view.findViewById(R.id.textView7_2); //value
            TextView textView7_3 = view.findViewById(R.id.textView7_3); //title
            TextView textView7 = view.findViewById(R.id.textView7); //alert value
            TextView textView8_1 = view.findViewById(R.id.textView8_1); //unit
            TextView textView8_2 = view.findViewById(R.id.textView8_2); //value
            TextView textView8_3 = view.findViewById(R.id.textView8_3); //title
            TextView textView8 = view.findViewById(R.id.textView8); //alert value
            TextView textView12 = view.findViewById(R.id.textView12);
            TextView textView13 = view.findViewById(R.id.textView13);
            textView13.setVisibility(View.GONE);

            List<String> list = new ArrayList<>();
            list.clear();
            list = recordList.get(position);
            logMessage.showmessage(TAG,"list = " + list);
            JSONArray jsonArray = new JSONArray(list.get(3));

            textView1.setText(list.get(0));
            textView2.setText(list.get(1));
            textView12.setText(list.get(2));

            /*if(sort) {
                if (device_address.matches("")) {
                    device_address = list.get(2);
                } else {
                    if (!list.get(2).matches(device_address)) {
                        textView13.setVisibility(View.VISIBLE);
                        device_address = list.get(2);
                        sort = false;
                    }
                }
            }*/

            count = jsonArray.length() / 4;

            switch (count) {
                case 1: {
                    textView4_1.setVisibility(View.GONE);
                    textView4_2.setVisibility(View.GONE);
                    textView4_3.setVisibility(View.GONE);
                    textView4.setVisibility(View.GONE);
                    textView5_1.setVisibility(View.GONE);
                    textView5_2.setVisibility(View.GONE);
                    textView5_3.setVisibility(View.GONE);
                    textView5.setVisibility(View.GONE);
                    textView6_1.setVisibility(View.GONE);
                    textView6_2.setVisibility(View.GONE);
                    textView6_3.setVisibility(View.GONE);
                    textView6.setVisibility(View.GONE);
                    textView7_1.setVisibility(View.GONE);
                    textView7_2.setVisibility(View.GONE);
                    textView7_3.setVisibility(View.GONE);
                    textView7.setVisibility(View.GONE);
                    textView8_1.setVisibility(View.GONE);
                    textView8_2.setVisibility(View.GONE);
                    textView8_3.setVisibility(View.GONE);
                    textView8.setVisibility(View.GONE);

                    textView3_1.setText(getunit(jsonArray.get(0).toString()));
                    textView3_2.setText(jsonArray.get(1).toString());
                    textView3.setText(jsonArray.get(2).toString());
                    if(jsonArray.get(3).toString().matches("true")){
                        textView3_2.setTextColor(Color.RED);
                    }
                    break;
                }
                case 2: {
                    textView5_1.setVisibility(View.GONE);
                    textView5_2.setVisibility(View.GONE);
                    textView5_3.setVisibility(View.GONE);
                    textView5.setVisibility(View.GONE);
                    textView6_1.setVisibility(View.GONE);
                    textView6_2.setVisibility(View.GONE);
                    textView6_3.setVisibility(View.GONE);
                    textView6.setVisibility(View.GONE);
                    textView7_1.setVisibility(View.GONE);
                    textView7_2.setVisibility(View.GONE);
                    textView7_3.setVisibility(View.GONE);
                    textView7.setVisibility(View.GONE);
                    textView8_1.setVisibility(View.GONE);
                    textView8_2.setVisibility(View.GONE);
                    textView8_3.setVisibility(View.GONE);
                    textView8.setVisibility(View.GONE);

                    textView3_1.setText(getunit(jsonArray.get(0).toString()));
                    textView3_2.setText(jsonArray.get(1).toString());
                    textView3.setText(jsonArray.get(2).toString());
                    if(jsonArray.get(3).toString().matches("true")){
                        textView3_2.setTextColor(Color.RED);
                    }

                    textView4_1.setText(getunit(jsonArray.get(4).toString()));
                    textView4_2.setText(jsonArray.get(5).toString());
                    textView4.setText(jsonArray.get(6).toString());
                    if(jsonArray.get(7).toString().matches("true")){
                        textView4_2.setTextColor(Color.RED);
                    }
                    break;
                }
                case 3: {
                    textView6_1.setVisibility(View.GONE);
                    textView6_2.setVisibility(View.GONE);
                    textView6_3.setVisibility(View.GONE);
                    textView6.setVisibility(View.GONE);
                    textView7_1.setVisibility(View.GONE);
                    textView7_2.setVisibility(View.GONE);
                    textView7_3.setVisibility(View.GONE);
                    textView7.setVisibility(View.GONE);
                    textView8_1.setVisibility(View.GONE);
                    textView8_2.setVisibility(View.GONE);
                    textView8_3.setVisibility(View.GONE);
                    textView8.setVisibility(View.GONE);

                    textView3_1.setText(getunit(jsonArray.get(0).toString()));
                    textView3_2.setText(jsonArray.get(1).toString());
                    textView3.setText(jsonArray.get(2).toString());
                    if(jsonArray.get(3).toString().matches("true")){
                        textView3_2.setTextColor(Color.RED);
                    }

                    textView4_1.setText(getunit(jsonArray.get(4).toString()));
                    textView4_2.setText(jsonArray.get(5).toString());
                    textView4.setText(jsonArray.get(6).toString());
                    if(jsonArray.get(7).toString().matches("true")){
                        textView4_2.setTextColor(Color.RED);
                    }

                    textView5_1.setText(getunit(jsonArray.get(8).toString()));
                    textView5_2.setText(jsonArray.get(9).toString());
                    textView5.setText(jsonArray.get(10).toString());
                    if(jsonArray.get(11).toString().matches("true")){
                        textView5_2.setTextColor(Color.RED);
                    }
                    break;
                }
                case 4: {
                    textView7_1.setVisibility(View.GONE);
                    textView7_2.setVisibility(View.GONE);
                    textView7_3.setVisibility(View.GONE);
                    textView7.setVisibility(View.GONE);
                    textView8_1.setVisibility(View.GONE);
                    textView8_2.setVisibility(View.GONE);
                    textView8_3.setVisibility(View.GONE);
                    textView8.setVisibility(View.GONE);

                    textView3_1.setText(getunit(jsonArray.get(0).toString()));
                    textView3_2.setText(jsonArray.get(1).toString());
                    textView3.setText(jsonArray.get(2).toString());
                    if(jsonArray.get(3).toString().matches("true")){
                        textView3_2.setTextColor(Color.RED);
                    }

                    textView4_1.setText(getunit(jsonArray.get(4).toString()));
                    textView4_2.setText(jsonArray.get(5).toString());
                    textView4.setText(jsonArray.get(6).toString());
                    if(jsonArray.get(7).toString().matches("true")){
                        textView4_2.setTextColor(Color.RED);
                    }

                    textView5_1.setText(getunit(jsonArray.get(8).toString()));
                    textView5_2.setText(jsonArray.get(9).toString());
                    textView5.setText(jsonArray.get(10).toString());
                    if(jsonArray.get(11).toString().matches("true")){
                        textView5_2.setTextColor(Color.RED);
                    }

                    textView6_1.setText(getunit(jsonArray.get(12).toString()));
                    textView6_2.setText(jsonArray.get(13).toString());
                    textView6.setText(jsonArray.get(14).toString());
                    if(jsonArray.get(15).toString().matches("true")){
                        textView6_2.setTextColor(Color.RED);
                    }
                    break;
                }
                case 5: {
                    textView8_1.setVisibility(View.GONE);
                    textView8_2.setVisibility(View.GONE);
                    textView8_3.setVisibility(View.GONE);
                    textView8.setVisibility(View.GONE);

                    textView3_1.setText(getunit(jsonArray.get(0).toString()));
                    textView3_2.setText(jsonArray.get(1).toString());
                    textView3.setText(jsonArray.get(2).toString());
                    if(jsonArray.get(3).toString().matches("true")){
                        textView3_2.setTextColor(Color.RED);
                    }

                    textView4_1.setText(getunit(jsonArray.get(4).toString()));
                    textView4_2.setText(jsonArray.get(5).toString());
                    textView4.setText(jsonArray.get(6).toString());
                    if(jsonArray.get(7).toString().matches("true")){
                        textView4_2.setTextColor(Color.RED);
                    }

                    textView5_1.setText(getunit(jsonArray.get(8).toString()));
                    textView5_2.setText(jsonArray.get(9).toString());
                    textView5.setText(jsonArray.get(10).toString());
                    if(jsonArray.get(11).toString().matches("true")){
                        textView5_2.setTextColor(Color.RED);
                    }

                    textView6_1.setText(getunit(jsonArray.get(12).toString()));
                    textView6_2.setText(jsonArray.get(13).toString());
                    textView6.setText(jsonArray.get(14).toString());
                    if(jsonArray.get(15).toString().matches("true")){
                        textView6_2.setTextColor(Color.RED);
                    }

                    textView7_1.setText(getunit(jsonArray.get(16).toString()));
                    textView7_2.setText(jsonArray.get(17).toString());
                    textView7.setText(jsonArray.get(18).toString());
                    if(jsonArray.get(19).toString().matches("true")){
                        textView7_2.setTextColor(Color.RED);
                    }
                    break;
                }
                case 6: {
                    textView3_1.setText(getunit(jsonArray.get(0).toString()));
                    textView3_2.setText(jsonArray.get(1).toString());
                    textView3.setText(jsonArray.get(2).toString());
                    if(jsonArray.get(3).toString().matches("true")){
                        textView3_2.setTextColor(Color.RED);
                    }

                    textView4_1.setText(getunit(jsonArray.get(4).toString()));
                    textView4_2.setText(jsonArray.get(5).toString());
                    textView4.setText(jsonArray.get(6).toString());
                    if(jsonArray.get(7).toString().matches("true")){
                        textView4_2.setTextColor(Color.RED);
                    }

                    textView5_1.setText(getunit(jsonArray.get(8).toString()));
                    textView5_2.setText(jsonArray.get(9).toString());
                    textView5.setText(jsonArray.get(10).toString());
                    if(jsonArray.get(11).toString().matches("true")){
                        textView5_2.setTextColor(Color.RED);
                    }

                    textView6_1.setText(getunit(jsonArray.get(12).toString()));
                    textView6_2.setText(jsonArray.get(13).toString());
                    textView6.setText(jsonArray.get(14).toString());
                    if(jsonArray.get(15).toString().matches("true")){
                        textView6_2.setTextColor(Color.RED);
                    }

                    textView7_1.setText(getunit(jsonArray.get(16).toString()));
                    textView7_2.setText(jsonArray.get(17).toString());
                    textView7.setText(jsonArray.get(18).toString());
                    if(jsonArray.get(19).toString().matches("true")){
                        textView7_2.setTextColor(Color.RED);
                    }

                    textView8_1.setText(getunit(jsonArray.get(20).toString()));
                    textView8_2.setText(jsonArray.get(21).toString());
                    textView8.setText(jsonArray.get(22).toString());
                    if(jsonArray.get(23).toString().matches("true")){
                        textView8_2.setTextColor(Color.RED);
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private String getunit(String unit){
        String str = "";

        if (unit.matches("0")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("1")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("2")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("3")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("4")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("5")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("6")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("7")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("8")) {
            str = context.getString(R.string.recordP);
        } else if (unit.matches("9")) {
            str = context.getString(R.string.recordT);
        } else if (unit.matches("10")) {
            str = context.getString(R.string.recordT);
        } else if (unit.matches("11")) {
            str = context.getString(R.string.recordH);
        } else if (unit.matches("12")) {
            str = context.getString(R.string.recordCO);
        } else if (unit.matches("13")) {
            str = context.getString(R.string.recordC);
        } else if (unit.matches("14")) {
            str = context.getString(R.string.recordM);
        }else if (unit.matches("15")) {
            str = context.getString(R.string.recordpercent);
        }

        return str;
    }
}
