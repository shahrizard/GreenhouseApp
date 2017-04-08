package com.example.admin.greenhouseapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
    private Button scan_btn;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.admin.greenhouse.R.layout.activity_reader);
        myDb = new DatabaseHelper(this);
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

    public void goToScanner(View view){
        Intent intent = new Intent(this, BarcodeScanner.class);
        Bundle bundle = new Bundle();
        bundle.putString("voiceSpeech", "Audio Input Will Show Up Here");
        bundle.putBoolean("timer", false);
        bundle.putBoolean("camera", true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
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
