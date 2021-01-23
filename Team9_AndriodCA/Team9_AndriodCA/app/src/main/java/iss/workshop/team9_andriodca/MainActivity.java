package iss.workshop.team9_andriodca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected ImageButton[] iButtons;
    protected int[] iButtonsId;
    protected Bitmap[] imageBM;
    private SoundPlayer sound;

    //attributes for selecting 6 images
    protected int count = 0;
    protected int[] selectedImages;

    //protected String[] urls = {"https://stocksnap.io/", "https://www.shutterstock.com/search/photostock"};
    protected ReadImages readImagesTask;
    EditText urlInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sound = new SoundPlayer(this);
        Button button = findViewById(R.id.fetch);

        if(button!=null)
            button.setOnClickListener(this);

        iButtons = new ImageButton[20]; //3 is the total number of photos taken down
        iButtonsId = new int[20];

        for(int i = 0; i< 20;i++) {
            int resourceId = getResources().getIdentifier(
                    "image" + (i + 1),
                    "id",
                    this.getPackageName()
            );
            iButtonsId[i] = resourceId;
            ImageButton image = findViewById(resourceId);
            image.setOnClickListener(this);
            iButtons[i] = image;
        }
    }

    @Override
    public void onClick(View v)
    {
        sound.click();
        urlInput = findViewById(R.id.url);
        urlInput.setHint(""); //removing the hint in the bar if there is hint shown
        //this is to hide the keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);

        //getting the view Id that invoke the click
        int id = v.getId();

        if(id == R.id.fetch) {

            String url = urlInput.getText().toString(); //retrieving the url input by the player

            if(!url.equals("")) {//make sure that url is not empty, and no download yet
                if(readImagesTask == null)
                    startDownloading(url);
                else if (readImagesTask.getStatus() == AsyncTask.Status.RUNNING) {
                    readImagesTask.cancel(true);
                    startDownloading(url);
                }
                else
                    startDownloading(url);
            }
            else {
                //show a toast if no url and fetch is pressed
                urlInput.setHintTextColor(getColor(R.color.lightRed));
                urlInput.setHint("Please insert a url to start downloading");
            }
        }
        else if(Arrays.stream(iButtonsId).anyMatch(x -> x == id)){

            if (readImagesTask != null) {//to make button useless if no download or is in progress
                if (readImagesTask.getStatus() != AsyncTask.Status.RUNNING)
                    selectImage(id);
            }
        }
    }

    //method to start downloading
    protected void startDownloading(String url){
        imageBM = new Bitmap[20];
        //retrieving the progress bar and text
        ProgressBar progressBar = findViewById(R.id.indeterminateBar);
        TextView progressText = findViewById(R.id.txtProgress);

        readImagesTask = new ReadImages(iButtons, progressBar, progressText, imageBM, this);

        if (urlIsValid(url))
            readImagesTask.execute(url);
        else {
            Toast.makeText(this, "Please enter a valid link", Toast.LENGTH_LONG).show();
        }
    }

    //check if user input is a valid URL
    protected boolean urlIsValid(String url){
        try{
            new URL(url).toURI();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    //method on selecting images
    protected void selectImage(int id){

        ImageButton image = findViewById(id);
        String idName = image.getResources().getResourceEntryName(id);
        int idNumber;
        sound.click();

        //getting the idNumber from the ImageButton id
        if (idName.length() == 6)
            idNumber = Character.getNumericValue(idName.charAt(5));
        else
            idNumber = Integer.parseInt(idName.substring(5));

        //instantiate the selectedImage int[], and set count to 0
        if(selectedImages == null) {
            selectedImages = new int[6]; //only select 6 images
            for(int i = 0; i < 6; i++) //set all element value to -1 first, to indicate no chosen images
            {
                selectedImages[i] = -1;
            }
        }

        //image selection
        if (image.getColorFilter() == null) {
            image.setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);

            for(int i = 0; i < 6; i++) { //to replace all -1 (which means no photo) in the array to the chosen photo
                if(selectedImages[i] == -1) { //cause player does not select/deselect in array order
                    selectedImages[i] = idNumber;
                    break;
                }
            }

            count++;
        } else { //remove selection
            image.setColorFilter(null);
            //run through to find the element that holds a particular idNumber
            for (int i = 0; i < 6; i++) {
                if (selectedImages[i] == idNumber) {
                    selectedImages[i] = -1;
                    count--;
                }
            }
        }
        if (count == 6){ //when user selected 6 images, Game immediately begins
            startGame();
        }
    }

    protected void startGame(){
        Intent startGame = new Intent(this, GamePlay.class);
        startGame.putExtra("selectedImages", selectedImages);
        startActivity(startGame);
    }
}