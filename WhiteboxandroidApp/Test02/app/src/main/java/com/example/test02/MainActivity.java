package com.example.test02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String uri = "rtsp://192.168.43.186:8555/test";
        VideoView v = (VideoView) findViewById( R.id.videoView );
        v.setVideoURI( Uri.parse(uri) );
        //v.setMediaController( new MediaController( this ) );
        v.requestFocus();
        v.start();
    }
}
