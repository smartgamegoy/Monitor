package com.jetec.Monitor.SupportFunction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jetec.Monitor.R;

public class Runningpdf {

    private Context context;
    private Dialog processing = null;
    private Screen screen;
    private boolean check = false;

    public Runningpdf(Context context){
        this.context = context;
        screen = new Screen(context);
    }

    public void startFlash(String message){
        processing = showDialog(context, message);
        processing.show();
        processing.setCanceledOnTouchOutside(false);
    }

    public void closeFlash(){
        check = false;
        processing.dismiss();
    }

    private Dialog showDialog(Context context, String message){
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.running, null);

        LinearLayout layout = v.findViewById(R.id.showDialog);
        ProgressBar pb_progress_bar = v.findViewById(R.id.progressCircle);
        pb_progress_bar.setVisibility(View.VISIBLE);
        TextView tv = v.findViewById(R.id.message);

        if (message == null || message.equals("")) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setText(message);
            tv.setTextColor(context.getResources().getColor(R.color.colorBackground));
        }

        DisplayMetrics dm = screen.size();

        if(dm.heightPixels > dm.widthPixels) {
            progressDialog.setContentView(v, new LinearLayout.LayoutParams(dm.widthPixels, dm.heightPixels));
        }
        else {
            progressDialog.setContentView(v, new LinearLayout.LayoutParams(dm.widthPixels, dm.heightPixels));
        }

        check = true;

        return progressDialog;
    }

    public boolean isCheck() {
        return check;
    }
}
