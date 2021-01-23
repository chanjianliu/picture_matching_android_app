package iss.workshop.team9_andriodca;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

public class SoundPlayer {

    private SoundPool soundPool;
    private int clickSound;
    private int clickSound1;
    private int clickSound2;
    private int clickSound3;
    private int clapping;
    private int poor;

    public SoundPlayer(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //if phone uses Lollipop and above, use SoundPool.Builder
            AudioAttributes audioAttr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttr).setMaxStreams(2).build();

        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        clickSound = soundPool.load(context, R.raw.match, 1);
        clickSound1 = soundPool.load(context, R.raw.lose, 1);
        clickSound2 = soundPool.load(context, R.raw.complete, 2);
        clickSound3 = soundPool.load(context, R.raw.click1, 2);
        clapping = soundPool.load(context, R.raw.clapping, 2);
        poor = soundPool.load(context, R.raw.poorgrade, 2);
    }

    public  void match(){
        soundPool.play(clickSound,1.0f,1.0f,1,0,1.0f);
    }

    public void noMatch(){
        soundPool.play(clickSound1,1.0f,1.0f,1,0,1.0f);
    }

    public void complete(){
        soundPool.play(clickSound2,1.0f,1.0f,1,0,1.0f);
    }

    public void click(){
        soundPool.play(clickSound3,1.0f,1.0f,1,0,1.0f);
    }

    public void clapping(){
        soundPool.play(clapping, 1.0f,1.0f,1,0,1.0f);
    }

    public void poorGrade(){
        soundPool.play(poor, 1.0f,1.0f,1,0,1.0f);
    }
}
