package com.jetec.Monitor.SupportFunction;

public class CheckDeviceName {

    public CheckDeviceName() {
        super();
    }

    public String setName(String name) {

        String rename = "";

        if (name.startsWith("PV")) {
            rename = name.substring(0, 3);
        } else if (name.startsWith("EH")) {
            rename = name.substring(0, 3);
        } else if (name.startsWith("EL")) {
            rename = name.substring(0, 3);
        } else if (name.startsWith("ADR")) {
            rename = "ADR";
        } else if (name.startsWith("RL")) {
            rename = name.substring(0, 3);
        } else if (name.startsWith("PR")) {
            rename = name.substring(0, 3);
        } else if (name.startsWith("INTER")) {
            rename = "INTER";
        } else if (name.startsWith("OVER")) {
            rename = "OVER";
        }
        /*else if(name.startsWith("IH1")){
            rename = "IH1";
        }
        else if(name.startsWith("IH2")){
            rename = "IH2";
        }
        else if(name.startsWith("IH3")){
            rename = "IH3";
        }
        else if(name.startsWith("IL1")){
            rename = "IL1";
        }
        else if(name.startsWith("IL2")){
            rename = "IL2";
        }
        else if(name.startsWith("IL3")){
            rename = "IL3";
        }*/
        else if (name.startsWith("SPK")) {
            rename = "SPK";
        }

        return rename;
    }
}
