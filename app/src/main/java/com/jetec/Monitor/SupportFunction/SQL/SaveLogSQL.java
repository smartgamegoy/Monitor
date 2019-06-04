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

public class SaveLogSQL extends SQLiteOpenHelper {

    private String TAG = "SaveLogSQL";
    private final static String table_name = "savelog"; //資料表名
    private final static String db_name = "savesql.db";    //資料庫名
    private static final int VERSION = 2;

    public SaveLogSQL(Context context) {
        super(context, db_name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {   //ok
        String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table_name + "(" +
                "_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," +
                "name" + " TEXT, " +    //方便查找資料之名稱標籤
                "saveTime" + " TEXT, " +
                "saveDate" + " TEXT, " +
                "saveInter" + " TEXT, " +
                "savelist" + " TEXT " + ")"; //存下的紀錄資料
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //oldVersion=舊的資料庫版本；newVersion=新的資料庫版本
        db.execSQL("DROP TABLE IF EXISTS " + table_name); //刪除舊有的資料表
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

    public int getCount(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor=db.rawQuery("SELECT * FROM " + table_name + " WHERE name=?", new String[]{name});
        return cursor.getCount();
    }

    public void delete(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE name = ?", new String[] {name});
        if(cursor.getCount() > 0) {
            String[] del = new String[]{name};
            db.delete(table_name, "name" + "=?", del);
        }
    }

    public void insert(String name, JSONArray savelist, String saveDate, String saveTime, String saveInter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Log.e(TAG, "savelist = " + savelist);
        cv.put("name", name);
        cv.put("saveTime", saveTime);
        cv.put("saveDate", saveDate);
        cv.put("saveInter", saveInter);
        cv.put("savelist", savelist.toString());

        db.insert(table_name, name, cv);
    }

    public ArrayList<String> getsaveLog(String name){
        ArrayList<String> jsonlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_name + " WHERE name = ?", new String[] {name});

        cursor.moveToFirst();
        String saveTime = cursor.getString(cursor.getColumnIndex("saveTime"));
        String saveDate = cursor.getString(cursor.getColumnIndex("saveDate"));
        String saveInter = cursor.getString(cursor.getColumnIndex("saveInter"));
        String save = cursor.getString(cursor.getColumnIndex("savelist"));

        jsonlist.add(saveTime);
        jsonlist.add(saveDate);
        jsonlist.add(saveInter);
        jsonlist.add(save);

        return jsonlist;
    }
}
