package com.example.admin.greenhouseapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;import com.example.admin.greenhouse.R;

/**
 * Created by Admin on 3/23/2017.
 */
public class CRUDplants extends AppCompatActivity {
    int count;
    DatabaseHelper myDb;
    Context context;
    String pid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crudplants);
        myDb = new DatabaseHelper(this);
        context = getApplicationContext();
        load();
    }

    public void load(){
        TableLayout layout = (TableLayout) findViewById(R.id.layout_1);
        layout.setPadding(0,0, 0, 0);
        count = 3;
        for (int c = 0; c < 15; c++) {
                TableRow tableRow = new TableRow(this);
                tableRow.setMinimumHeight(300);
                layout.addView(tableRow);
                Button button = new Button(this);
                button.setWidth(5);
                button.setText("Edit");
                pid = "dog";
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DidYouMeanThis.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("pid", pid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                TextView tview = new TextView(this);
                tview.setText(pid);
                TextView tview1 = new TextView(this);
                tview1.setText("fish");
                tview1.setPadding(10, 10, 10, 10);
                tableRow.addView(button);
                tableRow.addView(tview);
                tableRow.addView(tview1);
            }
        }
}
