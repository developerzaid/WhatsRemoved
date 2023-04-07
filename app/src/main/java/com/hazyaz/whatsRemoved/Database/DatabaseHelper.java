package com.hazyaz.whatsRemoved.Database;

import  android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "chats.db";
    public static final String TABLE_NAME = "MessagesList";
    public static final String COL_1_ID = "UNIQUEID";
    public static final String COL_2_NAME = "SENDERNAME";
    public static final String COL_3_DATE = "SENDERDATE";
    public static final String COL_4_MESSAGE = "SENDERMESSAGE";


    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        context = context;
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String NewTable = "CREATE TABLE " + TABLE_NAME + "(" + COL_1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_2_NAME + " TEXT," + COL_3_DATE + " TEXT," + COL_4_MESSAGE + " TEXT )";
        db.execSQL(NewTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Boolean insertData(String name, String time, String messgage) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_NAME, name);
        contentValues.put(COL_3_DATE, time);
        contentValues.put(COL_4_MESSAGE, messgage);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        Log.d("getalldata", "" + res);
        return res;

    }

    public void deleteCourse(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "SENDERNAME=?", new String[]{name});
        db.close();
    }





}
