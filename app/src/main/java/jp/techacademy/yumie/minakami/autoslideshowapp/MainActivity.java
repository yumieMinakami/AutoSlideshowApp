package jp.techacademy.yumie.minakami.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private Handler handler;
    private Timer tmr;
    ContentResolver resolver;
    Cursor cursor;
    Button bt_back;
    Button bt_next;
    Button bt_st1;
    TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android ver check
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // confirm PERMISSION's approval status
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // not approved, show approval dialog
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
        }

        resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   // data type
                null,
                null,
                null,
                null
        );
//        Log.d("LESSON5_8-3", "ColumnCount : " + cursor.getColumnCount());

        // buttons
        bt_back = (Button) findViewById(R.id.button_back);
        bt_back.setOnClickListener(this);

        bt_next = (Button) findViewById(R.id.button_next);
        bt_next.setOnClickListener(this);

        bt_st1 = (Button) findViewById(R.id.button_ss);
        bt_st1.setText("再生");
        bt_st1.setOnClickListener(this);

//        Button bt_st2 = (Button) findViewById(R.id.button_stop);
//        bt_st2.setOnClickListener(this);


        //        // ver 6.0 or later
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            // confirm PERMISSION's approval status
//            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                getContentsInfo();  // approved
//            } else{
//                // not approved, show approval dialog
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
//            }
//        } else{     // ver 5 or former
//            getContentsInfo();
//        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
        Log.d("LESSON5_8-3", "OnDestroy");
    }

    private void getContentsInfo(Cursor c){

        if(c != null){
            int fieldIndex = c.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = c.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIEW = (ImageView) findViewById(R.id.imageView);
            imageVIEW.setImageURI(imageUri);

            Log.d("LESSON5_8-3", "URI : " + imageUri.toString());
        } else
            Log.d("LESSON5_8-3", "No image.");
 //       cursor.close();
    }

    @Override
    public void onClick(View v) {
        if(cursor.getCount() < 1){
            TextView mtextView = (TextView) findViewById(R.id.textview1);
            mtextView.setText("No Image.");
        } else {
            switch (v.getId()) {
                case R.id.button_back:
                    if (cursor.isFirst()) {
                        cursor.moveToLast();
                    } else {
                        cursor.moveToPrevious();
                    }
                    getContentsInfo(cursor);
                    break;
                case R.id.button_next:
                    if (cursor.isLast())
                        cursor.moveToFirst();
                    else
                        cursor.moveToNext();
                    getContentsInfo(cursor);
                    break;
                case R.id.button_ss:
                    Log.d("LESSON5_8-3", bt_st1.getText().toString());
                    if (bt_st1.getText().toString() == "再生") {
                        bt_st1.setText("停止");
                        bt_back.setEnabled(false);
                        bt_next.setEnabled(false);

                        tmr = new Timer();
                        handler = new Handler();
                        tmr.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (cursor.moveToNext())
                                            getContentsInfo(cursor);
                                        else {
                                            cursor.moveToFirst();
                                            getContentsInfo(cursor);
                                        }
                                    }
                                });
                            }
                        }, 2000, 2000);
                    } else {
                        bt_st1.setText("再生");
                        if(tmr != null){
                            tmr.cancel();
                            tmr = null;
                        }
                        bt_back.setEnabled(true);
                        bt_next.setEnabled(true);
                    }
                    break;
                default:
                    Log.d("LESSON5_8-3", "ERR");
                    break;
            }
        }
//        cursor.close();
    }
}
