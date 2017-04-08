package com.example.admin.greenhouseapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.greenhouse.R;import com.google.zxing.Result;

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
    private TextView voiceInText;
    private TextView label;
    private Button cancelButton;
    CountDownTimer clock;
    String speech;
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

        voiceInText = (TextView) findViewById(R.id.editText);
        label = (TextView) findViewById(R.id.label);
        cancelButton = (Button) findViewById(R.id.cancelBtn);
        cancelButton.setText("");
        counter = 3;

        //==variable initialization
        bundle = getIntent().getExtras();
        speech = bundle.getString("voiceSpeech");
        camera = bundle.getBoolean("camera");
        timer = bundle.getBoolean("timer");
        voiceInText.setText(speech);
        frame =  (FrameLayout) findViewById(R.id.frameLayout);

        //==spinner
        traitValueAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, traitValues);
        traitValueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(traitValueAdapter);

        //==scan
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());
        if (camera) { //if camera is true, it will start the camera on the upper screen
            frame.addView(scannerView);
            scannerView.startCamera();
        }
//test
        //==timer, if timer is true, will set the timer, and stop the camera
        if (timer){
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("Cancel");
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
                    Toast.makeText(BarcodeScanner.this, "Insert Complete!", Toast.LENGTH_SHORT).show();
                }
            }.start();
        }
    }
///=================================
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
//========================strictly for barcode handling
    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {
        @Override
        public void handleResult(Result result) {
            String resultCode = result.getText(); //retrieves barcode input
            Toast.makeText(BarcodeScanner.this, resultCode, Toast.LENGTH_SHORT).show();

            setContentView(R.layout.barcodescanner);
            scannerView.stopCamera();
            OpenMic();
        }
    }

    public void cancelAudio(View view){
        clock.cancel();
        OpenMic(); //restart
    }

//======================================audio input
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

                    Intent intent = new Intent(context, BarcodeScanner.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("voiceSpeech", VoiceInText.get(0));
                    bundle.putBoolean("timer", true);
                    bundle.putBoolean("camera", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
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
                break;
            }
        }
    }
}