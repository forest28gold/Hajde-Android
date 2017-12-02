package com.azizinetwork.hajde.newpost;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;

import java.io.File;

public class NewVoiceActivity extends AppCompatActivity {

    private final static String TAG = "NewVoiceActivity";
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    TextView txt_hold, txt_record_time, txt_maximum;
    ImageButton btn_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voice);

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_record = (ImageButton)findViewById(R.id.imageButton_record);
        txt_hold = (TextView)findViewById(R.id.textView_tap);
        txt_record_time = (TextView)findViewById(R.id.textView_start);
        txt_maximum = (TextView)findViewById(R.id.textView_max);

        onInitView();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                boolean isReleased = event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL;
                boolean isPressed = event.getAction() == MotionEvent.ACTION_DOWN;

                if (isReleased) {  // record end
                    Log.d(TAG, "Recording is end");

                    mRecorder.stop();
                    mRecorder.release();
                    customHandler.removeCallbacks(updateTimerThread);

                    String voice_file_str = "/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT;
                    Uri uri = Uri.parse(voice_file_str);
                    mPlayer = MediaPlayer.create(NewVoiceActivity.this, uri);

                    int sec = mPlayer.getDuration() / 1000;

                    if (sec >= 1) {
                        int REQUEST_CODE = 123;
                        Intent i = new Intent(NewVoiceActivity.this, PreviewRecordingActivity.class);
                        startActivityForResult(i, REQUEST_CODE);
                    } else {
                        onRemoveRecordFile("/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT);
                        onInitView();
                    }

                } else if (isPressed) {  // recording is start
                    Log.d(TAG, "Recording is start");
                    try {
                        int randomID = (int) (900000000 * Math.random()) + 100000000;
                        String prefixName = Integer.toString(randomID);
                        Global.RECORD_DATE = String.format("%s_%s", prefixName, (System.currentTimeMillis()/1000));

                        String uservoiceStoragePath = "/sdcard/hajde/";
                        File myDir = new File(uservoiceStoragePath);
                        if (!myDir.exists())
                            myDir.mkdirs();

                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setOutputFile("/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT);
                        mRecorder.prepare();
                        mRecorder.start();
                        btn_record.setImageDrawable(getResources().getDrawable(R.mipmap.recording));

                        txt_hold.setText(getString(R.string.recording));
                        txt_record_time.setText("0:00 / 0:15");
                        txt_maximum.setVisibility(View.INVISIBLE);

                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);

                    } catch (Exception e) {
//						Toast.makeText(this, "Error starting recorder.",Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onInitView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    public void onInitView() {
        txt_hold.setText(getString(R.string.tap_hold));
        txt_record_time.setText(getString(R.string.recording_button));
        txt_maximum.setVisibility(View.VISIBLE);
        btn_record.setImageDrawable(getResources().getDrawable(R.mipmap.record));

        mRecorder = new MediaRecorder();
        mPlayer = new MediaPlayer();
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            txt_record_time.setText("0:00 / 0:" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

            if (secs == 15) {
                mRecorder.stop();
                mRecorder.release();
                customHandler.removeCallbacks(updateTimerThread);
                Intent i = new Intent(NewVoiceActivity.this, PreviewRecordingActivity.class);
                startActivity(i);
            }
        }

    };

    public void onRemoveRecordFile(String filePath) {
        File file = new File (filePath);
        if (file.exists()) {
            file.delete();
            Log.e(TAG, "record file is deleted");
        }
    }
}
