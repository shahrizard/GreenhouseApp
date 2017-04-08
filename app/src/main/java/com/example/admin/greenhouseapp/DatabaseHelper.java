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
    public static final String TABLE_NAME = "PlantInformation";
    public static final String COL_1 = "plant_ID";
    public static final String COL_2 = "height";
    Context context;
    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "PlantID INTEGER UNIQUE, PlantName VARCHAR, LeafRust VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void Insert(int pid, String pname, String leafrust){
        try{
            db.execSQL("INSERT INTO PlantInformation (PlantID, PlantName, LeafRust) VALUES ("+
                    pid + ",'" + pname + "'," + leafrust
                    + ")");
        }
        catch(SQLException exc){
            //
        }

    }

    public Cursor getAllData(){
        Cursor res = db.rawQuery("select * from PlantInformation", null);
        return res;
    }

    public int getRowCount(){
        Cursor res = db.rawQuery("select * from PlantInformation", null);
        int count = res.getCount();
        return count;
    }

    public void emptyTableData(){
        db.execSQL("delete from " + TABLE_NAME);
    }

    public boolean deleteSpecificRow(String pName){
        try {
            db.execSQL("DELETE FROM PlantInformation WHERE PlantName='" + pName + "'");
        }
        catch (SQLException ex){
            return false;
        }
        return true;
    }
}
