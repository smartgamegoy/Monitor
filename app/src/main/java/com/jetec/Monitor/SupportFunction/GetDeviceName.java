package com.jetec.Monitor.SupportFunction;

import android.content.Context;

import com.jetec.Monitor.R;

import java.util.ArrayList;

public class GetDeviceName {

    private Context context;
    private String TAG = "GetDeviceName";
    private LogMessage logMessage = new LogMessage();
    private ArrayList<Character> name;

    public GetDeviceName(Context context, String devicename){
        this.context = context;

        name = new ArrayList<>();
        for (int i = 0; i < devicename.length(); i++) {
            name.add(devicename.charAt(i));
        }
        logMessage.showmessage(TAG,"name = " + name);
    }

    public String get(String str){

        String rename = "";

        if(str.startsWith("PV")){
            int get = Integer.valueOf(str.substring(2, 3));
            if(name.get((get - 1)).toString().matches("T")){
                rename = this.context.getString(R.string.T) + " " + this.context.getString(R.string.PV1);
            }else if(name.get((get - 1)).toString().matches("H")){
                rename = this.context.getString(R.string.H) + " " + this.context.getString(R.string.PV1);
            }else if(name.get((get - 1)).toString().matches("C") ||
                    name.get((get - 1)).toString().matches("D") ||
                    name.get((get - 1)).toString().matches("E")){
                rename = this.context.getString(R.string.C) + " " + this.context.getString(R.string.PV1);
            }else if(name.get((get - 1)).toString().matches("I")){
                rename = this.context.getString(R.string.I1) + " " + this.context.getString(R.string.PV1);
            }else if(name.get((get - 1)).toString().matches("P")){
                rename = this.context.getString(R.string.P) + " " + this.context.getString(R.string.PV1);
            }else if(name.get((get - 1)).toString().matches("M")){
                rename = this.context.getString(R.string.M) + " " + this.context.getString(R.string.PV1);
            }else if(name.get((get - 1)).toString().matches("Z")){
                rename = this.context.getString(R.string.percent) + " " + this.context.getString(R.string.PV1);
            }
        }else if(str.startsWith("EH")){
            int get = Integer.valueOf(str.substring(2, 3));
            if(name.get((get - 1)).toString().matches("T")){
                rename = this.context.getString(R.string.T) + " " + this.context.getString(R.string.EH1);
            }else if(name.get((get - 1)).toString().matches("H")){
                rename = this.context.getString(R.string.H) + " " + this.context.getString(R.string.EH1);
            }else if(name.get((get - 1)).toString().matches("C") ||
                    name.get((get - 1)).toString().matches("D") ||
                    name.get((get - 1)).toString().matches("E")){
                rename = this.context.getString(R.string.C) + " " + this.context.getString(R.string.EH1);
            }else if(name.get((get - 1)).toString().matches("I")){
                rename = this.context.getString(R.string.I1) + " " + this.context.getString(R.string.EH1);
            }else if(name.get((get - 1)).toString().matches("P")){
                rename = this.context.getString(R.string.P) + " " + this.context.getString(R.string.EH1);
            }else if(name.get((get - 1)).toString().matches("M")){
                rename = this.context.getString(R.string.M) + " " + this.context.getString(R.string.EH1);
            }else if(name.get((get - 1)).toString().matches("Z")){
                rename = this.context.getString(R.string.percent) + " " + this.context.getString(R.string.EH1);
            }
        }else if(str.startsWith("EL")){
            int get = Integer.valueOf(str.substring(2, 3));
            if(name.get((get - 1)).toString().matches("T")){
                rename = this.context.getString(R.string.T) + " " + this.context.getString(R.string.EL1);
            }else if(name.get((get - 1)).toString().matches("H")){
                rename = this.context.getString(R.string.H) + " " + this.context.getString(R.string.EL1);
            }else if(name.get((get - 1)).toString().matches("C") ||
                    name.get((get - 1)).toString().matches("D") ||
                    name.get((get - 1)).toString().matches("E")){
                rename = this.context.getString(R.string.C) + " " + this.context.getString(R.string.EL1);
            }else if(name.get((get - 1)).toString().matches("I")){
                rename = this.context.getString(R.string.I1) + " " + this.context.getString(R.string.EL1);
            }else if(name.get((get - 1)).toString().matches("P")){
                rename = this.context.getString(R.string.P) + " " + this.context.getString(R.string.EL1);
            }else if(name.get((get - 1)).toString().matches("M")){
                rename = this.context.getString(R.string.M) + " " + this.context.getString(R.string.EL1);
            }else if(name.get((get - 1)).toString().matches("Z")){
                rename = this.context.getString(R.string.percent) + " " + this.context.getString(R.string.EL1);
            }
        }else if(str.startsWith("ADR")){
            rename = this.context.getString(R.string.ADR);
        }else if(str.startsWith("DP")){
            int get = Integer.valueOf(str.substring(2, 3));
            if(name.get((get - 1)).toString().matches("T")){
                rename = this.context.getString(R.string.T) + " " + this.context.getString(R.string.DP1);
            }
            else if(name.get((get - 1)).toString().matches("H")){
                rename = this.context.getString(R.string.H) + " " + this.context.getString(R.string.DP1);
            }
            else if(name.get((get - 1)).toString().matches("C") ||
                    name.get((get - 1)).toString().matches("D") ||
                    name.get((get - 1)).toString().matches("E")){
                rename = this.context.getString(R.string.C) + " " + this.context.getString(R.string.DP1);
            }
            else if(name.get((get - 1)).toString().matches("I")){
                rename = this.context.getString(R.string.I1) + " " + this.context.getString(R.string.DP1);
            }
            else if(name.get((get - 1)).toString().matches("P")){
                rename = this.context.getString(R.string.P) + " " + this.context.getString(R.string.DP1);
            }
            else if(name.get((get - 1)).toString().matches("M")){
                rename = this.context.getString(R.string.M) + " " + this.context.getString(R.string.DP1);
            }
        }else if(str.startsWith("INTER")){
            rename = this.context.getString(R.string.INTER);
        }else if(str.startsWith("SPK")) {
            rename = this.context.getString(R.string.SPK);
        }else if(str.startsWith("RL")){
            char s = str.charAt(2);
            rename = this.context.getString(R.string.RL) + s;
        }else if(str.startsWith("PR")){
            rename = this.context.getString(R.string.percentadjustment);
        }
        /*if(str.startsWith("LOG")){
            rename = this.context.getString(R.string.LOG);
        }
        if(str.startsWith("IH1")){
            rename = this.context.getString(R.string.IH1);
        }
        if(str.startsWith("IH2")){
            rename = this.context.getString(R.string.IH2);
        }
        if(str.startsWith("IH3")){
            rename = this.context.getString(R.string.IH3);
        }
        if(str.startsWith("IL1")){
            rename = this.context.getString(R.string.IL1);
        }
        if(str.startsWith("IL2")){
            rename = this.context.getString(R.string.IL2);
        }
        if(str.startsWith("IL3")){
            rename = this.context.getString(R.string.IL3);
        }*/
        return rename;
    }
}
