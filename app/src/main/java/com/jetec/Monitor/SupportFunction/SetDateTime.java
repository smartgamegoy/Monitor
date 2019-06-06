package com.jetec.Monitor.SupportFunction;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;
import java.util.Calendar;

public class SetDateTime {

    private String TAG = "SetDateTime";
    private Calendar c;
    private String getdate, gettime;

    public SetDateTime(){
        super();
    }

    @SuppressLint("SetTextI18n")
    public void datechose(Context context, TextView textView){
        c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        getdate = " " + mYear + "-" + (mMonth + 1) + "-" + mDay + " ";
        gettime = "00:00";
        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            // set day of month , month and year value in the edit text
            getdate = Fix(year) + "-" + Fix((monthOfYear + 1)) + "-" + Fix(dayOfMonth);
            textView.setText(getdate + "  " + gettime);
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void timechose(Context context, TextView textView){
        c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        // date picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            gettime = Fix(hourOfDay) + ":" + Fix(minute);
            textView.setText(getdate + "  " + gettime);
        }, hour, minutes, false);
        timePickerDialog.show();
    }

    private String Fix(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
