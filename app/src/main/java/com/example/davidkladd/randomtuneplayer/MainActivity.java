package com.example.davidkladd.randomtuneplayer;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public TextView textView;
    Intent myIntent;
    Handler maHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check whether this app has write external storage permission or not.
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
// If do not grant write external storage permission.
        if(readExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
// Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textView.setText(String.valueOf(MyService.counter));
                MyService.ChooseNewSong();
            }
        });

        textView = findViewById(R.id.textView);

        if (isMyServiceRunning(MyService.class)){
            //textView.setText("Service Already Running");
            textView.setText(MyService.Anno);
        } else {
            textView.setText("Starting Service");
            myIntent = new Intent(getApplicationContext(), MyService.class);
            MyService.initContext(getApplicationContext());
            startService(myIntent);
        }

        // Make timer to update gui from service
// update text display regularly with announcement text
        maHandler = new Handler();
        Runnable rr = new Runnable(){
            public void run(){
                textView.setText(MyService.Anno);
                maHandler.postDelayed(this, 1000);
            }
        };
        maHandler.postDelayed(rr, 500);

        Toast.makeText(getApplicationContext(),  getLibraryCount() + " songs", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Quit) {
            //myIntent = new Intent(getApplicationContext(), MyService.class);
            //stopService(myIntent);
            MyService.killme();
            finishAffinity();
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public String getLibraryCount() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] columns = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };

        String libraryCount = "";
        String[] searchy = {"%" + "" + "%", "30000"};
        try {
            Cursor cursor = getContentResolver().query(uri, columns,  MediaStore.Audio.Media.TITLE + " LIKE ? AND duration > ?" , searchy, null);

            assert cursor != null;
            libraryCount = String.valueOf(cursor.getCount());
            cursor.close();
            //textViewLibraryCount.setText("Soungs found in library: " + String.valueOf(cursor.getCount()) );
        }
        catch( Exception eee){
            libraryCount = "error";
        }
        return libraryCount;
    }

}
