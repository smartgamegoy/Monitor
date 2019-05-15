package com.jetec.Monitor.SupportFunction;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCheck {

    public TimeCheck(){
        super();
    }

    public boolean checktime(String recordTime, String nowTime){
        boolean check = false;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date time1 = dateFormat.parse(recordTime);
            Date time2 = dateFormat.parse(nowTime);

            time1.setTime(time1.getTime() + 60000);  //60000

            if (time2.getTime() == time1.getTime()){
                check = true;
            }else if (time2.getTime() > time1.getTime()){
                check = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  check;
    }
}
