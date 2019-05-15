package com.jetec.Monitor.SupportFunction;

public class CheckDeviceName {

    public CheckDeviceName(){
        super();
    }

    public String setName(String name){

        String rename = "";

        if(name.startsWith("EH1")){
            rename = "EH1";
        }
        else if(name.startsWith("EH2")){
            rename = "EH2";
        }
        else if(name.startsWith("EH3")){
            rename = "EH3";
        }
        else if(name.startsWith("EL1")){
            rename = "EL1";
        }
        else if(name.startsWith("EL2")){
            rename = "EL2";
        }
        else if(name.startsWith("EL3")){
            rename = "EL3";
        }
        else if(name.startsWith("ADR")){
            rename = "ADR";
        }
        else if(name.startsWith("RL1")){
            rename = "RL1";
        }
        else if(name.startsWith("RL2")){
            rename = "RL2";
        }
        else if(name.startsWith("RL3")){
            rename = "RL3";
        }
        else if(name.startsWith("PR1")){
            rename = "PR1";
        }
        else if(name.startsWith("PR2")){
            rename = "PR2";
        }
        else if(name.startsWith("PR3")){
            rename = "PR3";
        }
        /*else if(name.startsWith("INTER")){
            rename = "INTER";
        }*/
        else if(name.startsWith("OVER")){
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
        else if(name.startsWith("SPK")){
            rename = "SPK";
        }

        return rename;
    }
}
