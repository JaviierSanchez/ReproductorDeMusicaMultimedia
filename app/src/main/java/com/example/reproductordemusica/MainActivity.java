package com.example.reproductordemusica;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;
    int pause;
    SeekBar selector;
    Runnable runnable;
    Handler handler;
    CheckBox bucle;

    TextView duracion;


    private int[] canciones = {R.raw.musica,R.raw.musica2,R.raw.musica3};
    private int indiceCancionActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bucle = findViewById(R.id.checkBoxBucle);
        duracion = findViewById(R.id.tvTiempo);
        selector = findViewById(R.id.seekBar);
        handler = new Handler();
        selector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mp != null) {
                        mp.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private String obtenerDuracionFormateada(int millis) {
        int segundos = millis / 1000;
        int minutos = segundos / 60;
        segundos = segundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    public void bucleCancion(View view){

        if (bucle.isChecked()) {
            if (mp != null) {
                mp.setLooping(true);
            }
        } else {
            if (mp != null) {
                mp.setLooping(false);
            }
        }
    }




    public void play(View view) {
        if (mp == null) {
            mp = MediaPlayer.create(this, canciones[indiceCancionActual]);
            if (mp != null) {
                mp.start();
                Toast.makeText(this, "Reproduciendo", Toast.LENGTH_SHORT).show();
                selector.setMax(mp.getDuration());
                actualizarSeekBar();
            } else {
                Toast.makeText(this, "Error al crear el reproductor", Toast.LENGTH_SHORT).show();
            }
        } else if (!mp.isPlaying()) {
            mp.seekTo(pause);
            mp.start();
            Toast.makeText(this, "Reanudando", Toast.LENGTH_SHORT).show();
        }
    }

    private void cambiarCancion() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            selector.setProgress(0);
            bucle.setChecked(false);

            // Cambia al siguiente índice de canción
            indiceCancionActual = (indiceCancionActual + 1) % canciones.length;
        }
    }
    public void siguienteCancion(View view) {
        cambiarCancion();
        play(view);
    }




    public void pause(View view) {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            pause = mp.getCurrentPosition();
            Toast.makeText(this, "Pausa", Toast.LENGTH_SHORT).show();
        }
    }

    public void stop(View view) {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
            selector.setProgress(0);
            bucle.setChecked(false); // Desactivar el bucle al detener la reproducción
            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
        }
    }

    public void actualizarSeekBar() {
        if (mp != null) {
            int currPos = mp.getCurrentPosition();
            selector.setProgress(currPos);

            // Obtén la duración total de la canción en milisegundos
            int duracionTotal = mp.getDuration();

            // Actualiza el TextView con el formato "Duración: mm:ss"
            String duracionFormateada = obtenerDuracionFormateada(currPos) + "/" + obtenerDuracionFormateada(duracionTotal);
            duracion.setText("Duración: " + duracionFormateada);

            runnable = () -> actualizarSeekBar();
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}