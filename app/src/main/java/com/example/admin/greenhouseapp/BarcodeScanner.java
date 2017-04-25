package com.example.admin.greenhouseapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.admin.greenhouse.R;import com.google.zxing.Result;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Admin on 4/4/2017.
 */
public class BarcodeScanner extends AppCompatActivity{

    private ZXingScannerView scannerView;
    private FrameLayout frame;

    private final int REQ_CODE_SPEECH_OUTPUT = 143;

    private Button cancelButton;
    private TextView viewId, viewTrait, viewVal, voiceInText, label, trait, dataBeingEdited;
    int count;
    String existsID, voiceText, traitGiven;

    TextView selVersion;
    private String[] state = { "", "Delete", "Edit" };

  //  Spinner spinnerOsversions;
    ImageButton next;
    DatabaseHelper myDb;
    CountDownTimer clock;
    Spinner spinner;
    Context context;
    Bundle bundle;
    boolean timer, camera;
    public int counter;
    int index;
    ArrayList<String> traits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcodescanner);
        context = getApplicationContext();

        myDb = new DatabaseHelper(this);

        dataBeingEdited = (TextView) findViewById(R.id.dataBeingEdited);
        voiceInText = (TextView) findViewById(R.id.editText);
        label = (TextView) findViewById(R.id.label);
        cancelButton = (Button) findViewById(R.id.cancelBtn);
        viewId = (TextView) findViewById(R.id.viewId);
        viewTrait = (TextView) findViewById(R.id.viewTrait);
        viewVal = (TextView) findViewById(R.id.viewVal);
        trait = (TextView) findViewById(R.id.trait);
        next = (ImageButton) findViewById(R.id.nextText);
        cancelButton.setText("");


   //     homeButton = (Button) findViewById(R.id.home_btn);

        bundle = getIntent().getExtras();
        camera = bundle.getBoolean("camera");
        existsID = bundle.getString("existsID");
        timer = bundle.getBoolean("timer");
        voiceInText.setText(bundle.getString("voiceSpeech"));
        frame =  (FrameLayout) findViewById(R.id.frameLayout);

        //==scan
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        //trait set up
        count = myDb.getTraitRowCount();
        traits = new ArrayList<String>(count);
        Cursor res = myDb.getTraitData();
        int i = 0;
        while(res.moveToNext()){
            traits.add(res.getString(0));
            i++;
        }
        index = 0;
        traitGiven = traits.get(index);

        //if camera is true, it will start the camera on the upper screen and no timer will be started
        if (camera) {
            setupTable();
            trait.setText("Color");
            frame.addView(scannerView);
            scannerView.startCamera();
        }
        //==timer, if timer is true, will set the timer, and stop the camera
        if (timer){
            counter = 3;
            trait.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            dataBeingEdited.setVisibility(View.VISIBLE);
            viewId.setVisibility(View.GONE);
            viewVal.setVisibility(View.GONE);
            viewTrait.setVisibility(View.GONE);
            cancelButton.setText("Retry Audio");
            voiceText = bundle.getString("voiceText"); //notice bundle grab
            traitGiven = bundle.getString("traitGiven");
            dataBeingEdited.setText("Data Entry being edited: " + existsID + " " + traitGiven);
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
                        myDb.barcodeInsert(existsID, traitGiven, voiceText); //will insert if not exists, will update if exists
                        Toast.makeText(BarcodeScanner.this, "Insert Complete!", Toast.LENGTH_SHORT).show();
                    }
                }.start();
        }
        // end of onCreate constructor
    }
//======================= Other Methods below
    public void nextTextShow(View view){ //for trait selection
        index++;
        if (index == count) {
            index = 0;
        }
            traitGiven = traits.get(index);
            trait.setText(traits.get(index));

    }


    public void onBackPressed(String id, String val) { //confirmation dialog
        final String pos = id;
        final String pos2 = val;
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting " + id)
                .setMessage("Are you sure you want to delete " + id + " with a value of " + val + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDb.deleteSpecificRow(pos, pos2);
                        Intent intent = new Intent(context, BarcodeScanner.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("timer", false);
                        bundle.putString("existsID", existsID);
                        bundle.putString("traitGiven", traitGiven);
                        bundle.putString("voiceText", "Audio Input Will Show Up Here" );
                        bundle.putBoolean("camera", true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    //set up table sets up the below table of information on the camera screen
    public void setupTable(){
        TableLayout layout = (TableLayout) findViewById(R.id.layout_1);
        layout.setPadding(0,0, 0, 0);
        Cursor res = myDb.getAllData();
        while(res.moveToNext()) {
            TableRow tableRow = new TableRow(this);
            layout.addView(tableRow);
            ImageButton button = new ImageButton(this);
            button.setImageResource(getResources().getIdentifier("abc_ic_clear_material", "drawable", getPackageName()));
            final String pos = res.getString(1);
            final String pos1 = res.getString(2);
            final String pos2 = res.getString(3);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onBackPressed(pos, pos2);
                }
            });

            ImageButton button1 = new ImageButton(this);
            button1.setImageResource(getResources().getIdentifier("abc_ic_voice_search_api_material", "drawable", getPackageName()));
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    existsID = pos;
                    traitGiven = pos1;
                    OpenMic();
                }
            });

            TextView tview = new TextView(this);
            TextView tview1 = new TextView(this);
            TextView tview2 = new TextView(this);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 70);
                tview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 70);
                tview2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 70);
            }
            else {
                tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                tview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                tview2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
            }
            tview.setText(res.getString(1));
            tview1.setText(res.getString(2));
            tview2.setText(res.getString(3));
            tview.setPadding(10, 30, 10, 0);
            tview1.setPadding(30, 30, 30, 0);
            tview2.setPadding(50,30,50,10);
            tableRow.addView(button);
            tableRow.addView(button1);
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
                    bundle.putString("traitGiven", traitGiven);
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

    /* This commmented out code is using the Overflow menu - put inside setup Table method
            spinnerOsversions = new Spinner(this);
            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, state);
            adapter_state
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerOsversions.setAdapter(adapter_state);
            final String pos = res.getString(1);
            final String pos2 = res.getString(3);
            spinnerOsversions.setSelection(0);
            spinnerOsversions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    String selState = (String) spinnerOsversions.getSelectedItem();
                    if(selState == "Delete"){
                        myDb.deleteSpecificRow(pos, pos2);
                        Intent intent = new Intent(context, BarcodeScanner.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("timer", false);
                        bundle.putString("existsID", existsID);
                        bundle.putString("traitGiven", traitGiven);
                        bundle.putString("voiceText", "Audio Input Will Show Up Here" );
                        bundle.putBoolean("camera", true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            }); */
}