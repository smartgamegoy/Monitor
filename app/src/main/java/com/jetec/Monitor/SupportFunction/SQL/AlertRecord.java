package com.jetec.Monitor.SupportFunction.SQL;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AlertRecord extends SQLiteOpenHelper {

    private final static String table_name = "alertlist"; //資料表名
    private final static String db_name = "alertsql.db";    //資料庫名
    private static final int VERSION = 2;

    public AlertRecord(Context context) {
        super(context, db_name, null, VERSION);
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(table_name, null, null, null, null,
                null, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //ok
        String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table_name + "(" +
                "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," +
                "address" + " TEXT, " +
                "devicename" + " TEXT," +
                "time" + " TEXT," +
                "jsonvalue" + " TEXT" + ")";
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //oldVersion=舊的資料庫版本；newVersion=新的資料庫版本
        //db.execSQL("DROP TABLE IF EXISTS " + table_name); //刪除舊有的資料表
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // TODO 每次成功打開數據庫後首先被執行
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    public int getCount(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?", new String[]{address});
        //Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);
        return cursor.getCount();
    }

    public int size() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);
        return cursor.getCount();
    }

    public ArrayList<String> address() {
        ArrayList<String> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        dataList.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);

        cursor.moveToFirst();
        do {
            boolean ckeck = false;
            String address = cursor.getString(cursor.getColumnIndex("address"));
            if (dataList.size() == 0) {
                dataList.add(address);
            } else {
                for (int i = 0; i < dataList.size(); i++) {
                    String a = dataList.get(i);
                    if (a.matches(address)) {
                        ckeck = true;
                    }
                }
                if (!ckeck) {
                    dataList.add(address);
                }
            }
        } while (cursor.moveToNext());

        return dataList;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table_name);
    }

    public ArrayList<List<String>> allRecordList() {
        ArrayList<List<String>> dataList = new ArrayList<>();
        dataList.clear();

        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);

        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            do {
                List<String> recordList = new ArrayList<>();
                recordList.clear();
                String devicename = cursor.getString(cursor.getColumnIndex("devicename"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String jsonvalue = cursor.getString(cursor.getColumnIndex("jsonvalue"));

                recordList.add(devicename);
                recordList.add(time);
                recordList.add(address);
                recordList.add(jsonvalue);

                dataList.add(recordList);
            } while (cursor.moveToPrevious());
        }

        return dataList;
    }

    public void delete(String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] del = new String[]{address};
        db.delete(table_name, "address" + "=?", del);
    }

    public void insert(String address, String devicename, String time, JSONArray jsonvalue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("address", address);
        cv.put("devicename", devicename);
        cv.put("time", time);
        cv.put("jsonvalue", jsonvalue.toString());

        db.insert(table_name, address, cv);
    }

    public List<String> getlist(String address) {
        List<String> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?", new String[]{address});

        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            Log.e("myLog", "cursor =" + cursor.getCount());
            String devicename = cursor.getString(cursor.getColumnIndex("devicename"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String jsonvalue = cursor.getString(cursor.getColumnIndex("jsonvalue"));

            dataList.add(devicename);
            dataList.add(time);
            dataList.add(jsonvalue);

        }
        return dataList;
    }

    public ArrayList<List<String>> getRecordList(String address) {
        ArrayList<List<String>> dataList = new ArrayList<>();
        dataList.clear();

        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?", new String[]{address});

        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            do {
                List<String> recordList = new ArrayList<>();
                recordList.clear();
                String devicename = cursor.getString(cursor.getColumnIndex("devicename"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String jsonvalue = cursor.getString(cursor.getColumnIndex("jsonvalue"));

                recordList.add(devicename);
                recordList.add(time);
                recordList.add(address);
                recordList.add(jsonvalue);

                dataList.add(recordList);
            } while (cursor.moveToPrevious());
        }

        return dataList;
    }

    public ArrayList<List<String>> getsortRecordList(ArrayList<String> recordAddress) {
        ArrayList<List<String>> dataList = new ArrayList<>();
        dataList.clear();

        SQLiteDatabase db = this.getReadableDatabase();

        for (int i = 0; i < recordAddress.size(); i++) {
            @SuppressLint("Recycle")
            Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?",
                    new String[]{recordAddress.get(i)});

            if (cursor.getCount() != 0) {
                cursor.moveToLast();
                do {
                    List<String> recordList = new ArrayList<>();
                    recordList.clear();
                    String devicename = cursor.getString(cursor.getColumnIndex("devicename"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String jsonvalue = cursor.getString(cursor.getColumnIndex("jsonvalue"));

                    recordList.add(devicename);
                    recordList.add(time);
                    recordList.add(address);
                    recordList.add(jsonvalue);

                    dataList.add(recordList);
                } while (cursor.moveToPrevious());
            }
        }

        return dataList;
    }

    public ArrayList<List<String>> exportList(ArrayList<String> recordAddress) {
        ArrayList<List<String>> dataList = new ArrayList<>();
        dataList.clear();

        SQLiteDatabase db = this.getReadableDatabase();

        for (int i = 0; i < recordAddress.size(); i++) {
            @SuppressLint("Recycle")
            Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?",
                    new String[]{recordAddress.get(i)});

            if (cursor.getCount() != 0) {
                cursor.moveToLast();
                do {
                    List<String> recordList = new ArrayList<>();
                    recordList.clear();
                    String devicename = cursor.getString(cursor.getColumnIndex("devicename"));
                    String time = cursor.getString(cursor.getColumnIndex("time"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String jsonvalue = cursor.getString(cursor.getColumnIndex("jsonvalue"));

                    recordList.add(address);
                    recordList.add(devicename);
                    recordList.add(time);
                    recordList.add(jsonvalue);

                    dataList.add(recordList);
                } while (cursor.moveToPrevious());
            }
        }

        return dataList;
    }

    public List<Integer> device_count(ArrayList<String> recordAddress){
        List<Integer> dataList = new ArrayList<>();
        dataList.clear();

        SQLiteDatabase db = this.getReadableDatabase();

        for (int i = 0; i < recordAddress.size(); i++) {
            @SuppressLint("Recycle")
            Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?",
                    new String[]{recordAddress.get(i)});

            if (cursor.getCount() != 0) {
                dataList.add(cursor.getCount());
            }
        }

        return dataList;
    }

    public List<String> pdftitle(ArrayList<String> recordAddress) {
        List<String> dataList = new ArrayList<>();
        dataList.clear();

        SQLiteDatabase db = this.getReadableDatabase();

        for (int i = 0; i < recordAddress.size(); i++) {
            @SuppressLint("Recycle")
            Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE address = ?",
                    new String[]{recordAddress.get(i)});

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                String devicename = cursor.getString(cursor.getColumnIndex("devicename"));
                String address = cursor.getString(cursor.getColumnIndex("address"));

                dataList.add(devicename);
                dataList.add(address);
            }
        }

        return dataList;
    }
}
