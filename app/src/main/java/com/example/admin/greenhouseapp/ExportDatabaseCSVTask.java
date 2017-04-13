package com.example.admin.greenhouseapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.greenhouse.R;

import java.io.File;
import java.io.FileWriter;





public class ExportDatabaseCSVTask extends AppCompatActivity{

    Context context;
    public ExportDatabaseCSVTask(){};
    public ExportDatabaseCSVTask(Context context) {
        this.context=context;
    }

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void exportDataBaseIntoCSV(){


        DatabaseHelper db = new DatabaseHelper(context);//here CredentialDb is my database. you can create your db object.
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");

        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "plantdata.csv");

        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase sql_db = db.getReadableDatabase();//here create a method ,and return SQLiteDatabaseObject.getReadableDatabase();
            Cursor curCSV = sql_db.rawQuery("SELECT * FROM "+DatabaseHelper.TABLE_NAME,null);
            csvWrite.writeNext(curCSV.getColumnNames());

            while(curCSV.moveToNext())
            {
                //Which column you want to export you can add over here...
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }

            csvWrite.close();
            curCSV.close();
          // Toast.makeText(ExportDatabaseCSVTask.this, "CSV created! Check storage/emulated/0/ directory for plantdata.csv", Toast.LENGTH_LONG).show();
        }
        catch(Exception sqlEx)
        {
            Log.e("Error:", sqlEx.getMessage(), sqlEx);
          // Toast.makeText(ExportDatabaseCSVTask.this, "CSV not created - check debugger", Toast.LENGTH_LONG).show();
        }
    }
}