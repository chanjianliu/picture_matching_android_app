package iss.workshop.team9_andriodca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class ReadImages extends AsyncTask<String, Integer, Long> {
    private Context context;

    private ImageButton[] iButtons;
    private Bitmap[] bmp;
    private String[] urls;

    private final String[] filenames = {"1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "6.jpg", "7.jpg", "8.jpg", "9.jpg", "10.jpg",
            "11.jpg", "12.jpg", "13.jpg", "14.jpg", "15.jpg", "16.jpg", "17.jpg", "18.jpg", "19.jpg", "20.jpg"};

    private ProgressBar progress;
    private TextView txtProgress;

    public ReadImages(ImageButton[] iButtons,  ProgressBar progress, TextView txtProgress, Bitmap[] imageBM, Context context){
        this.iButtons = iButtons;
        this.progress = progress;
        this.txtProgress = txtProgress;
        this.bmp = imageBM;
        this.context = context;
    }

    @Override
    protected Long doInBackground(String... strings) {

        long result = 0;

        try {
            //Connect to the website
            String url=strings[0];

            Document document = Jsoup.connect(url).get();

            // Locate the src attribute
            //Element img = document.select("a.photo-grid-preview > img").first(); :lt(5)
            Elements imgs = document.select("img[src$=.jpg]");

            if (imgs.size() >= 20) {
                urls = new String[20];
                for (int i = 0; i < 20; i++) {
                    String imgSrc = imgs.get(i).attr("src");
                    urls[i] = imgSrc;

                    if(isCancelled())
                        break;
                }

                for (int i = 0; i < urls.length; i++) {
                    URL link = new URL(urls[i]);

                    bmp[i] = BitmapFactory.decodeStream(link.openConnection().getInputStream());
                    downloadToSave(bmp[i], filenames[i]);

                    publishProgress((i + 1)); //i+1 so that it starts from 1 instead of 0
                    if(isCancelled())
                        break;
                }

                result = 100;
            }

        } catch (IOException e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//        super.onProgressUpdate(progress);
        int sequence = progress[0];
        setProgressPercent(sequence);

        //sequence - 1 to get back the start of array element with 0 as start
        iButtons[sequence - 1].setImageBitmap(bmp[sequence - 1]);
    }

    @Override
    protected void onPostExecute(Long result) {
        super.onPostExecute(result);
        if (result == 100)
            Toast.makeText(context.getApplicationContext(), "Download Completed\nselect 6 images to begin!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context.getApplicationContext(), "Download Unsuccessful\nPlease try a new link", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled(){
        super.onCancelled();
        Toast.makeText(context.getApplicationContext(), "Download stopped, restarting", Toast.LENGTH_LONG).show();
    }

    //saving all 20 images
    public boolean downloadToSave(Bitmap bitmap, String filename){
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES); //primary external storage
        File file = new File(dir, filename);

        try{
            FileOutputStream out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            out.write(byteArray);
            out.close();

            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void setProgressPercent(Integer percent){
        progress.setProgress(percent);
        txtProgress.setText(percent+"/"+progress.getMax());
    }
}
