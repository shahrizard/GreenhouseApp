package com.example.admin.greenhouseapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.admin.greenhouse.R;

/**
 * Created by Admin on 4/24/2017.
 */
public class AudioChange extends AppCompatActivity {
    DatabaseHelper myDb;
    Context context;
    private Button submit_btn;
    private EditText editTextFrom, editTextTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audiochange);
        myDb = new DatabaseHelper(this);
        context = getApplicationContext();
        submit_btn = (Button) findViewById(R.id.submit_btn);
        editTextFrom = (EditText) findViewById(R.id.editTextFrom);
        editTextTo = (EditText) findViewById(R.id.editTextTo);
        setupTable();
    }

    public void setupTable() {
        TableLayout layout = (TableLayout) findViewById(R.id.layout_1);
        layout.setPadding(0, 0, 0, 0);
        Cursor res = myDb.getAudioData();
        int count = myDb.getAudioRowCount();
        if (count > 0) {
            while (res.moveToNext()) {
                TableRow tableRow = new TableRow(this);
                layout.addView(tableRow);
                ImageButton button = new ImageButton(this);
                button.setImageResource(getResources().getIdentifier("abc_ic_clear_material", "drawable", getPackageName()));
                final String pos = res.getString(0);
                final String pos1 = res.getString(1);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        myDb.deleteAudio(pos, pos1);
                        Intent intent = new Intent(context, AudioChange.class);
                        startActivity(intent);
                        finish();
                    }
                });

                TextView tview = new TextView(this);
                TextView tview1 = new TextView(this);
                TextView tview2 = new TextView(this);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                    tview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
                } else {
                    tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
                    tview1.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
                }
                tview.setText(res.getString(0));
                tview1.setText(res.getString(1));
                tview.setPadding(10, 30, 10, 0);
                tview1.setPadding(10, 30, 10, 0);
                tableRow.addView(button);
                tableRow.addView(tview);
                tview2.setText("=>");
                tableRow.addView(tview2);
                tableRow.addView(tview1);
            }
        }
    }

    public void submitAudio(View view){
        String from = editTextFrom.getText().toString();
        String to = editTextTo.getText().toString();
        from.trim();
        to.trim();
        if (to.length() < 1 || to.isEmpty() || from.length() < 1 || from.isEmpty()){

        }
        else {
            myDb.insertAudioValues(from,to);
        }
        Intent intent = new Intent(this, AudioChange.class);
        startActivity(intent);
    }

    public void gotoHome(View view){
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
    }
}
