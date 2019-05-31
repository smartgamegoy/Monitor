package com.jetec.Monitor.SupportFunction.SQL;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

public class DataListSQL extends SQLiteOpenHelper {

    private final static String table_name = "datalist"; //資料表名
    private final static String db_name = "listsql.db";    //資料庫名
    private static final int VERSION = 2;

    public DataListSQL(Context context) {
        super(context, db_name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //ok
        String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table_name + "(" +
                "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," +
                "num" + " TEXT," +
                "model" + " TEXT," +
                "savelist" + " TEXT" + ")";
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

    public int getCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name, null);
        return cursor.getCount();
    }

    public void delete(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table_name, "_id=" + id ,null);
    }

    public void insert(String saveList, String model, String num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("num", num);
        cv.put("model", model);
        cv.put("savelist", saveList);

        db.insert(table_name, num, cv);
    }

    public int modelsearch(String model){
        int count;

        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor=db.rawQuery("SELECT * FROM " + table_name + " WHERE model=?", new String[]{model});

        count = cursor.getCount();
        Log.e("myLog","count =" + count);
        return count;
    }

    public ArrayList<HashMap<String, String>> fillList(String model){

        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor=db.rawQuery("SELECT * FROM " + table_name+ " WHERE model=?", new String[]{model});
        cursor.moveToFirst();
        do {
            String id = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
            String num = cursor.getString(cursor.getColumnIndex("num"));
            String savelist = cursor.getString(cursor.getColumnIndex("savelist"));

            HashMap<String, String> map = new HashMap<>();
            Log.e("putmap", "id" + id);
            map.put("id", id);
            map.put("num", num);
            map.put("savelist", savelist);

            dataList.add(map);
        }while(cursor.moveToNext());
        return dataList;
    }

    public int getCount(String name, String model){
        int count;

        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor=db.rawQuery("SELECT * FROM " + table_name + " WHERE num=? AND model=?", new String[]{name, model});

        count = cursor.getCount();
        return count;
    }

    public void update(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("num", name);

        db.update(table_name, cv, "_id=" + id, null);
    }
}
