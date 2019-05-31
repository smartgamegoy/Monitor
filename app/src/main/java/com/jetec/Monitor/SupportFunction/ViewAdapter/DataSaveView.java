package com.jetec.Monitor.SupportFunction.ViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.Screen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSaveView extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<HashMap<String, String>> list;
    private List<String> name;
    private DisplayMetrics dm;

    public DataSaveView(Context context, ArrayList<HashMap<String, String>> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        Screen screen = new Screen(context);
        dm = screen.size();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewGroup view;

        name = new ArrayList<>();
        name.clear();

        if (convertView != null) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) inflater.inflate(R.layout.show_datalist, null);
        }

        TextView show_name = view.findViewById(R.id.show_name);

        for (HashMap<String, String> map : list) {
            name.add(map.get("num"));
        }

        String show = name.get(position);
        show_name.setPadding((dm.widthPixels / 8), 0, 0, 0);
        show_name.setVisibility(View.VISIBLE);
        show_name.setText(show);
        return view;
    }
}
