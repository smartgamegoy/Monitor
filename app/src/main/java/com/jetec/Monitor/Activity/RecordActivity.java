package com.jetec.Monitor.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.PDF.*;
import com.jetec.Monitor.SupportFunction.Runningcsv;
import com.jetec.Monitor.SupportFunction.SQL.AlertRecord;
import com.jetec.Monitor.SupportFunction.*;
import com.jetec.Monitor.SupportFunction.ViewAdapter.RecordView;
import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecordActivity extends AppCompatActivity {

    private String TAG = "RecordActivity";
    private Vibrator vibrator;
    private LogMessage logMessage = new LogMessage();
    private AlertRecord alertRecord;
    private boolean csvdo = false, pdfdo = false;
    private Runningcsv runningcsv;
    private Runningpdf runningpdf;
    private RecordView recordView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        alertRecord = new AlertRecord(this);
        runningcsv = new Runningcsv(this);
        runningpdf = new Runningpdf(this);
        recordView = new RecordView(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        recordlist();
    }

    private void recordlist() {
        setContentView(R.layout.alertrecord);

        Toolbar myToolbar = findViewById(R.id.title);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        new Thread(makecsv).start();
        new Thread(makepdf).start();

        ListView listView = findViewById(R.id.listview);
        Spinner spinner = findViewById(R.id.toolbar);

        ArrayList<String> recordAddress = new ArrayList<>();
        recordAddress.clear();
        recordAddress.add(getString(R.string.allrecord));
        recordAddress.add(getString(R.string.sortrecord));
        recordAddress.addAll(alertRecord.address());

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_style, recordAddress) {    //android.R.layout.simple_spinner_item
            @Override
            public boolean isEnabled(int position) {
                return true;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("myLog", "position = " + position);
                if (position == 0) {
                    recordView.setRecordList(alertRecord.allRecordList());
                    listView.setAdapter(recordView);
                } else if (position == 1) {
                    logMessage.showmessage(TAG, "recordAddress = " + recordAddress);
                    recordView.setRecordList(alertRecord.getsortRecordList(recordAddress));
                    listView.setAdapter(recordView);
                } else {
                    recordView.setRecordList(alertRecord.getRecordList(recordAddress.get(position)));
                    listView.setAdapter(recordView);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String getunit(String unit) {    //Item + Unit
        String str = "";

        if (unit.matches("0")) {
            str = getString(R.string.P) + "/" + "Mpa";
        } else if (unit.matches("1")) {
            str = getString(R.string.P) + "/" + "Kpa";
        } else if (unit.matches("2")) {
            str = getString(R.string.P) + "/" + "Pa";
        } else if (unit.matches("3")) {
            str = getString(R.string.P) + "/" + "Bar";
        } else if (unit.matches("4")) {
            str = getString(R.string.P) + "/" + "Mbar";
        } else if (unit.matches("5")) {
            str = getString(R.string.P) + "/" + "Kg/cm" + (char) (178);
        } else if (unit.matches("6")) {
            str = getString(R.string.P) + "/" + "psi";
        } else if (unit.matches("7")) {
            str = getString(R.string.P) + "/" + "mh" + (char) (178) + "O";
        } else if (unit.matches("8")) {
            str = getString(R.string.P) + "/" + "mmh" + (char) (178) + "O";
        } else if (unit.matches("9")) {
            str = getString(R.string.T) + "/" + "˚C";
        } else if (unit.matches("10")) {
            str = getString(R.string.T) + "/" + "˚F";
        } else if (unit.matches("11")) {
            str = getString(R.string.H) + "/" + "%";
        } else if (unit.matches("12")) {
            str = getString(R.string.CO) + "/" + "ppm";
        } else if (unit.matches("13")) {
            str = getString(R.string.C) + "/" + "ppm";
        } else if (unit.matches("14")) {
            str = getString(R.string.M) + "/" + (char) (956) + "g/m" + (char) (179);
        }else if (unit.matches("15")) {
            str = getString(R.string.percent) + "/" + "%";
        }

        return str;
    }

    private Runnable makepdf = new Runnable() {
        @Override
        public void run() {
            try {
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                // SD卡位置getApplicationContext().getFilesDir().getAbsolutePath();
                // 系統位置android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "JetecBLE" + ".pdf";
                String filePath = baseDir + File.separator + fileName;
                File pdffile = new File(filePath);

                ArrayList<String> addresslist = new ArrayList<>();
                addresslist.clear();
                addresslist.addAll(alertRecord.address());

                ArrayList<List<String>> recordList = new ArrayList<>();
                recordList.clear();
                recordList = alertRecord.exportList(addresslist);

                List<Integer> device_count = new ArrayList<>();
                device_count.clear();
                device_count = alertRecord.device_count(addresslist);

                List<String> pdftitle = new ArrayList<>();
                pdftitle.clear();
                pdftitle = alertRecord.pdftitle(addresslist);

                HeaderHandler headerHandler = new HeaderHandler();
                FooterHandler footerHandler = new FooterHandler();
                CreatTable creatTable = new CreatTable();

                Document document = new Document(PageSize.A4, -20, -20, 40, 40);
                FileOutputStream fOut = new FileOutputStream(pdffile);
                PdfWriter writer = PdfWriter.getInstance(document, fOut);

                headerHandler.setHeader(pdftitle, device_count, recordList);
                footerHandler.setallpage(pdftitle, device_count, recordList);
                writer.setPageEvent(headerHandler);
                writer.setPageEvent(footerHandler);

                creatTable.setList(RecordActivity.this, addresslist);
                //sleep(3000);
                List<Integer> getColumn = new ArrayList<>();
                getColumn.clear();
                getColumn = creatTable.getColumn();

                document.open();

                for(int i = 0; i < getColumn.size(); i++) {
                    logMessage.showmessage(TAG,"i = " + i);
                    document.add(creatTable.createTable(i));
                    //sleep(1000);
                }

                document.close();

                pdfdo = true;

                if (runningpdf.isCheck()) {
                    runningpdf.closeFlash();
                    pdftodo();
                }
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable makecsv = new Runnable() {
        @Override
        public void run() {
            try {
                ArrayList<String> addresslist = new ArrayList<>();
                addresslist.clear();
                addresslist.addAll(alertRecord.address());

                ArrayList<List<String>> recordList = new ArrayList<>();
                List<String> csvtitle = new ArrayList<>();

                recordList.clear();
                csvtitle.clear();

                recordList = alertRecord.exportList(addresslist);

                csvtitle.add("id");
                csvtitle.add("address");
                csvtitle.add("name");
                csvtitle.add("date");

                for (int i = 0; i < recordList.size(); i++) {
                    List<String> dataList = new ArrayList<>();
                    dataList.clear();
                    dataList = recordList.get(i);
                    for (int j = 0; j < dataList.size(); j++) {
                        if (j != dataList.size() - 1) {
                            logMessage.showmessage(TAG, "do nothing");
                        } else {    //if j = dataList.size() - 1 then will get jsonArray
                            JSONArray jsonArray = new JSONArray(dataList.get(j));
                            for (int k = 0; k < jsonArray.length(); k = k + 4) {
                                String unit = getunit(jsonArray.get(k).toString());
                                if (csvtitle.indexOf(unit) == -1) {
                                    csvtitle.add(unit);
                                }
                            }
                        }
                    }
                }

                String[] data_array = new String[csvtitle.size()];
                for (int n = 0; n < csvtitle.size(); n++) {
                    data_array[n] = csvtitle.get(n);
                }

                logMessage.showmessage(TAG, "data_array = " + csvtitle);

                String[] data2;
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

                logMessage.showmessage(TAG, "baseDir = " + baseDir);
                // SD卡位置getApplicationContext().getFilesDir().getAbsolutePath();
                // 系統位置android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

                String fileName = "JetecBLE" + ".csv";
                String filePath = baseDir + File.separator + fileName;

                logMessage.showmessage(TAG, "filePath = " + filePath);

                file = new File(filePath);
                CSVWriter writer;

                logMessage.showmessage(TAG, "file = " + file);

                String address = "";
                byte[] BOM_UTF8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

                if (file.exists() && !file.isDirectory()) {
                    FileOutputStream os = new FileOutputStream(filePath);
                    os.write(BOM_UTF8);
                    //FileWriter mFileWriter = new FileWriter(filePath, false);
                    writer = new CSVWriter(new OutputStreamWriter(os));
                    writer.writeNext(data_array);
                    for (int i = 0; i < recordList.size(); i++) {
                        data2 = new String[csvtitle.size()];
                        for (int k = 0; k < data2.length; k++) {
                            data2[k] = "";
                        }
                        List<String> dataList = new ArrayList<>();
                        dataList.clear();
                        dataList = recordList.get(i);
                        logMessage.showmessage(TAG, "dataList = " + dataList);
                        for (int j = 0; j < dataList.size(); j++) {
                            if (j == 0) {
                                data2[j] = String.valueOf(i);
                            }
                            if (j != dataList.size() - 1) {
                                if (j == 0) {
                                    if (address.matches("")) {
                                        data2[j + 1] = dataList.get(j);
                                        address = dataList.get(j);
                                    } else {
                                        if (dataList.get(j).matches(address)) {
                                            data2[j + 1] = "-";
                                        } else {
                                            data2[j + 1] = dataList.get(j);
                                            address = dataList.get(j);
                                        }
                                    }
                                } else {
                                    data2[j + 1] = dataList.get(j);
                                }
                            } else {
                                JSONArray jsonArray = new JSONArray(dataList.get(j));
                                for (int k = 0; k < jsonArray.length(); k = k + 4) {
                                    String unit = getunit(jsonArray.get(k).toString());
                                    int n = csvtitle.indexOf(unit);
                                    if (k != jsonArray.length())
                                        data2[n] = jsonArray.get(k + 1).toString();
                                }
                            }
                            logMessage.showmessage(TAG, "if data2 = " + Arrays.toString(data2));
                        }
                        writer.writeNext(data2);
                    }
                    writer.close();
                } else {
                    FileOutputStream os = new FileOutputStream(filePath);
                    os.write(BOM_UTF8);
                    //FileWriter mFileWriter = new FileWriter(filePath, false);
                    writer = new CSVWriter(new OutputStreamWriter(os));
                    writer.writeNext(data_array);
                    for (int i = 0; i < recordList.size(); i++) {
                        data2 = new String[csvtitle.size()];
                        for (int k = 0; k < data2.length; k++) {
                            data2[k] = "";
                        }
                        List<String> dataList = new ArrayList<>();
                        dataList.clear();
                        dataList = recordList.get(i);
                        logMessage.showmessage(TAG, "dataList = " + dataList);
                        for (int j = 0; j < dataList.size(); j++) {
                            if (j == 0) {
                                data2[j] = String.valueOf(i);
                            }
                            if (j != dataList.size() - 1) {
                                if (j == 0) {
                                    if (address.matches("")) {
                                        data2[j + 1] = dataList.get(j);
                                        address = dataList.get(j);
                                    } else {
                                        if (dataList.get(j).matches(address)) {
                                            data2[j + 1] = "-";
                                        } else {
                                            data2[j + 1] = dataList.get(j);
                                            address = dataList.get(j);
                                        }
                                    }
                                } else {
                                    data2[j + 1] = dataList.get(j);
                                }
                            } else {
                                JSONArray jsonArray = new JSONArray(dataList.get(j));
                                for (int k = 0; k < jsonArray.length(); k = k + 4) {
                                    String unit = getunit(jsonArray.get(k).toString());
                                    int n = csvtitle.indexOf(unit);
                                    if (k != jsonArray.length())
                                        data2[n] = jsonArray.get(k + 1).toString();
                                }
                            }
                            logMessage.showmessage(TAG, "if data2 = " + Arrays.toString(data2));
                        }
                        writer.writeNext(data2);
                    }
                    writer.close();
                }

                csvdo = true;

                if (runningcsv.isCheck()) {
                    runningcsv.closeFlash();
                    csvtodo();
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    private void pdftodo(){
        Intent intent = new Intent(this, PDFView.class);
        startActivity(intent);
        finish();
    }

    private void csvtodo() {
        Uri csvuri = Uri.fromFile(file);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, csvuri);
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    private void backtofirst() {
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //toolbar menu item
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            vibrator.vibrate(100);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.export)
                    .setMessage(R.string.exportmessage)
                    .setPositiveButton("PDF", (dialog, which) -> {
                        vibrator.vibrate(100);
                        /*ArrayList<String> addresslist = new ArrayList<>();
                        addresslist.clear();
                        addresslist.addAll(alertRecord.address());*/
                        if (pdfdo) {
                            pdftodo();
                        } else {
                            runningpdf.startFlash(getString(R.string.exportingpdf));
                        }
                    })
                    .setNegativeButton("CSV", (dialog, which) -> {
                        vibrator.vibrate(100);
                        if (csvdo) {
                            csvtodo();
                        } else {
                            runningcsv.startFlash(getString(R.string.exportingcsv));
                        }
                    })
                    .setNeutralButton(R.string.butoon_no, (dialog, which) -> vibrator.vibrate(100))
                    .show();
            return true;
        } else if (id == R.id.delete) {
            vibrator.vibrate(100);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.deleterecord)
                    .setMessage(R.string.delete_message)
                    .setPositiveButton(R.string.butoon_yes, (dialog, which) -> {
                        vibrator.vibrate(100);
                        alertRecord.deleteAll();
                        backtofirst();
                    })
                    .setNeutralButton(R.string.butoon_no, (dialog, which) -> vibrator.vibrate(100))
                    .show();
            return true;
        }
        return true;
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK:
                vibrator.vibrate(100);
                backtofirst();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logMessage.showmessage(TAG, "onDestroy()");
        alertRecord.close();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMessage.showmessage(TAG, "onPause");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
            //show_device();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
            //show_device();
        }
    }
}
