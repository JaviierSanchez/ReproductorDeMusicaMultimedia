package com.example.reproductordemusica;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;
    int pause;
    SeekBar selector;
    Runnable runnable;
    Handler handler;
    CheckBox bucle;
    TextView nombreCancion;
    TextView duracion;
    private int[] canciones = {R.raw.tunometescabra, R.raw.artificialnoise, R.raw.ultimosuspiro,R.raw.violinistaentutejado,R.raw.comocamaron,R.raw.video3};
    private int indiceCancionActual = 0;

    VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bucle = findViewById(R.id.checkBoxBucle);
        duracion = findViewById(R.id.tvTiempo);
        nombreCancion = findViewById(R.id.nombreCancion);
        selector = findViewById(R.id.seekBar);
        handler = new Handler();
        video = findViewById(R.id.video);

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

    public void bucleCancion(View view) {

        if (bucle.isChecked()) {
            if (mp != null) {
                mp.setLooping(true);
                Toast.makeText(this,"Bucle activado", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mp != null) {
                mp.setLooping(false);
                Toast.makeText(this,"Bucle desactivado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void play(View view) {
        if (mp == null) {
            mp = MediaPlayer.create(this, canciones[indiceCancionActual]);
            if (mp != null) {
                mp.start();

                // Obtener el nombre de la canción actual
                String nombreCancion = obtenerNombreCancion(indiceCancionActual);

                // Asignar el nombre de la canción al TextView
                asignarNombreCancion(view);

                selector.setMax(mp.getDuration());
                actualizarSeekBar();
                if(nombreCancion.equals("Video")){
                    verVideo();
                }else{

                    video.setActivated(false);
                }


            } else {
                Toast.makeText(this, "Error al crear el reproductor", Toast.LENGTH_SHORT).show();
            }
        } else if (!mp.isPlaying()) {
            mp.seekTo(pause);
            mp.start();

            // Obtener el nombre de la canción actual
            String nombreCancion2 = obtenerNombreCancion(indiceCancionActual);

            // Asignar el nombre de la canción al TextView
            asignarNombreCancion(view);

            // Mostrar el Toast con el nombre de la canción
            Toast.makeText(this, "Reanudando: " + nombreCancion2, Toast.LENGTH_LONG).show();
        }
    }



    private void asignarNombreCancion(View view){

        String nombre = obtenerNombreCancion(indiceCancionActual);

        nombreCancion.setText(nombre);
    }

    private String obtenerNombreCancion(int indice) {

        String[] nombresCanciones = {"Tu no metes cabra", "Artificial Noise", "El ultimo suspiro","Un Violinista En Tu Tejado","Como Camaron","Video"};

        // Asegúrate de que el índice esté dentro de los límites del array
        indice = Math.max(0, Math.min(indice, nombresCanciones.length - 1));

        return nombresCanciones[indice];
    }
    private void verVideo() {
        // Establecer la ruta del video utilizando el recurso raw
        String path = "android.resource://" + getPackageName() + "/" + R.raw.video3;
        video.setVideoPath(path);

        // Iniciar la reproducción del video
        video.start();

        // Configurar un Listener para detectar el final del video
        video.setOnCompletionListener(mp -> {

            cambiarCancion();
            play(findViewById(android.R.id.content));
        });
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

    public void anteriorCancion(View view) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
            selector.setProgress(0);
            bucle.setChecked(false);

            // Cambia al índice de la canción anterior
            indiceCancionActual = (indiceCancionActual - 1 + canciones.length) % canciones.length;

            // Reproduce la nueva canción
            play(view);
        }
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