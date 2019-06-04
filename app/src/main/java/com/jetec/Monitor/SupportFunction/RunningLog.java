package com.jetec.Monitor.SupportFunction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.jetec.Monitor.R;
import java.util.Objects;

public class RunningLog {

    private Context context;
    private Dialog processing = null;
    private Screen screen;
    private boolean check = false;
    private TextView textView;

    public RunningLog(Context context){
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

    @SuppressLint("SetTextI18n")
    public void setTextView(int size, int count){
        textView.setText(size + " / " + count);
    }

    private Dialog showDialog(Context context, String message){
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable());

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.running, null);

        ProgressBar pb_progress_bar = v.findViewById(R.id.progressCircle);
        pb_progress_bar.setVisibility(View.VISIBLE);
        textView = v.findViewById(R.id.message);

        if (message == null || message.equals("")) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(message);
            textView.setTextColor(context.getResources().getColor(R.color.colorBackground));
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
