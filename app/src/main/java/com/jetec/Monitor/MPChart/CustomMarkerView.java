package com.jetec.Monitor.MPChart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.jetec.Monitor.R;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private double all_Width, all_Height;
    private String TAG = "CustomMarkerView";

    public CustomMarkerView(Context context, int layoutResource, double all_Width, double all_Height) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = findViewById(R.id.tvContent);
        this.all_Width = all_Width;
        this.all_Height = all_Height;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(" " + e.getY()); // set the entry-value as the display text
        Log.e(TAG,"e.getY() = " + e.getY());
        Log.e(TAG,"e.getY() = " + String.valueOf(e.getY()).length());
        if(e.getY() > 0) {
            if (String.valueOf(e.getY()).length() == 6)
                tvContent.setPadding(0, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 5)
                tvContent.setPadding(0, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 4)
                tvContent.setPadding((int) all_Width / 80, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 3)
                tvContent.setPadding((int) all_Width / 40, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 2)
                tvContent.setPadding((int) all_Width / 20, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 1)
                tvContent.setPadding((int) all_Width / 10, (int) all_Height / 50, 0, 0);
        }
        else{
            if (String.valueOf(e.getY()).length() == 7)
                tvContent.setPadding(0, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 6)
                tvContent.setPadding(0, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 5)
                tvContent.setPadding((int) all_Width / 80, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 4)
                tvContent.setPadding((int) all_Width / 40, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 3)
                tvContent.setPadding((int) all_Width / 20, (int) all_Height / 50, 0, 0);
            if (String.valueOf(e.getY()).length() == 2)
                tvContent.setPadding((int) all_Width / 10, (int) all_Height / 90, 0, 0);
        }
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        // this will center the marker-view horizontally
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF((float) (-(getWidth() - all_Width / 20) / 2), (float)(-getHeight() - all_Height / 90));
            Log.e(TAG, "getWidth() = " + getWidth());
            Log.e(TAG, "getHeight() = " + getHeight());
        }

        return mOffset;
    }


}
