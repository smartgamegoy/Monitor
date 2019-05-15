package com.jetec.Monitor.SupportFunction;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Screen {

    private Context context;

    public Screen(Context context){
        this.context = context;
    }

    public DisplayMetrics size(){
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /*
     *         screen = new Screen(this);
     *         DisplayMetrics dm = screen.size();
     *         double all_Width = dm.widthPixels;
     *         double all_Height = dm.heightPixels;
     *         Log.d(TAG, "Screen : height : " + dm.heightPixels + "dp" + " width : " + dm.widthPixels + "dp");
     */
}
