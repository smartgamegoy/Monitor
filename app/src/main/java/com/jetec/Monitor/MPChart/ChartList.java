package com.jetec.Monitor.MPChart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.jetec.Monitor.R;
import java.util.ArrayList;

public class ChartList extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private double all_Width, all_Height;
    private ArrayList<String> chartlist;

    public ChartList(Context context, double all_Width, double all_Height, ArrayList<String> chartlist) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.all_Width = all_Width;
        this.all_Height = all_Height;
        this.chartlist = chartlist;
    }

    @Override
    public int getCount() {
        return chartlist.size();
    }

    @Override
    public Object getItem(int position) {
        return chartlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewGroup view;

        if (convertView != null) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) inflater.inflate(R.layout.chartlist, null);
        }

        String name = chartlist.get(position);
        final TextView chart_name = ((TextView) view.findViewById(R.id.chart_name));
        chart_name.setPadding((int)(all_Width/8),0,0,0);
        chart_name.setVisibility(View.VISIBLE);
        chart_name.setText(name);
        return view;
    }
}
