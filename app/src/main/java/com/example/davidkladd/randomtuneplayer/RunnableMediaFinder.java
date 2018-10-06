package com.example.davidkladd.randomtuneplayer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class RunnableMediaFinder implements Runnable{
    String[] columns = {MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
    };
    String searchString = "";
    String TAG = "Dave";

    private static int titleColumnIndex;
    private static String title;

    private static int artistColumnIndex;
    private static String artist;

    private static int dataColumnIndex;
    private static String data;

    private static int albumColumnIndex;
    private static String album;

    private static int yearColumnIndex;
    private static String year;

    private static String duration;

    public void run() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Log.d(TAG, "run: " + uri.getPath());
        //String search = MediaStore.Audio.Media.TITLE + " LIKE ? ";
        //String[] searchy = {"%w%"};
        String[] searchy = {"%" + searchString + "%", "30000"};
        try {

            //Cursor cursor = myAppContext.getContentResolver().query(uri, columns, MediaStore.Audio.Media.TITLE + " LIKE ? ", searchy, null);
            Cursor cursor =   MyService.nContext.get().getContentResolver().
                    query(uri, columns, MediaStore.Audio.Media.TITLE + " LIKE ? AND duration > ?" , searchy, " RANDOM() LIMIT 1 ");

            assert cursor != null;
            //cursor.getCount()
            cursor.moveToPosition(0);

            titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            title = cursor.getString(titleColumnIndex);
            artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            artist = cursor.getString(artistColumnIndex);

            albumColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            album = cursor.getString(albumColumnIndex);
            dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            data = cursor.getString(dataColumnIndex);
            yearColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
            year = cursor.getString(yearColumnIndex);

            duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            final String outputDetail = title + " " + artist + " " + album + " " + year + "\n" + data + " -- " + duration + "\n" ;
            final Bundle songBundle = new Bundle();
            songBundle.putString("title", title);
            songBundle.putString("artist",artist);
            songBundle.putString("album", album);
            songBundle.putString("year", year);
            songBundle.putString("data", data);
            songBundle.putString("duration", duration);
            //songBundle.putString();

            Log.d(TAG, "found " + songBundle.toString() );

            MyService.mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, " posting to mHandler " );

                    MyService.songChoosen(songBundle);

                }
            });

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


