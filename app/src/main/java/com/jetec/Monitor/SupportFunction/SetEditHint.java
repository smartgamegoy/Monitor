package com.jetec.Monitor.SupportFunction;

import android.content.Context;
import android.widget.EditText;
import com.jetec.Monitor.EditManagert.SearchEditListener;

public class SetEditHint {

    private String TAG = "SetEditHint";
    private SearchEditListener searchEditListener = new SearchEditListener();
    private GetUnit getUnit = new GetUnit();

    public SetEditHint(){
        super();
    }

    public void seteditHint(Context context, EditText e1, int position){
        String getName = getUnit.getName(context, Value.getviewlist.get((position - 3) * 4));
        searchEditListener.setValue(e1, getName);
        if (getName.matches("0")) { //1~8均為壓力，單位不同，尚無給定範圍，均暫時給定100
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("1")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("2")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("3")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("4")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("5")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("6")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("7")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("8")) {
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("9")) {  //攝氏
            e1.setHint(" - 10 ~ 65");
        } else if (getName.matches("10")) { //華氏
            e1.setHint(" 14 ~ 149");
        } else if (getName.matches("11")) { //濕度
            e1.setHint(" 0 ~ 100");
        } else if (getName.matches("12")) { //一氧化碳，因不清楚最大範圍多少，先行給定1000
            e1.setHint(" 0 ~ 1000");
        } else if (getName.matches("13")) { //二氧化碳，因不清楚最大範圍多少，先行給定2000
            e1.setHint(" 0 ~ 2000");
        } else if (getName.matches("14")) { //PM2.5
            e1.setHint(" 0 ~ 1000");
        } else if (getName.matches("15")) { //比例
            e1.setHint(" 0 ~ 100");
        }
        e1.addTextChangedListener(searchEditListener);
    }
}
