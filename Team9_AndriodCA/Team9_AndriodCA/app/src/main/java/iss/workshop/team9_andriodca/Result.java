package iss.workshop.team9_andriodca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Result extends AppCompatActivity implements View.OnClickListener{

    protected int[] selectedImages;

    protected int result;
    protected int besttime;

    protected TextView bestTimeLabel;
    protected TextView gradeLevel;
    protected TextView timeLabel;
    protected TextView congrats1;
    protected TextView congrats2;

    protected SoundPlayer sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        sound = new SoundPlayer(this);

        //getting intent
        Intent intent = getIntent();
        selectedImages = intent.getIntArrayExtra("selectedImages"); //keep track of selected images in case user wants to play again
        result = intent.getIntExtra("result", 0);

        timeLabel = (TextView) findViewById(R.id.time);
        bestTimeLabel = (TextView) findViewById(R.id.bestTime);
        gradeLevel = (TextView) findViewById(R.id.grade);
        congrats1 = (TextView) findViewById(R.id.congratsLine1);
        congrats2 = (TextView) findViewById(R.id.congratsLine2);

        Button playAgain = findViewById(R.id.playAgain);
        playAgain.setOnClickListener(this);

        Button mainMenu = findViewById(R.id.mainMenu);
        mainMenu.setOnClickListener(this);

        publishResult();
    }

    public void publishResult(){
        timeLabel.setText("Time: " + result + " seconds");
        resultCheck(result);
        sound.clapping();
    }

    @Override
    public void onClick(View view){
        sound.click();

        int id = view.getId();

        if(id == R.id.playAgain)
            playAgain();
        else if (id == R.id.mainMenu)
            backToMain();
    }

    public void playAgain(){
        Intent newGame = new Intent(this, GamePlay.class);
        newGame.putExtra("selectedImages", selectedImages);
        startActivity(newGame);
    }

    public void backToMain(){
        Intent mainPage = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainPage);
    }

    public void resultCheck(int time){
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        besttime = settings.getInt("BEST_TIME", 50);

        if (besttime == 0){ //if besttime is 0, means this is the first time user is playing
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("BEST_TIME", Integer.MAX_VALUE); //giving it the Integer.MAX_VALUE, highest possible result
            besttime = Integer.MAX_VALUE;
        }

        if (time < besttime) {
            bestTimeLabel.setText("Best Time: " + time +" seconds");
            //saved the new best time into sharedPreference
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("BEST_TIME", time);
            editor.commit();
        } else {
            bestTimeLabel.setText("Best Time: " + besttime + " seconds");
        }

        if (time <= 10) {
            congrats1.setText("Congratulations!");
            congrats2.setText("You're Unbelievable!");
            gradeLevel.setText("Grade: Excellent");
        } else if (time > 10 && time <= 30) {
            congrats1.setText("Well done!");
            congrats2.setText("still, could be better!");
            gradeLevel.setText("Grade: Good");
        } else {
            congrats1.setText("Hmm...");
            congrats2.setText("Try again, you can do it.");
            gradeLevel.setText("Grade: Poor");
        }
    }
}