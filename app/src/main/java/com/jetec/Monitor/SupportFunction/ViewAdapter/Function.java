package com.jetec.Monitor.SupportFunction.ViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jetec.Monitor.R;
import java.util.List;

public class Function extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> list_d_function;
    private List<String> list_d_num;

    public Function(Context context, List<String> list_d_function, List<String> list_d_num) {
        inflater = LayoutInflater.from(context);
        this.list_d_function = list_d_function;
        this.list_d_num = list_d_num;
    }

    @Override
    public int getCount() {
        return list_d_function.size();
    }

    @Override
    public Object getItem(int position) {
        return list_d_function.get(position);
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
            view = (ViewGroup) inflater.inflate(R.layout.show_device_funtion, null);
        }

        String name = list_d_function.get(position);
        String num = list_d_num.get(position);
        final TextView device_name = view.findViewById(R.id.device_name);
        final TextView device_num = view.findViewById(R.id.device_num);
        //device_name.setPadding((int)(all_Width/8),0,0,0);
        //device_num.setPadding(0,0,(int)(all_Width/8),0);
        device_name.setVisibility(View.VISIBLE);
        device_num.setVisibility(View.VISIBLE);
        device_name.setText(name);
        device_num.setText(num);
        return view;
    }
}
