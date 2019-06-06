package com.jetec.Monitor.SupportFunction.ViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.GetUnit;
import com.jetec.Monitor.SupportFunction.Value;
import java.util.ArrayList;
import java.util.List;

public class LogListData extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> timeList;
    private ArrayList<List<String>> valueList;
    private GetUnit getUnit = new GetUnit();

    public LogListData(Context context, ArrayList<List<String>> valueList, ArrayList<String> timeList) {
        this.context = context;
        this.valueList = valueList;
        this.timeList = timeList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Object getItem(int position) {
        return timeList.get(position);
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
            view = (ViewGroup) inflater.inflate(R.layout.showdata, null);
        }

        TextView time = view.findViewById(R.id.textView1);  //時間
        TextView value = view.findViewById(R.id.textView2); //ID
        TextView first = view.findViewById(R.id.textView3); //第幾排的資料
        TextView second = view.findViewById(R.id.textView4);
        TextView third = view.findViewById(R.id.textView5);
        TextView fourth = view.findViewById(R.id.textView6);
        TextView fifth = view.findViewById(R.id.textView7);
        TextView sixth = view.findViewById(R.id.textView8);

        time.setVisibility(View.GONE);
        value.setVisibility(View.GONE);
        first.setVisibility(View.GONE);
        second.setVisibility(View.GONE);
        third.setVisibility(View.GONE);
        fourth.setVisibility(View.GONE);
        fifth.setVisibility(View.GONE);
        sixth.setVisibility(View.GONE);

        List<TextView> textList = new ArrayList<>();
        textList.clear();

        textList.add(time);
        textList.add(value);
        textList.add(first);
        textList.add(second);
        textList.add(third);
        textList.add(fourth);
        textList.add(fifth);
        textList.add(sixth);

        setTextview(position, textList);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setTextview(int position, List<TextView> textList) {
        TextView time = textList.get(0);
        TextView id = textList.get(1);
        time.setVisibility(View.VISIBLE);
        id.setVisibility(View.VISIBLE);
        time.setText(context.getString(R.string.datetime) + " : " + timeList.get(position));
        id.setText(context.getString(R.string.size) + " : " + String.valueOf((position + 1)));
        for (int i = 0; i < valueList.size(); i++) {
            TextView row = textList.get(i + 2);
            row.setVisibility(View.VISIBLE);
            row.setText(getUnit.getName(context, Value.getviewlist.get(i * 4)) + " : " + valueList.get(i).get(position) +
                    " " + getUnit.unit(Value.getviewlist.get(i * 4)));
        }
    }
}
