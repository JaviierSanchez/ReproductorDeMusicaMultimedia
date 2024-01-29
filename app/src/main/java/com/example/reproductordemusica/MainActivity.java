package com.example.reproductordemusica;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;
    int pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View view){
        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.musica3);
            mp.start();
            Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
        }else if (!mp.isPlaying()){
            mp.seekTo(pause);
            mp.start();
            Toast.makeText(this, "Renaudando", Toast.LENGTH_SHORT).show();
        }

    }
    public void pause(View view){

        if (mp != null){
            mp.pause();
            pause = mp.getCurrentPosition();
            Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
        }
    }
    public void stop(View view){
        mp.stop();
        mp = null;
        Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();

    }
}