package com.repon.first.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import static android.content.ContentValues.TAG;
import static com.repon.first.first_obj.nowtime;
import static com.repon.first.first_obj.uselog;


//SQLite資料庫設定
public class firstDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "first.db"; //SQLite裡的資料庫名稱
    private static final int VERSION = 1; //資料庫版本，資料庫結構改變時要更改數字，通常+1
    public static final String DB_TABLE = "first_data"; //SQLite裡的資料表名稱
    private static final String crTBsql = "CREATE TABLE " + DB_TABLE + "(" + "id INTEGER PRIMARY KEY," +
                                          "enterdata TEXT NOT NULL," + "insert_date TEXT NOT NULL," +
                                          "modify_date TEXT" + ")";
    private static SQLiteDatabase db_first_list; //程式裡用的資料庫名稱


    public firstDb(@Nullable Context context, @Nullable String name,
                   @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context){

        if (db_first_list == null || !db_first_list.isOpen())
            db_first_list = new firstDb(context, DB_NAME,null, VERSION).getWritableDatabase();

        return db_first_list;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crTBsql);
    }

    //資料庫升版：系統自動偵測調用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_TABLE);
        onCreate(db);
    }

    //直接傳入SQLite語法做查詢
    public static Cursor rawquery(Context context, String sql){
        getDatabase(context); //開啟資料庫
        Cursor c = null;

        try{
            c = db_first_list.rawQuery(sql,null);

        }catch (Exception e){
            if (uselog) Log.d(TAG,"error=" + e.toString());
        }

        return c;
    }

    //新增資料
    public static long insert(Context context, String getData){
        String time = nowtime("yyyy/M/d HH:mm"); //抓曲目前時間
        getDatabase(context); //開啟資料庫
        ContentValues newRow = new ContentValues();
        newRow.put("enterdata", getData);
        newRow.put("insert_date", time);

        long rowID = db_first_list.insert(DB_TABLE,null, newRow);
        db_first_list.close(); //寫入完成關閉

        return rowID;
    }

    //修改資料
    public static int update(Context context, String getData, String getId){
        String time = nowtime("yyyy/M/d HH:mm"); //抓曲目前時間

        getDatabase(context); //開啟資料庫

        ContentValues newRows = new ContentValues();
        newRows.put("enterdata", getData);
        newRows.put("modify_date", time);

        String where = "id = '" + getId + "'"; //where條件

        int rowID = db_first_list.update(DB_TABLE, newRows, where,null);
        db_first_list.close(); //寫入完成關閉

        return rowID;
    }

    //刪除資料
    public static int delete(Context context, String where){
        getDatabase(context); //開啟資料庫
        String sql = "SELECT * FROM " + DB_TABLE;
        int rowAffected = -1;

        try{
            Cursor c = db_first_list.rawQuery(sql,null);

            if (c != null){

                if (c.getCount() != 0){
                    rowAffected = db_first_list.delete(DB_TABLE, where,null);
                    db_first_list.close();
                }

                c.close();
            }

        }catch (Exception e){
            if (uselog) Log.d(TAG,"error=" + e.toString());

        }finally {
            if (db_first_list != null)
                db_first_list.close();
        }

        return rowAffected;
    }
}
