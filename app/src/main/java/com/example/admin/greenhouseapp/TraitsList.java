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
 * Created by Admin on 4/18/2017.
 */
public class TraitsList extends AppCompatActivity {
    DatabaseHelper myDb;
    Context context;
    private Button home_btn, add_btn;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traitslist);
        myDb = new DatabaseHelper(this);
        context = getApplicationContext();
        home_btn = (Button) findViewById(R.id.home_btn);
        add_btn = (Button) findViewById(R.id.add_btn);
        editText = (EditText) findViewById(R.id.editText2);
        setupTable();
    }

    public void setupTable(){
        TableLayout layout = (TableLayout) findViewById(R.id.layout_1);
        layout.setPadding(0,0, 0, 0);
        Cursor res = myDb.getTraitData();
        while(res.moveToNext()) {
            TableRow tableRow = new TableRow(this);
            layout.addView(tableRow);
            ImageButton button = new ImageButton(this);
            button.setImageResource(getResources().getIdentifier("abc_ic_clear_material", "drawable", getPackageName()));
            final String pos = res.getString(0);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    myDb.deleteTrait(pos);
                    Intent intent = new Intent(context, TraitsList.class);
                    startActivity(intent);
                    finish();
                }
            });

            TextView tview = new TextView(this);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
            }
            else {
                tview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
            }
            tview.setText(res.getString(0));
            tview.setPadding(10, 30, 10, 0);
            if(pos.contains("LeafRust")  || pos.contains("Color")){
                //nothing
            }
            else tableRow.addView(button);
            tableRow.addView(tview);
        }
    }

    public void gotoHome(View view){
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
    }

    public void addTrait(View view){
        String text = editText.getText().toString();
        text.trim();
        if (text.length() < 1 || text.isEmpty()){

        }
        else {
            myDb.insertTraitValues(text);
        }
        Intent intent = new Intent(this, TraitsList.class);
        startActivity(intent);
    }
}

