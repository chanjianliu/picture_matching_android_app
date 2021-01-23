package iss.workshop.team9_andriodca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlay extends AppCompatActivity implements View.OnClickListener {

    protected int[] imageFileNo;
    protected List<Integer> shuffleList;
    private SoundPlayer sound;
    protected ImageButton[] iButtons;
    protected int[] iButtonsId;
    protected ImageButton previousButton;
    protected ImageButton currentButton;
    protected int previousButtonId;
    protected int currentButtonId;
    protected int lastClickedButtonId;
    protected Bitmap preImg;
    protected Bitmap curImg;

    int countMatched = 0;
    boolean needToFlipTwo = true;

    Map<Integer,Bitmap> positionAndBitmapMatcher = new HashMap<>();
    Bitmap[] allImages = new Bitmap[12];

    int clickedCount = 0; //to keep track that 2 images had been clicked, so don't allow 3rd click
    TextView matchResultView;
    Chronometer timer;
    boolean countDownDone = false;

    TextView mTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        sound=new SoundPlayer(this);
        matchResultView = findViewById(R.id.attempts);
        timer = findViewById(R.id.timer);

        //the 3-2-1 countdown to start game
        countDownTimer();

        //timer and gameplay starts only after the 3-2-1 countdown ends
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                countDownDone = true;
            }
        }, 4000);
    }

    //the 3-2-1 countdown to start game
    private void countDownTimer() {
        mTextField=findViewById(R.id.countdown);
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                if((millisUntilFinished)<1000){
                    mTextField.setText("START");
                }else{
                    mTextField.setText(""+(millisUntilFinished) / 1000);
                }
            }
            public void onFinish() {
                mTextField.setVisibility(View.GONE);
            }

        }.start();
    }

    @Override
    protected void onStart(){
        super.onStart();

        Intent getImages = getIntent();
        imageFileNo = getImages.getIntArrayExtra("selectedImages");

        iButtons = new ImageButton[12]; //3 is the total number of photos taken down
        iButtonsId = new int[12];

        for (int i = 0; i < 12; i++){
            int resourceId = getResources().getIdentifier("image"+(i+1), "id", this.getPackageName());
            ImageButton image = findViewById(resourceId);
            image.setOnClickListener(this);
            iButtons[i] = image;
            iButtonsId[i] = resourceId;
        }

        shuffle();
        setImages();

    }

    protected void shuffle() {
        if (shuffleList == null) {//make sure shuffleList is instantiated
            shuffleList = new ArrayList<Integer>();

            for (int i : imageFileNo){
                //add twice cause need 2 copy of each photo
                shuffleList.add(i);
                shuffleList.add(i);
            }
        }

        Collections.shuffle(shuffleList);
    }

    protected void setImages() {
        for (int i = 0; i < 12; i++){ //total only 6 images
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(dir, shuffleList.get(i) +".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            if(bitmap != null)
            {
                allImages[i] = bitmap; //putting the image into a bitmap array
                setTag(iButtons[i], allImages[i]);
            }
        }
    }

    //Game Logic from here onwards
    protected void setTag(ImageButton button, Bitmap img){ // should be replaced by randomDisplay();
        positionAndBitmapMatcher.put(button.getId(),img);
    }

    @Override
    public void onClick(View v) {

        currentButtonId = v.getId();

        //preventing user to click the 3rd photo if 2 photo is already clicked
        if(clickedCount < 2 && countDownDone == true) {
            sound.click();
            //1st time needToFlipTwo = true, once clicked it turns into false, meaning after
            needToFlipTwo = !needToFlipTwo;
            clickedCount++;
            chooseImage(currentButtonId);

            if (needToFlipTwo && previousButtonId == currentButtonId) { // click the same img twice
                clickedCount = 1; //clicked count is still 1 cause user clicked on same image
                needToFlipTwo = !needToFlipTwo;
                System.out.println("inside same image clicked twice");
            } else if (needToFlipTwo && previousButtonId != currentButtonId) { // e.g the second time user clicked a image,
                if (!same(preImg, curImg)) { // wrong guess
                    //here comparing the actual drawable tag
                    System.out.println("inside wrong match");
                    sound.noMatch();
                    //delay2seconds;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            previousButton.setImageResource(R.drawable.logo);
                            currentButton.setImageResource(R.drawable.logo);

                            //resetting clickedCount after 2 clicks, and resetting previousButtonId to 0 for a new set comparison
                            clickedCount = 0;
                            previousButtonId = 0;
                            System.out.println("inside wrong match refresh thread");
                        }
                    }, 1000);
                } else {
                    System.out.println("inside correct match");
                    countMatched++;
                    Toast.makeText(this, "It's a Match!!!", Toast.LENGTH_SHORT).show();
                    sound.match();
                    String score = "Score: " + countMatched + "/6";
                    matchResultView.setText(score);

                    //removing the onClickListener on imageButtons that are already matched
                    previousButton.setOnClickListener(null);
                    currentButton.setOnClickListener(null);

                    if (countMatched == 6) {
                        timer.stop();

                        long timeUsed = SystemClock.elapsedRealtime() - timer.getBase();
                        int milliToSec = (int) (timeUsed / 1000);

                        //sound effect for different result Grade
                        if (milliToSec <= 10)
                            sound.clapping();
                        else if (milliToSec > 10 && milliToSec <= 30)
                            sound.complete();
                        else
                            sound.poorGrade();

                        Intent result = new Intent(this, Result.class);
                        result.putExtra("selectedImages", imageFileNo);
                        result.putExtra("result", milliToSec);

                        startActivity(result);
                    }
                    //resetting clickedCount after 2 clicks, and resetting previousButtonId to 0 for a new set comparison
                    clickedCount = 0;
                    previousButtonId = 0;
                }
            }
        }
    }

    protected boolean same(Bitmap b1, Bitmap b2){
        return b1.sameAs(b2);
    }

    protected void chooseImage(int id){
        //setting the image to be compared to the next photo to be clicked
        if (clickedCount == 1) {
            lastClickedButtonId = id;
            System.out.println("inside lastClickedButton refresh\n" + lastClickedButtonId);
        }

        currentButton = findViewById(id);
        curImg = positionAndBitmapMatcher.get(id);
        currentButton.setImageBitmap(curImg);

        previousButtonId = lastClickedButtonId;
        previousButton = findViewById(previousButtonId);
        preImg = positionAndBitmapMatcher.get(previousButtonId);
    }

}