package com.example.admin.greenhouse;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Admin on 3/18/2017.
*/
public class OnlineLogin extends AppCompatActivity {
    private static final String url = "jdbc:mysql://50.62.209.88:3306/plexa_R";
    private static final String user = "testfarm";
    private static final String password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(com.example.admin.greenhouse.R.layout.onlinepage);
        testDB();
    }

    public void testDB(){
    /*        TextView tv = (TextView)this.findViewById(R.id.text_view);
        String result = "fish";
        try {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,user,password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from basichelp;");
            while(rs.next()){
                result += rs.getString("tree");
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
        tv.setText(result);
    } */
}
