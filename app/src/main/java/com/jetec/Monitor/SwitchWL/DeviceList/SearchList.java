package com.jetec.Monitor.SwitchWL.DeviceList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jetec.Monitor.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchList extends BaseAdapter {

    private String TAG = "SearchList";
    private LayoutInflater inflater;
    private Map<Integer, List<String>> viewList;
    private Context context;

    public SearchList(Context context, Map<Integer, List<String>> setList){
        this.context = context;
        this.viewList = setList;
        inflater = LayoutInflater.from(context);
    }

    public void resetList(Map<Integer, List<String>> setList){
        this.viewList = setList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object getItem(int position) {
        return viewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.search_device, null);
        }

        TextView textView = view.findViewById(R.id.search_device);
        TextView textView2 = view.findViewById(R.id.device_address);
        LinearLayout linearLayout1 = view.findViewById(R.id.linearlayout1);
        LinearLayout linearLayout2 = view.findViewById(R.id.linearlayout2);
        LinearLayout linearLayout3 = view.findViewById(R.id.linearlayout3);
        LinearLayout linearLayout4 = view.findViewById(R.id.linearlayout4);
        LinearLayout linearLayout5 = view.findViewById(R.id.linearlayout5);
        LinearLayout linearLayout6 = view.findViewById(R.id.linearlayout6);
        TextView text1 = view.findViewById(R.id.textView1);
        TextView text2 = view.findViewById(R.id.textView2);
        TextView text3 = view.findViewById(R.id.textView3);
        TextView text4 = view.findViewById(R.id.textView4);
        TextView text5 = view.findViewById(R.id.textView5);
        TextView text6 = view.findViewById(R.id.textView6);
        TextView text7 = view.findViewById(R.id.textView7);
        TextView text8 = view.findViewById(R.id.textView8);
        TextView text9 = view.findViewById(R.id.textView9);
        TextView text10 = view.findViewById(R.id.textView10);
        TextView text11 = view.findViewById(R.id.textView11);
        TextView text12 = view.findViewById(R.id.textView12);
        TextView text13 = view.findViewById(R.id.textView13);   //power
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);
        linearLayout4.setVisibility(View.GONE);
        linearLayout5.setVisibility(View.GONE);
        linearLayout6.setVisibility(View.GONE);
        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        text3.setVisibility(View.GONE);
        text4.setVisibility(View.GONE);
        text5.setVisibility(View.GONE);
        text6.setVisibility(View.GONE);
        text7.setVisibility(View.GONE);
        text8.setVisibility(View.GONE);
        text9.setVisibility(View.GONE);
        text10.setVisibility(View.GONE);
        text11.setVisibility(View.GONE);
        text12.setVisibility(View.GONE);

        List<String> getList = new ArrayList<>();
        getList.clear();
        getList = viewList.get(position);
        assert getList != null;
        textView.setText(getList.get(0));
        textView2.setText(getList.get(1));
        int power = Integer.valueOf(getList.get(2));
        if(power > 100){
            text13.setText(context.getString(R.string.power) + "100%");
        }
        else if(power < 0){
            text13.setText("");
        }
        else {
            text13.setText(context.getString(R.string.power) + power + "%");
        }
        int count = Integer.valueOf(getList.get(3));

        List<LinearLayout> linearLayoutList = new ArrayList<>();
        linearLayoutList.clear();
        linearLayoutList.add(linearLayout1);
        linearLayoutList.add(linearLayout2);
        linearLayoutList.add(linearLayout3);
        linearLayoutList.add(linearLayout4);
        linearLayoutList.add(linearLayout5);
        linearLayoutList.add(linearLayout6);

        List<TextView> textViewList = new ArrayList<>();
        textViewList.clear();
        textViewList.add(text1);
        textViewList.add(text2);
        textViewList.add(text3);
        textViewList.add(text4);
        textViewList.add(text5);
        textViewList.add(text6);
        textViewList.add(text7);
        textViewList.add(text8);
        textViewList.add(text9);
        textViewList.add(text10);
        textViewList.add(text11);
        textViewList.add(text12);

        List<String> valueList = new ArrayList<>();
        valueList.clear();
        for(int i = 4; i < getList.size(); i++){
            valueList.add(getList.get(i));
        }

        setView(count, linearLayoutList, textViewList, valueList);

        return view;
    }

    private void setView(int count, List<LinearLayout> linearLayoutList, List<TextView> textViewList, List<String> valueList){
        for(int i = 0; i < count; i++){
            linearLayoutList.get(i).setVisibility(View.VISIBLE);
            linearLayoutList.get(i).setBackgroundResource(R.drawable.co2);
        }

        for(int i = 0; i < (count * 2); i = i + 2){
            int j = i + 1;
            textViewList.get(i).setVisibility(View.VISIBLE);
            textViewList.get(i).setText(valueList.get(i));
            textViewList.get(j).setVisibility(View.VISIBLE);
            textViewList.get(j).setText(valueList.get(j));
        }
    }
}
