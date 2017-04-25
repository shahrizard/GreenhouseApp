package com.example.admin.greenhouseapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.greenhouse.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainPage extends AppCompatActivity {
    private Button goTraits;
    private Button viewAll;
    DatabaseHelper myDb;
    private ZXingScannerView scannerView;
    TextView instruct;
    TextView textView;
    ImageView imageView;
    Bitmap mbitmap;
    Button captureScreenShot;
    private Button toOnline;
    Context context;
    private static final int MY_REQUEST_CODE = 100;
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.admin.greenhouse.R.layout.activity_reader);
        myDb = new DatabaseHelper(this);
        goTraits = (Button) findViewById(R.id.trait_btn);
     //   scan_btn = (Button) findViewById(com.example.admin.greenhouse.R.id.scan_btn);
        viewAll = (Button) findViewById(com.example.admin.greenhouse.R.id.viewAll);
        instruct = (TextView) findViewById(R.id.instructions);
        context = getApplicationContext();
        view_All();
        final Activity activity = this;
        instruct.setText("To use our greenhouse app, " +
                "just press scan QR/barcode and start scanning! " +
                "When scanning a plant, make sure the plant has a proper PlantID/PlantName, after that say the values " +
                "you want to be stored. If you make a mistake, don't worry you can retry!");
        for (int i = 0; i<2; i++){
            myDb.barcodeInsert("123456789-" + Integer.toString(i), "LeafRust", "3");
        }
        myDb.insertTraitValues("Color");
        myDb.insertTraitValues("LeafRust");
        /*
        scan_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        */
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String sResult;
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this, "You cancelled scanning", Toast.LENGTH_LONG).show();
            }
            else{
                sResult = result.getContents();
                Cursor res = myDb.getAllData();
                while (res.moveToNext()){
                        String pid = res.getString(1);
                        String pname = res.getString(2);
                        String leafrust = res.getString(3);
                        List<String> scanned = Arrays.asList(sResult.split(","));
                        if(scanned.get(0).contains(pid) && scanned.get(1).trim().contains(pname))
                        {
                            Intent intent = new Intent(context, DidYouMeanThis.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("pid", pid);
                            bundle.putString("pName", pname);
                            bundle.putString("leafrust", leafrust);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        }
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void editData(View view){
       // Intent intent = new Intent(this, CRUDplants.class);
       // startActivity(intent);
    }
*/

    public void gotoTraits(View view){

        Intent intent = new Intent(this, TraitsList.class);
        startActivity(intent);
    }

    public void exportData(View view){
        ExportDatabaseCSVTask csv = new ExportDatabaseCSVTask(this);
        csv.exportDataBaseIntoCSV();
    }

    public void goToScanner(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_REQUEST_CODE);


            } else {
                Intent intent = new Intent(this, BarcodeScanner.class);
                Bundle bundle = new Bundle();
                bundle.putString("voiceSpeech", "Audio Input Will Show Up Here");
                bundle.putBoolean("timer", false);
                bundle.putBoolean("camera", true);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        }
        else {
            Intent intent = new Intent(this, BarcodeScanner.class);
            Bundle bundle = new Bundle();
            bundle.putString("voiceSpeech", "Audio Input Will Show Up Here");
            bundle.putBoolean("timer", false);
            bundle.putBoolean("camera", true);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

   /* public void goToOnline(View view){
        Intent intent = new Intent(this, OnlineLogin.class);
        startActivity(intent);
    } */

        public void view_All(){
        viewAll.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick (View v) {
                        Cursor res = myDb.getAllData();
                        if (res.getCount() == 0){
                            //show message
                            showMessage("Error", "Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()){
                            buffer.append("Id: " + res.getString(0)+"\n");
                            buffer.append("BarcodeID: " + res.getString(1)+"\n");
                            buffer.append("Trait: " + res.getString(2)+"\n");
                            buffer.append("Val: " + res.getString(3)+"\n\n");
                        }
                        showMessage("Data", buffer.toString());
                    }
            }
        );
    }

    public void deleteAllData(View view) {
        myDb.emptyTableData();
    }


    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
//----screenshotting services

}
