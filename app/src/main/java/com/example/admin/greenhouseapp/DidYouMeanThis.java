package com.example.admin.greenhouseapp;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Admin on 3/23/2017.
 */

import com.example.admin.greenhouse.R;

public class DidYouMeanThis extends AppCompatActivity {
    String pid;
    String pName;
    TextView pNameView;
    TextView pidView;
    TextView leafrustView;
    DatabaseHelper myDb;
    Context context;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.admin.greenhouse.R.layout.didyoumeanthis);
        myDb =  new DatabaseHelper(this);
        context = getApplicationContext();
        button = (Button) findViewById(R.id.delButton);
        setValues();
    }

    public void setValues(){
        pNameView  = (TextView)this.findViewById(R.id.pNameView);
        pidView = (TextView)this.findViewById(R.id.pidView);
        leafrustView = (TextView)this.findViewById(R.id.leafrustView);
        Bundle bundle = getIntent().getExtras();
        pid = bundle.getString("pid");
        pName = bundle.getString("pName");
        pidView.setText(pid);
        pNameView.setText(pName);
    }

    public void deleteRow(View view){
        boolean result = false;
        result = myDb.deleteSpecificRow(pName);
        if(result){
            Toast.makeText(this, "Deleted!", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(this, "Data entry not found!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, MainPage.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", result);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
