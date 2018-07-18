package com.spiderappdev.inductions18.hangman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class StartMenuActivity extends AppCompatActivity {

    ImageButton buttonStartGame;
    TextView textViewHighScore;

    int highScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_start);

        buttonStartGame = (ImageButton)findViewById(R.id.buttonStartGame);

        //
        //load high score
        highScore=0;
        SharedPreferences sharedPref = getPreferences(Context.MODE_MULTI_PROCESS);
        if((sharedPref.getInt(getString(R.string.saved_high_score),0))!=0){
            highScore = sharedPref.getInt(getString(R.string.saved_high_score), 0);
        }
        //high score loaded
        //

        textViewHighScore = (TextView) findViewById(R.id.textViewHighScore);
        textViewHighScore.setVisibility(View.INVISIBLE);
        setHighScore();

        buttonStartGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity();
            }
        });
        }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    public void setHighScore(){
        textViewHighScore.setText("High Score: " + String.valueOf(highScore));
    }
}
