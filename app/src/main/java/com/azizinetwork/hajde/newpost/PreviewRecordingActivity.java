package com.azizinetwork.hajde.newpost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.backend.Post;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.azizinetwork.hajde.settings.Utility;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.wevey.selector.dialog.NormalAlertDialog;

import java.io.File;
import java.io.IOException;

public class PreviewRecordingActivity extends AppCompatActivity {

    private final static String TAG = "PreviewRecordingActivity";
    private MediaPlayer mPlayer = new MediaPlayer();
    private SVProgressHUD mSVProgressHUD;
    private NormalAlertDialog dialog;
    private String strBackColor;
    ImageButton btn_color1, btn_color2, btn_color3, btn_color4, btn_color5, btn_color6;
    ImageButton btn_color7, btn_color8, btn_color9, btn_color10, btn_color11;
    ImageButton btn_play;
    TextView txt_time;
    RelativeLayout relativeLayout_post;

    private static int color_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_recording);

        mSVProgressHUD = new SVProgressHUD(this);

        strBackColor = Global.COLOR1;
        color_count = 0;

        Button btn_back = (Button)findViewById(R.id.button_back);
        Button btn_post = (Button)findViewById(R.id.button_post);

        btn_color1 = (ImageButton)findViewById(R.id.button_color1);
        btn_color2 = (ImageButton)findViewById(R.id.button_color2);
        btn_color3 = (ImageButton)findViewById(R.id.button_color3);
        btn_color4 = (ImageButton)findViewById(R.id.button_color4);
        btn_color5 = (ImageButton)findViewById(R.id.button_color5);
        btn_color6 = (ImageButton)findViewById(R.id.button_color6);
        btn_color7 = (ImageButton)findViewById(R.id.button_color7);
        btn_color8 = (ImageButton)findViewById(R.id.button_color8);
        btn_color9 = (ImageButton)findViewById(R.id.button_color9);
        btn_color10 = (ImageButton)findViewById(R.id.button_color10);
        btn_color11 = (ImageButton)findViewById(R.id.button_color11);

        String voice_file_str = "/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT;
        Uri uri = Uri.parse(voice_file_str);
        mPlayer = MediaPlayer.create(PreviewRecordingActivity.this, uri);

        txt_time = (TextView)findViewById(R.id.textView_time);
        txt_time.setText(formatAsTime(mPlayer.getDuration()));

        btn_play = (ImageButton)findViewById(R.id.imageButton_play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Handler voicetimerHandler = new Handler();
                final Runnable voicetimerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        txt_time.setText(formatAsTime(mPlayer.getCurrentPosition()));
                        voicetimerHandler.postDelayed(this, 1000);
                    }
                };

                voicetimerHandler.postDelayed(voicetimerRunnable, 0);

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    btn_play.setImageResource(R.mipmap.voice_play);
                } else {
                    mPlayer.start();
                    btn_play.setImageResource(R.mipmap.voice_pause);
                }

                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mplayer) {
                        if (mPlayer == null)
                            return;
//							mPlayer.release();
                        try {
                            mPlayer.prepare();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block

                        } catch (IOException e) {
                            // TODO Auto-generated catch block

                        }
                        txt_time.setText(formatAsTime(mPlayer.getDuration()));
                        voicetimerHandler.removeCallbacks(voicetimerRunnable);
                        btn_play.setImageResource(R.mipmap.voice_play);
                    }
                });


            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRemoveRecordFile("/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT);
                finish();
            }
        });

        relativeLayout_post = (RelativeLayout)findViewById(R.id.relativeLayout_post);
        relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color1));

        relativeLayout_post.setOnTouchListener(new View.OnTouchListener() {
            private int min_distance = 60;
            private float downX, downY, upX, upY;
            View v;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                this.v = view;
                switch(event.getAction()) { // Check vertical and horizontal touches
                    case MotionEvent.ACTION_DOWN: {
                        downX = event.getX();
                        downY = event.getY();
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        upX = event.getX();
                        upY = event.getY();

                        float deltaX = downX - upX;
                        float deltaY = downY - upY;

                        //HORIZONTAL SCROLL
                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            if (Math.abs(deltaX) > min_distance) {
                                // left or right
                                if (deltaX > 0) {  // RightToLeftSwipe
                                    if (color_count == 10){
                                        color_count = 0;
                                    } else {
                                        color_count++;
                                    }
                                    onSetPostBackColor(color_count);
                                    return true;
                                }
                                if (deltaX < 0) {  // LeftToRightSwipe
                                    if (color_count == 0) {
                                        color_count = 10;
                                    } else {
                                        color_count--;
                                    }
                                    onSetPostBackColor(color_count);
                                    return true;
                                }
                            } else {
                                //not long enough swipe...
                                return false;
                            }
                        }
                        //VERTICAL SCROLL
                        else
                        {
                            if(Math.abs(deltaY) > min_distance){
                                // top or down
                                if(deltaY < 0) {
//                                    this.onTopToBottomSwipe();
                                    return true;
                                }
                                if(deltaY > 0) {
//                                    this.onBottomToTopSwipe();
                                    return true;
                                }
                            }
                            else {
                                //not long enough swipe...
                                return false;
                            }
                        }

                        return false;
                    }
                }
                return false;
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                File file = new File ("/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT);

                Backendless.Files.upload(file, Global.BACKEND_URL_AUDIO, new AsyncCallback<BackendlessFile>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void handleResponse(BackendlessFile response) {

                        Log.d(TAG, String.format("audio file -> %s", response.getFileURL().toString()));

                        Post post = new Post();
                        post.setType(Global.POST_TYPE_AUDIO);
                        post.setFilePath(response.getFileURL().toString());
                        post.setUserID(Global.g_userInfo.getUserID());
                        post.setBackColor(strBackColor);
                        post.setLatitude(Float.toString(Global.g_userInfo.getLatitude()));
                        post.setLongitude(Float.toString(Global.g_userInfo.getLongitude()));
                        post.setTime(GlobalSharedData.getCurrentDate());
                        post.setCommentCount(0);
                        post.setLikeCount(0);
                        post.setReportCount(0);
                        post.setCommentType("");
                        post.setLikeType("");
                        post.setReportType("");
                        post.setPeriod(formatAsTime(mPlayer.getDuration()));

                        Backendless.Persistence.save(post, new AsyncCallback<Post>() {
                            @Override
                            public void handleResponse(Post response) {
                                Log.d(TAG, "Saved new audio post data");

                                onRemoveRecordFile("/sdcard/hajde/" + Global.RECORD_DATE + Global.RECORD_FILE_FORMAT);

                                mSVProgressHUD.dismiss();

                                int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_POST;
                                Global.g_userInfo.setKarmaScore(karmaScore);
                                int postCount = Global.g_userInfo.getPostCount() + 1;
                                Global.g_userInfo.setPostCount(postCount);

                                GlobalSharedData.updateUserDBData();
                                Utility.karmaInBackground(Global.KARMA_POST);

                                setResultOkSoSecondActivityWontBeShown();
                                finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.d(TAG, String.format("Failed save in background of audio post data = %s", fault.toString()));
                                mSVProgressHUD.dismiss();
                                dialog = new NormalAlertDialog.Builder(PreviewRecordingActivity.this)
                                        .setHeight(0.2f)
                                        .setWidth(0.8f)
                                        .setTitleVisible(true)
                                        .setTitleText(getString(R.string.alert))
                                        .setTitleTextColor(R.color.black)
                                        .setContentText(getString(R.string.alert_new_posting_failed))
                                        .setContentTextColor(R.color.dark)
                                        .setSingleMode(true)
                                        .setSingleButtonText(getString(R.string.ok))
                                        .setSingleButtonTextColor(R.color.blue)
                                        .setCanceledOnTouchOutside(false)
                                        .setSingleListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .build();

                                dialog.show();
                            }
                        });

                    }

                    @SuppressLint("LongLogTag")
                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d(TAG, String.format("Failed save in background of audio post data = %s", fault.toString()));
                        mSVProgressHUD.dismiss();
                        dialog = new NormalAlertDialog.Builder(PreviewRecordingActivity.this)
                                .setHeight(0.2f)
                                .setWidth(0.8f)
                                .setTitleVisible(true)
                                .setTitleText(getString(R.string.alert))
                                .setTitleTextColor(R.color.black)
                                .setContentText(getString(R.string.alert_new_posting_failed))
                                .setContentTextColor(R.color.dark)
                                .setSingleMode(true)
                                .setSingleButtonText(getString(R.string.ok))
                                .setSingleButtonTextColor(R.color.blue)
                                .setCanceledOnTouchOutside(false)
                                .setSingleListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                })
                                .build();

                        dialog.show();
                    }
                });

            }
        });

        btn_color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(0);
            }
        });

        btn_color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(1);
            }
        });

        btn_color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(2);
            }
        });

        btn_color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(3);
            }
        });

        btn_color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(4);
            }
        });

        btn_color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(5);
            }
        });

        btn_color7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(6);
            }
        });

        btn_color8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(7);
            }
        });

        btn_color9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(8);
            }
        });

        btn_color10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(9);
            }
        });

        btn_color11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetPostBackColor(10);
            }
        });
    }

    private void setResultOkSoSecondActivityWontBeShown() {
        Intent intent = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    public String formatAsTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        return String.format("%01d:%02d", seconds / 60, seconds % 60);
    }

    @SuppressLint("LongLogTag")
    public void onRemoveRecordFile(String filePath) {
        File file = new File (filePath);
        if (file.exists()) {
            file.delete();
            Log.e(TAG, "record file is deleted");
        }
    }

    public void setColorButton(ImageButton btn_color, boolean is_selected) {

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btn_color.getLayoutParams();

        if (is_selected) {
            layoutParams.height = (int) getResources().getDimension(R.dimen.post_back_color_selected);
            layoutParams.width = (int) getResources().getDimension(R.dimen.post_back_color_selected);
        } else {
            layoutParams.height = (int) getResources().getDimension(R.dimen.post_back_color);
            layoutParams.width = (int) getResources().getDimension(R.dimen.post_back_color);
        }

        btn_color.setLayoutParams(layoutParams);
    }

    public void onSetPostBackColor(int count) {

        switch (count) {
            case 0:
                setColorButton(btn_color1, true);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color1));
                strBackColor = Global.COLOR1;
                color_count = 0;
                break;
            case 1:
                setColorButton(btn_color1, false);setColorButton(btn_color2, true);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color2));
                strBackColor = Global.COLOR2;
                color_count = 1;
                break;
            case 2:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, true);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color3));
                strBackColor = Global.COLOR3;
                color_count = 2;
                break;
            case 3:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, true);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color4));
                strBackColor = Global.COLOR4;
                color_count = 3;
                break;
            case 4:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, true);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color5));
                strBackColor = Global.COLOR5;
                color_count = 4;
                break;
            case 5:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, true);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color6));
                strBackColor = Global.COLOR6;
                color_count = 5;
                break;
            case 6:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, true);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color7));
                strBackColor = Global.COLOR7;
                color_count = 6;
                break;
            case 7:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, true);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color8));
                strBackColor = Global.COLOR8;
                color_count = 7;
                break;
            case 8:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, true);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color9));
                strBackColor = Global.COLOR9;
                color_count = 8;
                break;
            case 9:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, true);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color10));
                strBackColor = Global.COLOR10;
                color_count = 9;
                break;
            case 10:
                setColorButton(btn_color1, false);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, true);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color11));
                strBackColor = Global.COLOR11;
                color_count = 10;
                break;
            default:
                setColorButton(btn_color1, true);setColorButton(btn_color2, false);setColorButton(btn_color3, false);
                setColorButton(btn_color4, false);setColorButton(btn_color5, false);setColorButton(btn_color6, false);
                setColorButton(btn_color7, false);setColorButton(btn_color8, false);setColorButton(btn_color9, false);
                setColorButton(btn_color10, false);setColorButton(btn_color11, false);
                relativeLayout_post.setBackgroundColor(getResources().getColor(R.color.post_color1));
                strBackColor = Global.COLOR1;
                color_count = 0;
                break;
        }
    }
}
