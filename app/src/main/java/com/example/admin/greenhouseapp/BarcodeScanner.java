package com.example.admin.greenhouseapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.greenhouse.R;import com.google.zxing.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Admin on 4/4/2017.
 */
public class BarcodeScanner extends AppCompatActivity {

    private ZXingScannerView scannerView;
    private FrameLayout frame;

    private final int REQ_CODE_SPEECH_OUTPUT = 143;

    private Button cancelButton;
    private TextView viewId, viewTrait, viewVal, voiceInText, label;
    int count;
    String existsID, voiceText;

    DatabaseHelper myDb;
    CountDownTimer clock;
    Spinner spinner;
    Context context;
    Bundle bundle;
    boolean timer, camera;
    public int counter;
    ArrayAdapter<String> traitValueAdapter;
    String traitValues[] = { "LeafRust", "Height" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcodescanner);
        context = getApplicationContext();

        myDb = new DatabaseHelper(this);

        voiceInText = (TextView) findViewById(R.id.editText);
        label = (TextView) findViewById(R.id.label);
        cancelButton = (Button) findViewById(R.id.cancelBtn);
        viewId = (TextView) findViewById(R.id.viewId);
        viewTrait = (TextView) findViewById(R.id.viewTrait);
        viewVal = (TextView) findViewById(R.id.viewVal);
        cancelButton.setText("");

   //     homeButton = (Button) findViewById(R.id.home_btn);

        bundle = getIntent().getExtras();
        camera = bundle.getBoolean("camera");
        existsID = bundle.getString("existsID");
        timer = bundle.getBoolean("timer");
        voiceInText.setText(bundle.getString("voiceSpeech"));
        frame =  (FrameLayout) findViewById(R.id.frameLayout);

        //==spinner
        traitValueAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, traitValues);
        traitValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(traitValueAdapter);

        //==scan
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        //if camera is true, it will start the camera on the upper screen
        if (camera) {
            setupTable();
            frame.addView(scannerView);
            scannerView.startCamera();
        }
        //==timer, if timer is true, will set the timer, and stop the camera
        if (timer){
            counter = 3;
            cancelButton.setVisibility(View.VISIBLE);
            viewId.setVisibility(View.GONE);
            viewVal.setVisibility(View.GONE);
            viewTrait.setVisibility(View.GONE);
            cancelButton.setText("Retry Audio");
            voiceText = bundle.getString("voiceText"); //notice bundle grab

                clock = new CountDownTimer(5000, 1000){
                    public void onTick(long millisUntilFinished){
                        label.setText(String.valueOf(counter));
                        counter--;
                    }
                    public void onFinish(){
                        Intent intent = new Intent(context, BarcodeScanner.class);
                        Bundle bundle = new Bundle();
                        String voice =  "Audio Input Will Show Up Here";
                        bundle.putString("voiceSpeech", voice);
                        bundle.putBoolean("camera", true);
                        bundle.putBoolean("timer", false);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        myDb.barcodeInsert(existsID, voiceText); //will insert if not exists, will update if exists
                        Toast.makeText(BarcodeScanner.this, "Insert Complete!", Toast.LENGTH_SHORT).show();
                    }
                }.start();
        }
        // end of onCreate constructor
    }
//======================= Other Methods below

    //set up table sets up the below table of information on the camera screen
    public void setupTable(){
        TableLayout layout = (TableLayout) findViewById(R.id.layout_1);
        layout.setPadding(0,0, 0, 0);
        Cursor res = myDb.getAllData();
        while(res.moveToNext()) {
            TableRow tableRow = new TableRow(this);
            layout.addView(tableRow);
            Button button = new Button(this);
            button.setWidth(5);
            button.setText("Edit");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, DidYouMeanThis.class);
                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            TextView tview = new TextView(this);
            TextView tview1 = new TextView(this);
            TextView tview2 = new TextView(this);
            tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            tview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            tview2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            tview.setText(res.getString(1));
            tview1.setText(res.getString(2));
            tview2.setText(res.getString(3));
            tview.setPadding(0, 10, 0, 10);
            tview1.setPadding(30, 10, 30, 10);
            tview2.setPadding(50,10,50,10);
            tableRow.addView(button);
            tableRow.addView(tview);
            tableRow.addView(tview1);
            tableRow.addView(tview2);
        }
    }
///=================================
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    public void goHome(View view){ //button that works for both scan and not-scan screen
        if (timer) { //if the timer is turned on and is going then cancel the clock when we click on this method
            clock.cancel();
        }
        Intent intent = new Intent(context, MainPage.class);
        startActivity(intent);
        finish();

    }

    public void cancelAudio(View view){ //retry button
        clock.cancel();
        OpenMic(); //restart
    }
//========================strictly for barcode handling
    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
        @Override
        public void handleResult(Result result) {
            String resultCode = result.getText(); //retrieves barcode input
            Toast.makeText(BarcodeScanner.this, resultCode, Toast.LENGTH_SHORT).show();
            existsID = resultCode;

            setContentView(R.layout.barcodescanner);
            scannerView.stopCamera();
            OpenMic();
        }
    }

//======================================audio input
    //!!! do not worry about these methods, just know the OpenMic allows you to voice chat again.
    private void OpenMic(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your trait value");

        try{
            startActivityForResult(intent, REQ_CODE_SPEECH_OUTPUT);
        }
        catch(ActivityNotFoundException ex){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){ //the resulting action of your voice to text
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQ_CODE_SPEECH_OUTPUT: {
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> VoiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInText.setText(VoiceInText.get(0));
                    voiceText = VoiceInText.get(0);
                    Intent intent = new Intent(context, BarcodeScanner.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("voiceSpeech", VoiceInText.get(0));
                    bundle.putBoolean("timer", true);
                    bundle.putString("existsID", existsID);
                    bundle.putString("voiceText", voiceText);
                    bundle.putBoolean("camera", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                }
                Toast.makeText(BarcodeScanner.this, "Nothing said - refreshed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, BarcodeScanner.class);
                Bundle bundle = new Bundle();
                bundle.putString("voiceSpeech", "Audio Input Will Show Up Here");
                bundle.putBoolean("camera", true);
                bundle.putBoolean("timer", false);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            }
        }
    }
    //=======end of audio input
}