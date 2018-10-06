package com.example.davidkladd.randomtuneplayer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID;

public class DavesTTS extends TextToSpeech {
    private final String TAG = "Dave";
    public Bundle songBundle;
    Handler mmHandler;

    public DavesTTS(Context context, OnInitListener listener, final Handler mmHandler) {
        super(context, listener);
        this.mmHandler = mmHandler;

        this.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "TSS startin");
            }

            @Override
            public void onDone(final String utteranceId) {
                Log.d(TAG, "TTS done");
                MyService.playMusic(songBundle);
            }

            @Override
            public void onError(String utteranceId) {
            }
        });
    }

    public void sayIt(String text, Bundle songBundle){
        this.songBundle = songBundle;
        HashMap<String, String> hashtts = new HashMap<>();
        hashtts.put(KEY_PARAM_UTTERANCE_ID, "1");
        MyService.mp.setVolume(0.2f, 0.2f);
        Log.d(TAG, "sayIt: " + text);
        //this.speak("             ." + text, TextToSpeech.QUEUE_FLUSH, hashtts);
        // Have to add spaces and period to allow delay after music dip
        this.setSpeechRate(1.1f);
        this.speak("             ." + text, TextToSpeech.QUEUE_ADD, hashtts);
    }
}

