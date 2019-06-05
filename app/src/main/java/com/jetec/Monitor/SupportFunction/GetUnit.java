package com.jetec.Monitor.SupportFunction;

import android.content.Context;

import com.jetec.Monitor.R;

public class GetUnit {

    private String TAG = "GetUnit";
    private LogMessage logMessage = new LogMessage();

    public GetUnit() {
        super();
    }

    public String getUnit(String model) {   //model為型號判定 ex:THC之中的T
        String unit = "N/A";

        if (model.matches("0")) {
            unit = "Pressure" + "/" + "Mpa";
        } else if (model.matches("1")) {
            unit = "Pressure" + "/" + "Kpa";
        } else if (model.matches("2")) {
            unit = "Pressure" + "/" + "Pa";
        } else if (model.matches("3")) {
            unit = "Pressure" + "/" + "Bar";
        } else if (model.matches("4")) {
            unit = "Pressure" + "/" + "Mbar";
        } else if (model.matches("5")) {
            unit = "Pressure" + "/" + "Kg/cm" + (char) (178);
        } else if (model.matches("6")) {
            unit = "Pressure" + "/" + "psi";
        } else if (model.matches("7")) {
            unit = "Pressure" + "/" + "mh" + (char) (178) + "O";
        } else if (model.matches("8")) {
            unit = "Pressure" + "/" + "mmh" + (char) (178) + "O";
        } else if (model.matches("9")) {
            unit = "Temperature" + "/" + "˚C";
        } else if (model.matches("10")) {
            unit = "Temperature" + "/" + "˚F";
        } else if (model.matches("11")) {
            unit = "Humidity" + "/" + "%";
        } else if (model.matches("12")) {
            unit = "CO" + "/" + "ppm";
        } else if (model.matches("13")) {
            unit = "CO2" + "/" + "ppm";
        } else if (model.matches("14")) {
            unit = "PM2.5" + "/" + (char) (956) + "g/m" + (char) (179);
        } else if (model.matches("15")) {
            unit = "Proportion" + "/" + "%";
        }
        return unit;
    }

    public String getName(Context context, String model) {
        String unit = "N/A";

        if (model.matches("0")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("1")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("2")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("3")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("4")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("5")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("6")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("7")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("8")) {
            unit = context.getString(R.string.pressure);
        } else if (model.matches("9")) {
            unit = context.getString(R.string.temperature);
        } else if (model.matches("10")) {
            unit = context.getString(R.string.temperature);
        } else if (model.matches("11")) {
            unit = context.getString(R.string.humidity);
        } else if (model.matches("12")) {
            unit = context.getString(R.string.co);
        } else if (model.matches("13")) {
            unit = context.getString(R.string.co2);
        } else if (model.matches("14")) {
            unit = context.getString(R.string.pm);
        } else if (model.matches("15")) {
            unit = context.getString(R.string.percent);
        }

        return unit;
    }
}
