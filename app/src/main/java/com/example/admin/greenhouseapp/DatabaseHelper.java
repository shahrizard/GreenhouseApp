package com.example.admin.greenhouseapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 2/18/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    public static final String DATABASE_NAME = "plants.db";
    public static final String TABLE_NAME = "Plant_Info";
    Context context;
    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BarcodeID VARCHAR, Trait VARCHAR, Val VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllData(){
        Cursor res = db.rawQuery("select * from Plant_Info", null);
        return res;
    }

    public int getRowCount(){
        Cursor res = db.rawQuery("select * from Plant_Info", null);
        int count = res.getCount();
        return count;
    }

    public void emptyTableData(){
        db.execSQL("delete from " + TABLE_NAME);
    }

    public void barcodeInsert(String id, String trait, String voiceText) {
        Cursor res = db.rawQuery("select * from Plant_Info where BarcodeID='" + id + "'", null);
        int count = res.getCount();
        if(count == 0){
            db.execSQL("INSERT INTO Plant_Info (BarcodeID, Trait, Val) VALUES ('"+
                    id + "','" + trait + "','" + voiceText
                    + "')");

        }
        db.execSQL("UPDATE Plant_Info SET Val = '" + voiceText + "' WHERE BarcodeID='" + id + "';");

    }

    public boolean deleteSpecificRow(String pName){
        try {
            //db.execSQL("DELETE FROM PlantInformation WHERE PlantName='" + pName + "'");
        }
        catch (SQLException ex){
            return false;
        }
        return true;
    }
}
