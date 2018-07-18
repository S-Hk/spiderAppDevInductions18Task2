package com.spiderappdev.inductions18.hangman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import android.os.Bundle;
import android.os.Handler;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

// #######Doubt######:: Logcat/Error::  : /? E/android.os.Debug: failed to load memtrack module: -2


public class MainActivity extends AppCompatActivity {
    //
    EditText editTextGuess;
    TextView wordView;
    TextView textViewHighScoreM, textViewCurrentScore, textViewWrongGuess;
    ListView letterList;
    Button checkGuess;
    ProgressBar mProgress;
    //
    ArrayList<Character> guessCharList;
    ArrayAdapter<Character> adapter;
    //
    String[] wordData = {"DINOSAUR", "MERCURY", "SUBMARINE", "EXTINCTION", "EXTERMINATE", "COSMOLOGY","URBANISATION", "PHOTOSYNTHESIS", "PNEUMONIA", "EXPLOSION", "DEMOLITION", "SUSPENDED", "STADIUM", "DESTINATION", "INTERNET", "REVERSE","RADIATION","PLASMA"};
    //
    Character letter;
    int wordSelNum, wordSize;
    String wordSelected, displayWord;
    char[]  displayArray = new char[30];
    char[] wordSelArray = new char[20];
    //
    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();
    //
    int counterGuess=0, counterWrongGuess=0, counterRightGuess=0;
    int currentScoreVal, highScoreVal;
    //
    static final String STATE_HIGH_SCORE = "highScore";
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        //load high score
        highScoreVal=0;
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_MULTI_PROCESS);
        if (savedInstanceState != null) {

            // Restore value of members from saved state
            highScoreVal = savedInstanceState.getInt(STATE_HIGH_SCORE);

        }
        else if((sharedPref.getInt(getString(R.string.saved_high_score),0))!=0){
            highScoreVal = sharedPref.getInt(getString(R.string.saved_high_score), 0);
        }
        //high score loaded
        //
        initialise();
        //
        progressBarSetFull();
        //
        selectWord();
        //
        checkGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String letterS=editTextGuess.getText().toString();
                if(letterS.length()!=0){
                    letter = letterS.charAt(0);
                }
                else {
                    letter='0';
                }
                //letter=editTextGuess.getText().toString().charAt(0);


                if (Character.isUpperCase(letter)) {
                    //
                    checkGuessLetter();
                        //counterGuess++;
                    //
                }
                else {
                    letter=Character.toUpperCase(letter);

                    if (Character.isUpperCase(letter)) {
                        checkGuessLetter();
                            //counterGuess++;
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Invalid Guess", Toast.LENGTH_SHORT).show();
                    }

                    }
                editTextGuess.setText("");

            }
        });

        /*
    editTextGuess.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            InputMethodManager keyboard =(InputMethodManager) v.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus) {
                keyboard.showSoftInput(editTextGuess, 0);
            } else {
                keyboard.hideSoftInputFromWindow(editTextGuess.getWindowToken(), 0);
            }
        }
    });

*/

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the user's current game state
        savedInstanceState.putInt(STATE_HIGH_SCORE, highScoreVal);


        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onDestroy() {

        saveHighScore();

        super.onDestroy();
    }


    public void initialise() {
        //
        editTextGuess=(EditText)findViewById(R.id.editTextGuess);
        wordView=(TextView)findViewById(R.id.wordView);
        letterList=(ListView)findViewById(R.id.letterList);
        checkGuess=(Button)findViewById(R.id.checkGuess);
        //
        textViewCurrentScore=(TextView)findViewById(R.id.textViewCurrentScore);
        textViewHighScoreM=(TextView)findViewById(R.id.textViewHighScoreM);
        textViewWrongGuess=(TextView)findViewById(R.id.textViewWrongGuess);
        //
        guessCharList= new ArrayList<Character>();
        adapter=new ArrayAdapter<Character>(MainActivity.this, R.layout.custom_list_item, guessCharList);
        //
        letterList.setAdapter(adapter);
        //
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        //
        displayHighScore();
        //


    }

    public void selectWord() {
        wordSelNum=new Random().nextInt(18);
        Log.e("debug1", "wordSelNum: "+String.valueOf(wordSelNum));
        wordSelected=wordData[wordSelNum];
        //wordSize=wordSelected.length();
        wordSelArray=wordSelected.toCharArray();
        wordSize=wordSelArray.length;
        displayWord="";
        Log.e("debug1", "wordSize: " + String.valueOf(wordSize));
        for (int i=0; i<wordSize*2; i++) {
            //Log.e("debug1", "i: "+String.valueOf(i));
            if (i % 2 == 0) {
                displayArray[i] = '_';
            } else {
                displayArray[i] = ' ';
            }


            //displayWord=displayWord.concat("_ ");

        }

            displayWord=displayArray.toString();
            displayWord=String.copyValueOf(displayArray);

        wordView.setText(displayWord);


    }

    public void checkGuessLetter() {
       boolean foundGuess=false;
       boolean newLetter=false;

        for(int i=0; i<wordSize; i++){
            if (wordSelArray[i] == letter) {
                foundGuess=true;
                if (displayArray[i * 2] != letter) {
                    newLetter=true;
                    displayArray[i*2]=letter;
                }
            }

        }


        counterGuess++;
        if (foundGuess&&newLetter) {
            //
            letterGuessed();
            counterRightGuess++;
            currentScoreVal+=4*(28-counterGuess);

            textViewCurrentScore.setText("Current Score: " + String.valueOf(currentScoreVal));
            //return true;

        } else if (foundGuess) {
            Toast.makeText(MainActivity.this, "Already Guessed", Toast.LENGTH_SHORT).show();

        }
        else {
            //
            counterWrongGuess++;
            progressBarReduce(counterWrongGuess*10);
            currentScoreVal-=5*counterWrongGuess;

            textViewWrongGuess.setText("Wrong Guess: " + String.valueOf(counterWrongGuess) + "/10");

            if (counterRightGuess != 0) {
                textViewCurrentScore.setText("Current Score: " + String.valueOf(currentScoreVal));
            } else {
                textViewCurrentScore.setText("Current Score: " + String.valueOf(0));
            }

            guessCharList.add(letter);
            adapter.notifyDataSetChanged();

            //return false;
        }

        //currentScoreVal=10-counterWrongGuess;



        if (counterWrongGuess > 10) {
            Toast.makeText(MainActivity.this, "You have no more guesses left", Toast.LENGTH_SHORT).show();
            textViewWrongGuess.setText("You Loose!!!");

            editTextGuess.clearFocus();
            editTextGuess.setVisibility(View.INVISIBLE);
            checkGuess.setEnabled(false);
            checkGuess.setVisibility(View.INVISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    gameOver();
                }
            }, 3000);

        }
    }

    public void letterGuessed() {
        //displayWord=displayArray.toString();
        displayWord=String.copyValueOf(displayArray);
        wordView.setText(displayWord);

        boolean gameover=true;

        for(int i=0; i<wordSize; i++){
            if (displayArray[i * 2] == '_') {
                gameover=false;
            }
        }

        /*
        if (counterWrongGuess > 10) {
            gameover=true;
            //textViewWrongGuess.setText("You Loose!!!");
        }
        */

        if (gameover) {
            Toast.makeText(MainActivity.this, "Congrats. You found the word.", Toast.LENGTH_SHORT).show();
            textViewWrongGuess.setText("You Win!!!");
            editTextGuess.clearFocus();
            editTextGuess.setVisibility(View.INVISIBLE);
            checkGuess.setEnabled(false);
            checkGuess.setVisibility(View.INVISIBLE);

            checkForHighScore();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    gameOver();
                }
            }, 2500);

        }

    }

    public void gameOver() {
        Intent intent = new Intent(this, StartMenuActivity.class);
        startActivity(intent);

        if(currentScoreVal>highScoreVal)
        {
            highScoreVal=currentScoreVal;
            saveHighScore();
        }

        finish();
    }

    public void progressBarSetFull() {
        Resources res = getResources();
        //final TextView tv;
        Drawable drawable = res.getDrawable(R.drawable.custom_progressbar_drawable);

        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/

        //tv = (TextView) findViewById(R.id.txtProgress);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus < 100) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            //tv.setText(pStatus + "%");

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void progressBarReduce(final int amount) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus > 100-amount) {
                    pStatus -= 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            //tv.setText(pStatus + "%");
                        }
                    });
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void onBackPressed() {
        gameOver();
    }

    public void checkForHighScore() {
        if (highScoreVal < currentScoreVal) {
            highScoreVal=currentScoreVal;
            Toast.makeText(MainActivity.this, "New High Score Attained", Toast.LENGTH_SHORT).show();
            saveHighScore();
        }
    }

    public void saveHighScore() {
        //Save High Score
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor saver = sharedPref.edit();

        saver.putInt(getString(R.string.saved_high_score), highScoreVal);
        saver.apply();

    }


    public void displayHighScore() {
        //Display saved High Score
        textViewHighScoreM.setText("High Score: " + String.valueOf(highScoreVal));
    }
}
