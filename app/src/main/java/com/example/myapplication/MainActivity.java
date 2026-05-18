package com.example.myapplication;

/*
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;

import com.example.myapplication.R;

// This class was causing a duplicate class error with MainActivity.kt.
// The layout activity_main.xml has been updated to use a NavHostFragment,
// which is handled by the Kotlin version of MainActivity.

public class MainActivity extends AppCompatActivity {

    TextView timerText;
    Button startButton;

    CountDownTimer timer;
    Button continueButton;
    MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.Timer);
        startButton = findViewById(R.id.startbtn);
        continueButton = findViewById(R.id.Continuebtn);
        mediaPlayer = MediaPlayer.create(this,
                R.raw.alarm);
        if (mediaPlayer == null) {
            timerText.setText("Sound not loaded");
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         stopTimer();
         startTimer();

          startButton.setText("RESTART!");
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerText.getText().toString()=="Done!"){
               Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cairoreg.aast.edu/aastreg/"));
                startActivity(intent);}
            }
        });


    }

    private void startTimer() {

        timer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) millisUntilFinished / 1000;
                timerText.setText("00:" + seconds);
                if (seconds == 3) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }

            }

            public void onFinish() {
                timerText.setText("Done!");
            }

        }.start();

    }
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }


    }
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
*/
