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
        db.execSQL("CREATE TABLE Traits_List (TraitName VARCHAR NOT NULL UNIQUE)");
        db.execSQL("CREATE TABLE Audio_Dictionary (OldValue VARCHAR NOT NULL, NewValue VARCHAR NOT NULL)");
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

    public Cursor getTraitData(){
        Cursor res = db.rawQuery("select * from Traits_List", null);
        return res;
    }

    public Cursor getAudioData(){
        Cursor res = db.rawQuery("select * from Audio_Dictionary", null);
        return res;
    }

    public int getTraitRowCount(){
        Cursor res = db.rawQuery("select * from Traits_List", null);
        int count = res.getCount();
        return count;
    }

    public int getAudioRowCount(){
        Cursor res = db.rawQuery("select * from Audio_Dictionary", null);
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

    public boolean deleteSpecificRow(String barcodeID, String val){
        try {
            db.execSQL("DELETE FROM Plant_Info WHERE BarcodeID='" + barcodeID + "' AND Val='" + val + "'");
        }
        catch (SQLException ex){
            return false;
        }
        return true;
    }

    public boolean deleteTrait(String trait){
        try {
            db.execSQL("DELETE FROM Traits_List WHERE TraitName='" + trait +  "'");
        }
        catch (SQLException ex){
            return false;
        }
        return true;
    }

    public boolean deleteAudio(String to, String from){
        try {
            db.execSQL("DELETE FROM Audio_Dictionary WHERE OldValue='" + to +  "' AND NewValue ='" + from + "'");
        }
        catch (SQLException ex){
            return false;
        }
        return true;
    }

    public void insertTraitValues(String trait){
        try {
        db.execSQL("INSERT INTO Traits_List (TraitName) VALUES ('"+
                trait + "')");
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insertAudioValues(String from, String to){
        try {
            db.execSQL("INSERT INTO Audio_Dictionary (OldValue, NewValue) VALUES ('"+
                    from + "','" + to + "')");
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
