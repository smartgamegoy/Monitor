package com.jetec.Monitor.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.jetec.Monitor.MPChart.ChartList;
import com.jetec.Monitor.MPChart.CreatPDF.CreatTable;
import com.jetec.Monitor.MPChart.CreatPDF.FooterHandler;
import com.jetec.Monitor.MPChart.CreatPDF.HeaderHandler;
import com.jetec.Monitor.MPChart.CustomMarkerView;
import com.jetec.Monitor.R;
import com.jetec.Monitor.Service.BluetoothLeService;
import com.jetec.Monitor.SupportFunction.GetUnit;
import com.jetec.Monitor.SupportFunction.LogMessage;
import com.jetec.Monitor.SupportFunction.Runningpdf;
import com.jetec.Monitor.SupportFunction.SQL.SaveLogSQL;
import com.jetec.Monitor.SupportFunction.Screen;
import com.jetec.Monitor.SupportFunction.Value;
import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LogChartActivity extends AppCompatActivity {

    private String TAG = "LogChartActivity";
    private LogMessage logMessage = new LogMessage();
    private Vibrator vibrator;
    private BluetoothLeService mBluetoothLeService;
    private boolean s_connect = false;
    private Intent intents;
    private String BID, modelName;
    private ArrayList<String> selectItem;
    private ArrayList<String> reList;
    private ArrayList<String> dataList;
    private ArrayList<String> SQLList;
    private ArrayList<String> timeList;
    private ArrayList<String> viewtimeList;
    private ArrayList<List<String>> valueList;
    private String saveDate, saveTime, saveInter;
    private JSONArray savejson;
    private File file, pdffile;
    private Uri csvuri;
    private SaveLogSQL saveLogSQL = new SaveLogSQL(this);
    private Screen screen = new Screen(this);
    private GetUnit getUnit = new GetUnit();
    private LineChart lineChart;
    private DisplayMetrics dm;
    private YAxis leftAxis;
    private LimitLine yLimitLinedown, yLimitLineup;
    private int dialogflag = 0, select_item = -1, flag = 0;
    private ArrayList<ILineDataSet> dataSets;
    private Dialog chartdialog = null;
    private View view1;
    private boolean setdpp = false;
    private Runningpdf runningpdf = new Runningpdf(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        if (mBluetoothLeService == null) {
            Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
            s_connect = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            if (s_connect)
                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            else
                Log.e(TAG, "連線失敗");
        }
        ConfigurationChange();
    }

    private void ConfigurationChange() {
        Intent intent = getIntent();

        selectItem = new ArrayList<>();
        reList = new ArrayList<>();
        dataList = new ArrayList<>();
        SQLList = new ArrayList<>();
        timeList = new ArrayList<>();
        valueList = new ArrayList<>();
        viewtimeList = new ArrayList<>();
        dataSets = new ArrayList<>();
        selectItem.clear();
        reList.clear();
        dataList.clear();
        SQLList.clear();
        timeList.clear();
        valueList.clear();
        viewtimeList.clear();
        dataSets.clear();

        BID = intent.getStringExtra("BID");
        selectItem = intent.getStringArrayListExtra("selectItem");
        reList = intent.getStringArrayListExtra("reList");
        dataList = intent.getStringArrayListExtra("dataList");
        //BID、selectItem、reList、dataList僅暫存，回歸devicefunction生命週期時使用，須將各設定值回傳
        logMessage.showmessage(TAG, "selectItem = " + selectItem);
        logMessage.showmessage(TAG, "reList = " + reList);
        logMessage.showmessage(TAG, "dataList = " + dataList);

        dm = screen.size();
        modelName = Value.model_name;
        makeList();
    }

    private void makeList() {
        try {
            SQLList = saveLogSQL.getsaveLog(Value.device);    //time, date, list
            saveTime = SQLList.get(0);
            saveDate = SQLList.get(1);
            saveInter = SQLList.get(2);
            String savelist = SQLList.get(3);   //savelist內存有幾排之數值ex:TH，即有2排數值
            savejson = new JSONArray(savelist);
            for (int i = 0; i < savejson.length(); i++) {
                List<String> newList = new ArrayList<>();
                newList.clear();
                String newvalue = savejson.get(i).toString();
                JSONArray jsonArray = new JSONArray(newvalue);
                for (int j = 0; j < jsonArray.length(); j++) {
                    newList.add(jsonArray.get(j).toString());
                }
                valueList.add(newList);
            }
            logMessage.showmessage(TAG, "valueList = " + valueList);
            logMessage.showmessage(TAG, "valueList.size = " + valueList.size());
            maketimeList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(packagecsv).start();
        new Thread(makepdf).start();

        showChart();
    }

    private void showChart() {
        setContentView(R.layout.showlogview);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);
        Button b4 = findViewById(R.id.button4);

        initView();
        initData();

        b1.setOnClickListener(v -> {
            vibrator.vibrate(100);
            chartdialog = Dialogview(this);
            chartdialog.show();
            chartdialog.setCanceledOnTouchOutside(false);
        });

        b3.setOnClickListener(v -> {
            vibrator.vibrate(100);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.report)
                    .setMessage(R.string.choose)
                    .setPositiveButton(R.string.CSV, (dialog, which) -> {
                        vibrator.vibrate(100);
                        csvuri = Uri.fromFile(file);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, csvuri);
                        shareIntent.setType("text/*");
                        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                    })
                    .setNegativeButton(R.string.PDF, (dialog, which) -> {
                        vibrator.vibrate(100);
                        if (!setdpp) {
                            runningpdf.startFlash(getString(R.string.exportingpdf));
                        } else {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdffile));
                            shareIntent.setType("text/*");
                            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                        }
                    })
                    .setNeutralButton(R.string.mes_no, (dialog, which) -> vibrator.vibrate(100))
                    .show();
        });

        b4.setOnClickListener(v -> {
            vibrator.vibrate(100);
            flag = 2;
            if (!setdpp) {
                runningpdf.startFlash(getString(R.string.exportingpdf));
            } else {
                pdf_intent();
            }
        });
    }

    private void initView() {
        lineChart = findViewById(R.id.chart1);
    }

    private void initData() {
        lineChart.clear();
        //noinspection IntegerDivisionInFloatingPointContext
        lineChart.setExtraOffsets((2 * dm.widthPixels / 100), (3 * dm.heightPixels / 100),
                (4 * dm.widthPixels / 100), (dm.heightPixels / 100));
        setDescription(Value.device_name);
        lineChart.animateXY(800, 800);   //繪製延遲動畫
        setLegend();
        setYAxis();
        setXAxis();
        setChartData();
    }

    private Dialog Dialogview(Context context) {
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.chosedialog, null);
        LinearLayout chart = v.findViewById(R.id.chart);
        ListView chart_list = v.findViewById(R.id.datalist1);
        Button b1 = v.findViewById(R.id.button1);
        Button b2 = v.findViewById(R.id.button2);

        ArrayList<String> chartview = new ArrayList<>();
        chartview.clear();
        chartview.add(getString(R.string.Combine));

        for (int i = 0; i < valueList.size(); i++) {
            chartview.add(getUnit.getName(context, Value.getviewlist.get(i * 4)));
        }

        ChartList chartList = new ChartList(this, dm.widthPixels, dm.heightPixels, chartview);
        chart_list.setAdapter(chartList);
        chart_list.setOnItemClickListener(mchosechart);

        b1.setOnClickListener(v1 -> {
            vibrator.vibrate(100);
            select_item = -1;
            chartdialog.dismiss();
        });

        b2.setOnClickListener(v12 -> {
            vibrator.vibrate(100);
            select_item = -1;
            Log.e(TAG, "開始");
            //initView();
            dataSets.clear();
            initData();
            chartdialog.dismiss();
        });

        progressDialog.setContentView(chart, new LinearLayout.LayoutParams((3 * dm.widthPixels / 5),
                (2 * dm.heightPixels / 5)));

        return progressDialog;
    }

    private AdapterView.OnItemClickListener mchosechart = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            vibrator.vibrate(100);
            //一開始未選擇任何一個item所以為-1
            //======================
            //點選某個item並呈現被選取的狀態
            if ((select_item == -1) || (select_item == position)) {
                view.setBackgroundColor(Color.YELLOW); //為View加上選取效果
            } else {
                //noinspection deprecation
                view1.setBackgroundDrawable(null); //將上一次點選的View保存在view1
                view.setBackgroundColor(Color.YELLOW); //為View加上選取效果
            }
            view1 = view; //保存點選的View
            select_item = position;//保存目前的View位置
            //======================
            dialogflag = select_item;
        }
    };

    private void viewSet(String model, List<String> getList) {
        if (model.matches("0")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("1")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("2")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("3")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("4")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("5")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("6")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("7")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("8")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pressure), Color.DKGRAY));
        } else if (model.matches("9")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.temperature), Color.GREEN));
        } else if (model.matches("10")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.temperature), Color.GREEN));
        } else if (model.matches("11")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.humidity), Color.BLUE));
        } else if (model.matches("12")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.co), Color.RED));
        } else if (model.matches("13")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.co2), Color.MAGENTA));
        } else if (model.matches("14")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.pm), Color.YELLOW));
        } else if (model.matches("15")) {
            dataSets.add(lineDataSet(ChartData(getList), getString(R.string.percent), Color.CYAN));
        }
    }

    private List<Entry> ChartData(List<String> list) {
        List<Entry> data = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            float getdata = (Float.valueOf(list.get(i)));
            data.add(new Entry((i + 1), getdata));
        }
        return data;
    }

    private LineDataSet lineDataSet(List<Entry> ChartData, String getname, int color) {
        LineDataSet lineDataSet = new LineDataSet(ChartData, getname);
        lineDataSet.setDrawCircleHole(true);   //空心圓點
        lineDataSet.setColor(color); //線的顏色green
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setCubicIntensity(1);  //强度
        lineDataSet.setCircleColor(color);    //圓點顏色
        lineDataSet.setLineWidth(1);

        lineDataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> String.valueOf(value));
        return lineDataSet;
    }

    private void setChartData() {
        CustomMarkerView mv = new CustomMarkerView(this, R.layout.custom_marker_view_layout, dm.widthPixels, dm.heightPixels);
        //noinspection deprecation
        lineChart.setMarkerView(mv);

        if (dialogflag == 0) {
            for (int i = 0; i < valueList.size(); i++) {
                viewSet(Value.getviewlist.get(i * 4), valueList.get(i));
            }
        } else {
            int i = dialogflag - 1;
            viewSet(Value.getviewlist.get(i * 4), valueList.get(i));
        }

        LineData lineData = new LineData(dataSets);
        lineData.setDrawValues(false);

        lineChart.setVisibleXRangeMaximum(viewtimeList.size());
        lineChart.setScaleXEnabled(true);
        lineChart.setData(lineData);
    }

    private void setXAxis() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);  //格線
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(14);
        xAxis.setGranularity(1);    //間隔
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(viewtimeList.size());
        xAxis.setValueFormatter((value, axis) -> {
            if (value == 0) {
                return "";
            } else {
                return viewtimeList.get(((int) value) - 1);
            }
        });
    }

    private void setYAxis() {
        final YAxis yAxisLeft = lineChart.getAxisLeft();
        if (dialogflag == 0) {
            if (leftAxis != null) {
                leftAxis.removeLimitLine(yLimitLinedown);
                leftAxis.removeLimitLine(yLimitLineup);
            }
            List<String> Mm = new ArrayList<>();
            Mm.clear();
            for (int i = 0; i < valueList.size(); i++) {
                Mm.addAll(valueList.get(i));
            }
            float maxIndex = Float.valueOf(Mm.get(0));
            float minIndex = Float.valueOf(Mm.get(0));
            for (int i = 0; i < Mm.size(); i++) {
                if (maxIndex < Float.valueOf(Mm.get(i))) {
                    maxIndex = Float.valueOf(Mm.get(i));
                }
                if (minIndex > Float.valueOf(Mm.get(i))) {
                    minIndex = Float.valueOf(Mm.get(i));
                }
            }
            maxIndex = maxIndex + 10;
            minIndex = minIndex - 10;

            Log.e(TAG, "maxIndex = " + maxIndex);
            Log.e(TAG, "minIndex = " + minIndex);

            yAxisLeft.setAxisMaximum(maxIndex);
            yAxisLeft.setAxisMinimum(minIndex);
            yAxisLeft.setGranularity(1);
            yAxisLeft.setTextSize(14);
            yAxisLeft.setTextColor(Color.BLACK);
            yAxisLeft.setValueFormatter((value, axis) -> String.valueOf((int) value));
        } else {
            if (leftAxis != null) {
                Log.e(TAG, "開始清屏");
                leftAxis.removeLimitLine(yLimitLinedown);
                leftAxis.removeLimitLine(yLimitLineup);
                Log.e(TAG, "清屏null");
            }
            int i = dialogflag - 1;
            setLimitline(Value.getviewlist.get(i * 4), dialogflag);
            yLimitLinedown.enableDashedLine((float) dm.widthPixels / 100, (float) dm.widthPixels / 100, 1);
            yLimitLinedown.setTextSize(14);
            yLimitLinedown.setLineColor(Color.RED);
            yLimitLinedown.setTextColor(Color.RED);
            leftAxis = lineChart.getAxisLeft();
            leftAxis.addLimitLine(yLimitLinedown);

            yLimitLineup.enableDashedLine((float) dm.widthPixels / 100, (float) dm.widthPixels / 100, 1);
            yLimitLineup.setTextSize(14);
            yLimitLineup.setLineColor(Color.CYAN);
            yLimitLineup.setTextColor(Color.CYAN);
            YAxis leftAxis2 = lineChart.getAxisLeft();
            leftAxis2.addLimitLine(yLimitLineup);

            ArrayList<String> Mm = new ArrayList<>();
            Mm.clear();
            Mm.addAll(valueList.get(i));

            float maxIndex = Float.valueOf(Mm.get(0));
            float minIndex = Float.valueOf(Mm.get(0));
            for (int j = 0; j < Mm.size(); j++) {
                if (maxIndex < Float.valueOf(Mm.get(j))) {
                    maxIndex = Float.valueOf(Mm.get(j));
                }
                if (minIndex > Float.valueOf(Mm.get(j))) {
                    minIndex = Float.valueOf(Mm.get(j));
                }
            }

            maxIndex = maxIndex + 10;
            minIndex = minIndex - 10;

            Log.e(TAG, "maxIndex = " + maxIndex);
            Log.e(TAG, "minIndex = " + minIndex);

            yAxisLeft.setAxisMaximum(maxIndex);
            yAxisLeft.setAxisMinimum(minIndex);
            yAxisLeft.setGranularity(1);
            yAxisLeft.setTextSize(14);
            yAxisLeft.setTextColor(Color.BLACK);
            yAxisLeft.setValueFormatter((value, axis) -> String.valueOf((int) value));
        }
    }

    private void setLimitline(String model, int i) {
        String EH = "EH" + String.valueOf(i);
        String EL = "EL" + String.valueOf(i);
        if (model.matches("0")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("1")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("2")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("3")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("4")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("5")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("6")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("7")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("8")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pressure) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pressure) + getString(R.string.LL));  //下限線
        } else if (model.matches("9")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.temperature) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.temperature) + getString(R.string.LL));  //下限線
        } else if (model.matches("10")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.temperature) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.temperature) + getString(R.string.LL));  //下限線
        } else if (model.matches("11")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.humidity) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.humidity) + getString(R.string.LL));  //下限線
        } else if (model.matches("12")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.co) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.co) + getString(R.string.LL));  //下限線
        } else if (model.matches("13")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.co2) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.co2) + getString(R.string.LL));  //下限線
        } else if (model.matches("14")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.pm) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.pm) + getString(R.string.LL));  //下限線
        } else if (model.matches("15")) {
            yLimitLinedown = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EH) - 1)),
                    getString(R.string.percent) + getString(R.string.UL));  //上限線
            yLimitLineup = new LimitLine(Float.valueOf(dataList.get(selectItem.indexOf(EL) - 1)),
                    getString(R.string.percent) + getString(R.string.LL));  //下限線
        }
    }

    private void setLegend() {
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(14);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setTextColor(Color.BLACK);
    }

    private void setDescription(String descriptionStr) {
        Description description = new Description();
        description.setText(descriptionStr);
        Paint paint = new Paint();
        paint.setTextSize(20);
        float x = (float) (dm.widthPixels - dm.widthPixels / 100);
        float y = (float) (2 * dm.heightPixels / 100);
        description.setPosition(x, y);
        lineChart.setDescription(description);
    }

    private Runnable packagecsv = new Runnable() {
        @Override
        public void run() {
            try {
                ArrayList<String> data = new ArrayList<>();
                data.clear();
                data.add("id");
                data.add("dateTime");
                for (int i = 0; i < savejson.length(); i++) { //加入各排單位
                    String unit = getUnit.getUnit(Value.getviewlist.get(i * 4));
                    data.add(unit);
                }
                String[] data_array = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    data_array[i] = data.get(i);
                }
                String[] data2;
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                // SD卡位置getApplicationContext().getFilesDir().getAbsolutePath();
                // 系統位置android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "JetecLogData" + ".csv";
                String filePath = baseDir + File.separator + fileName;
                Log.e(TAG, "filePath = " + filePath);
                file = new File(filePath);
                CSVWriter writer;
                if (file.exists() && !file.isDirectory()) {
                    FileWriter mFileWriter;
                    mFileWriter = new FileWriter(filePath, false);
                    writer = new CSVWriter(mFileWriter);
                    writer.writeNext(data_array);
                    for (int i = 0; i < timeList.size(); i++) {
                        data2 = new String[savejson.length() + 2];  //ID+時間+排數
                        data2[0] = String.valueOf(i + 1);
                        data2[1] = timeList.get(i);
                        for (int j = 0; j < savejson.length(); j++) {
                            data2[j + 2] = valueList.get(j).get(i);
                        }
                        writer.writeNext(data2);
                    }
                    writer.close();
                } else {
                    FileWriter mFileWriter;
                    mFileWriter = new FileWriter(filePath, false);
                    writer = new CSVWriter(mFileWriter);
                    writer.writeNext(data_array);
                    for (int i = 0; i < timeList.size(); i++) {
                        data2 = new String[savejson.length() + 2];  //ID+時間+排數
                        data2[0] = String.valueOf(i + 1);
                        data2[1] = timeList.get(i);
                        for (int j = 0; j < savejson.length(); j++) {
                            data2[j + 2] = valueList.get(j).get(i);
                        }
                        writer.writeNext(data2);
                    }
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable makepdf = new Runnable() {
        @Override
        public void run() {
            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            // SD卡位置getApplicationContext().getFilesDir().getAbsolutePath();
            // 系統位置android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "JetecLogData" + ".pdf";
            String filePath = baseDir + File.separator + fileName;
            pdffile = new File(filePath);
            try {
                JSONArray jsonArray = new JSONArray(savejson.get(0).toString());
                int alldata = jsonArray.length(), page;

                if (alldata % 300 == 0) {
                    page = alldata / 300;
                } else {
                    page = (alldata / 300) + 1;
                }

                HeaderHandler head = new HeaderHandler();
                FooterHandler foot = new FooterHandler();
                CreatTable table = new CreatTable();
                Document document = new Document(PageSize.A4, -20, -20, 40, 40);
                FileOutputStream fOut = new FileOutputStream(pdffile);
                PdfWriter writer = PdfWriter.getInstance(document, fOut);
                BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UTF16-H", true);
                Font chineseFont = new Font(bfChinese, 12, Font.NORMAL);
                String gettitle = Value.device_name;
                Paragraph title = new Paragraph(gettitle, chineseFont);
                head.setHeader(title);
                foot.setallpage(page);
                writer.setPageEvent(head);
                writer.setPageEvent(foot);
                document.open();
                table.setList(LogChartActivity.this, valueList, timeList, page);
                document.add(table.createTable());
                logMessage.showmessage(TAG, "valueList = " + valueList);
                logMessage.showmessage(TAG, "timeList = " + timeList);
                document.close();
                Log.e(TAG, "已完成");
                setdpp = true;
                if (runningpdf.isCheck()) {
                    runningpdf.closeFlash();
                    if (flag == 1) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdffile));
                        shareIntent.setType("text/*");
                        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                    } else if (flag == 2) {
                        pdf_intent();
                    }
                }
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void maketimeList() {
        try {
            String getvaluelist = savejson.get(0).toString();   //由於存取之數據長度一致，故取其一陣列
            JSONArray jsonArray = new JSONArray(getvaluelist);
            String formattime;
            int inter = Integer.valueOf(saveInter.substring(5));
            String Date = saveDate.substring(4);
            String Time = saveTime.substring(4);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat log_date = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            Date = Date.substring(0, 2) + "-" +
                    Date.substring(2, 4) + "-" + Date.substring(4, 6);
            Time = Time.substring(0, 2) + ":" +
                    Time.substring(2, 4) + ":" + Time.substring(4, 6);
            String all_date = Date + " " + Time;
            Date date = log_date.parse(all_date);
            for (int i = 0; i < jsonArray.length(); i++) {
                formattime = log_date.format(date);
                timeList.add(formattime);
                formattime = formattime.substring(3, formattime.indexOf(" ")) + " " +
                        formattime.substring(formattime.indexOf(" ") + 1, formattime.length() - 3);
                viewtimeList.add(formattime);
                date.setTime(date.getTime() + (inter * 1000));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pdf_intent(){
        Intent intent = new Intent(LogChartActivity.this, PDFLogView.class);

        intent.putExtra("BID", BID);
        intent.putStringArrayListExtra("selectItem", selectItem);
        intent.putStringArrayListExtra("reList", reList);
        intent.putStringArrayListExtra("dataList", dataList);

        startActivity(intent);
        finish();
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            intents = intent;
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                logMessage.showmessage(TAG, "連線成功");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                logMessage.showmessage(TAG, "連線中斷" + Value.connected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                logMessage.showmessage(TAG, "連線狀態改變");
                mBluetoothLeService.enableTXNotification();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(() -> {
                    byte[] txValue = intents.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    String text = new String(txValue, StandardCharsets.UTF_8);
                    logMessage.showmessage(TAG, "text = " + text);
                });
            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //https://github.com/googlesamples/android-BluetoothLeGatt/tree/master/Application/src/main/java/com/example/android/bluetoothlegatt
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                logMessage.showmessage(TAG, "初始化失敗");
            }
            mBluetoothLeService.connect(BID);
            logMessage.showmessage(TAG, "進入連線");
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            logMessage.showmessage(TAG, "失去連線端");
        }
    };

    private void disconnect() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    public void Service_close() {
        if (mBluetoothLeService == null) {
            return;
        }
        mBluetoothLeService.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //toolbar menu item
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            vibrator.vibrate(100);
            //Service_close();
            disconnect();
            return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*logMessage.showmessage(TAG, "onDestroy()");
        if (mBluetoothLeService != null) {
            if (s_connect) {
                unbindService(mServiceConnection);
                s_connect = false;
            }
            mBluetoothLeService.stopSelf();
            mBluetoothLeService = null;
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMessage.showmessage(TAG, "onStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logMessage.showmessage(TAG, "onResume");
        /*logMessage.showmessage(TAG, "s_connect = " + s_connect);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(BID);
            Log.d(TAG, "Connect request result=" + result);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMessage.showmessage(TAG, "onPause");
        /*if (s_connect)
            unregisterReceiver(mGattUpdateReceiver);*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }
    }
}
