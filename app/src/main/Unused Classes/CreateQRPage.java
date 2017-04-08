package com.example.admin.greenhouse;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 2/18/2017.
*/
public class CreateQRPage extends AppCompatActivity {
    private Button create_btn;
    private Button home_btn;
    private static final int PERMS_REQUEST_CODE = 123;
    Context context;
    private EditText editText;
    ImageView image;
    String text2Qr;
    DatabaseHelper myDb;
    //---image
    TextView textView;
    ImageView imageView;
    Bitmap mbitmap;
    Button captureScreenShot;
//-t-------------------------------------------
    public void showToast(){
        Toast.makeText(this, "Needs to be more than one character", Toast.LENGTH_SHORT);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createpage);
        myDb = new DatabaseHelper(this);
        context = getApplicationContext();
        editText = (EditText) findViewById(R.id.editText);
        create_btn=(Button) findViewById(R.id.create_btn);
        image = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Your ScreenShot Image:");
        home_btn = (Button) findViewById(R.id.homeBtn);
        captureScreenShot = (Button) findViewById(R.id.capture_screen_shot);
        imageView = (ImageView) findViewById(R.id.imageView);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                    text2Qr = editText.getText().toString().trim();
                    List<String> text2QRlist = Arrays.asList(text2Qr.split(","));
                    try {
                        myDb.Insert(Integer.parseInt(text2QRlist.get(0)), text2QRlist.get(1), text2QRlist.get(2));
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200, 200);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            image.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                    catch(NumberFormatException exc){
                        Intent intent = new Intent(context, CreateQRPage.class);
                        startActivity(intent);
                    }
                    catch(ArrayIndexOutOfBoundsException exc){
                        Intent intent = new Intent(context, CreateQRPage.class);
                        startActivity(intent);
                    }

            }
        });
    }

 /*   private boolean hasPermissions(){
        int res = 0;
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms: permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res ==PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMS_REQUEST_CODE );
        }

    } */

    public void screenShot(View view) {
        mbitmap = getBitmapOFRootView(captureScreenShot);
        imageView.setImageBitmap(mbitmap);
        createImage(mbitmap);
    }

    public Bitmap getBitmapOFRootView(View v) {
        View rootview = v.getRootView();
        rootview.setDrawingCacheEnabled(true);
        Bitmap bitmap1 = rootview.getDrawingCache();
        return bitmap1;
    }

    public void createImage(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File file = new File(Environment.getExternalStorageDirectory() +
                "/capturedscreenandroid.jpg");
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goHome(View view){
        Intent intent = new Intent(context, MainPage.class);
        startActivity(intent);
    }
}
