package com.example.notasdevoz;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.notasdevoz.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder recorder;
    private File archivo;
    private TextView txtEstado;
    private MediaPlayer player;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        txtEstado = (TextView) this.findViewById(R.id.txtEstado);
        ImageView imgView = (ImageView)findViewById(R.id.imageView);
        ImageView imgPlay = (ImageView)findViewById(R.id.imgPlay);
        ImageView imgPause = (ImageView)findViewById(R.id.imgPause);
        ImageView imgRecord = (ImageView)findViewById(R.id.imgRecord);

        imgPlay.setOnClickListener(v -> {
            imgView.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            if (recorder == null && player == null) {
                player = new MediaPlayer();
                reproducir();
            }
        });

        imgPause.setOnClickListener(v -> {
            imgView.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_baseline_pause_24));
            if (recorder != null) {
                detener();
            } else if(player != null) {
                player.stop();
                player.release();
                player = null;
            }
        });

        imgRecord.setOnClickListener(v -> {
            imgView.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_baseline_record_voice_over_24));
                    if (recorder == null) {
                try {
                    grabar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void grabar() throws IOException {
        txtEstado.setText("Grabando");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File rutaAudio = new File(Environment.getExternalStorageDirectory().getPath());
        try {
            archivo = File.createTempFile("temporal", ".3gp", rutaAudio);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            recorder.setOutputFile(archivo);
        }
        recorder.prepare();
        recorder.start();
    }

    public void detener() {
        txtEstado.setText("Pausa");
        recorder.stop();
        recorder.reset();
        recorder.release();
        player = new MediaPlayer();
        player.setOnCompletionListener((MediaPlayer.OnCompletionListener) this);
        try {
            player.setDataSource(archivo.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reproducir() {
        player.start();
        txtEstado.setText("Reproduciendo");
    }
}