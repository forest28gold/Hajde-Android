package com.azizinetwork.hajde.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.backend.ActivityPost;
import com.azizinetwork.hajde.model.backend.Post;
import com.azizinetwork.hajde.model.parse.ActivityPostData;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.azizinetwork.hajde.settings.Utility;
import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.backendless.services.messaging.MessageStatus;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.qbw.customview.RefreshLoadMoreLayout;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiTextView;
import com.wang.avi.AVLoadingIndicatorView;
import com.wevey.selector.dialog.NormalAlertDialog;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity implements RefreshLoadMoreLayout.CallBack {

    private final static String TAG = "PostDetailsActivity";
    private SVProgressHUD mSVProgressHUD;
    private NormalAlertDialog dialog;
    protected RefreshLoadMoreLayout mRefreshloadmore;
    private Handler handler = new Handler();

    private EmojiEditText edit_comment;
    private RelativeLayout relativeLayout_post, relativeLayout_comment;
    private RelativeLayout relativeLayout_report, btn_abuse;
    private ImageView img_post, img_overlay, img_wave;
    private AVLoadingIndicatorView avi_indicator;
    private TextView txt_content, txt_period, txt_time, txt_distance, txt_comment_count, txt_like_count;
    private ImageButton btn_play, btn_comment, btn_like, btn_dislike, btn_report;
    protected ListView mListview;
    protected Adapter mAdapter;
    private MediaPlayer mPlayer;
    public ProgressBar prog_loading;
    public CircleProgressBar prog_circle;

    int dataCount = 0;
    String post_data = "";
//    int cell_height = 0;
//    String keyboard = "Keyboard_comment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

//        Intent intent = getIntent();
//        keyboard = intent.getStringExtra("comment");
//
//        if (keyboard.equals("Keyboard")) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            keyboard = "Keyboard_comment";
//        }

        Button btn_back = (Button)findViewById(R.id.button_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSVProgressHUD = new SVProgressHUD(this);
        mRefreshloadmore = (RefreshLoadMoreLayout)findViewById(R.id.refreshloadmore);
        mRefreshloadmore.init(new RefreshLoadMoreLayout.Config(this));

        edit_comment = (EmojiEditText)findViewById(R.id.editText_comment);
        relativeLayout_comment = (RelativeLayout)findViewById(R.id.relativeLayout_comment);
        relativeLayout_post = (RelativeLayout)findViewById(R.id.relativeLayout_post);
        relativeLayout_report = (RelativeLayout)findViewById(R.id.relativeLayout_report);
        btn_abuse = (RelativeLayout)findViewById(R.id.relativeLayout_abuse);
        img_post = (ImageView)findViewById(R.id.imageView_image);
        img_overlay = (ImageView)findViewById(R.id.imageView_overlay);
        avi_indicator = (AVLoadingIndicatorView)findViewById(R.id.indicator);
        img_wave = (ImageView)findViewById(R.id.imageView_wave);
        txt_content = (TextView)findViewById(R.id.textView_text);
        txt_time = (TextView)findViewById(R.id.textView_time);
        txt_period = (TextView)findViewById(R.id.textView_period);
        txt_distance = (TextView)findViewById(R.id.textView_location);
        txt_comment_count = (TextView)findViewById(R.id.textView_comment_count);
        txt_like_count = (TextView)findViewById(R.id.textView_like_count);
        btn_play = (ImageButton)findViewById(R.id.imageButton_play);
        prog_loading = (ProgressBar)findViewById(R.id.audio_progress);
        prog_circle = (CircleProgressBar)findViewById(R.id.custom_progress);
        btn_comment = (ImageButton)findViewById(R.id.imageButton_comment);
        btn_like = (ImageButton)findViewById(R.id.imageButton_like);
        btn_dislike = (ImageButton)findViewById(R.id.imageButton_dislike);
        btn_report = (ImageButton)findViewById(R.id.imageButton_report);

        mListview = (ListView)findViewById(R.id.listview);
        mListview.setAdapter(mAdapter = new Adapter(this));

//        LinearLayout cell_list = (LinearLayout) (View.inflate(this, R.layout.row_list_comment, null));
//        cell_height = cell_list.getHeight();

        edit_comment.setHintTextColor(getResources().getColor(R.color.gray));
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                post_data = edit_comment.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.d(TAG, "==================" + edit_comment.getLineCount());

                if (charSequence.toString().length() > 200) {
                    edit_comment.setText(post_data);
                } else if (edit_comment.getLineCount() > 7) {
                    edit_comment.setText(edit_comment.getText().toString().replace("\n", " "));
                }

                int unit_height = (int)(25 * getResources().getDisplayMetrics().density);
                int stand_height = (int)(58 * getResources().getDisplayMetrics().density);
                if (edit_comment.getLineCount() > 1) {
                    int height = edit_comment.getLineCount() * unit_height;
                    relativeLayout_comment.getLayoutParams().height = height;
                    edit_comment.getLayoutParams().height = height;
                    edit_comment.setHeight(stand_height);
                } else {
                    relativeLayout_comment.getLayoutParams().height = stand_height;
                    edit_comment.getLayoutParams().height = stand_height;
                    edit_comment.setHeight(stand_height);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        relativeLayout_report.setVisibility(View.GONE);

        btn_abuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout_report.setVisibility(View.GONE);

                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_ABUSE);
                GlobalSharedData.updateUserDBData();
                Utility.karmaInBackground(Global.KARMA_ABUSE);

                if (!Global.g_postData.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {

                    mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                    int reportCount = Global.g_postData.getReportCount() + 1;
                    Global.g_postData.setReportCount(reportCount);

                    ArrayList<String> reportTypeArray = new ArrayList<String>();
                    reportTypeArray = Global.g_postData.getReportTypeArray();
                    reportTypeArray.add(Global.g_userInfo.getUserID());
                    Global.g_postData.setReportTypeArray(reportTypeArray);

                    Global.g_postData.setReportType("");
                    if (Global.g_postData.getReportTypeArray().size() > 1) {
                        Global.g_postData.setReportType(Global.g_postData.getReportTypeArray().get(0));
                        for (int i=1; i<Global.g_postData.getReportTypeArray().size(); i++) {
                            Global.g_postData.setReportType(String.format("%s;%s", Global.g_postData.getReportType(), Global.g_postData.getReportTypeArray().get(i)));
                        }
                    } else if (Global.g_postData.getReportTypeArray().size() == 1) {
                        Global.g_postData.setReportType(Global.g_postData.getReportTypeArray().get(0));
                    }

                    BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                    String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, Global.g_postData.getObjectId());
                    dataQuery.setWhereClause( whereClause );

                    Backendless.Persistence.of( Post.class ).find( dataQuery,
                            new AsyncCallback<BackendlessCollection<Post>>(){
                                @Override
                                public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                                {
                                    if (postBackendlessCollection.getData().size() > 0) {

                                        Post updatedData = postBackendlessCollection.getData().get(0);
                                        updatedData.setReportCount(Global.g_postData.getReportCount());
                                        updatedData.setReportType(Global.g_postData.getReportType());

                                        Backendless.Persistence.save(updatedData, new AsyncCallback<Post>() {
                                            @Override
                                            public void handleResponse(Post response) {
                                                Log.d(TAG, "****************** Post Report Count is succeed! *************************");

                                                mSVProgressHUD.dismiss();
                                                finish();
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                Log.d(TAG, "=========================== Post Report Count is failed! ===========================");
                                                mSVProgressHUD.dismiss();
                                            }
                                        });

                                    } else {
                                        Log.d(TAG, "=========================== Post Report Count is not found! ===========================");
                                        mSVProgressHUD.dismiss();
                                    }
                                }
                                @Override
                                public void handleFault( BackendlessFault fault )
                                {
                                    Log.d(TAG, "=========================== Post Report Count is failed! ===========================");
                                    mSVProgressHUD.dismiss();
                                }
                            });

                }

            }
        });

        Button btn_post = (Button)findViewById(R.id.button_post);
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_comment = edit_comment.getText().toString();
                if (str_comment.equals("") || str_comment.equals(getString(R.string.post_placeholder))) {
                    dialog = new NormalAlertDialog.Builder(PostDetailsActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_enter_comment))
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
                    return;
                }

                String toServerUnicodeEncoded = StringEscapeUtils.escapeJava(str_comment);

                ActivityPost commentData = new ActivityPost();
                commentData.setPostId(Global.g_postData.getObjectId());
                commentData.setComment(toServerUnicodeEncoded);
                commentData.setFromUser(Global.g_userInfo.getUserID());
                commentData.setToUser(Global.g_postData.getUserID());
                commentData.setLatitude(Float.toString(Global.g_userInfo.getLatitude()));
                commentData.setLongitude(Float.toString(Global.g_userInfo.getLongitude()));
                commentData.setTime(GlobalSharedData.getCurrentDate());
                commentData.setLikeCount(0);
                commentData.setReportCount(0);
                commentData.setLikeType("");
                commentData.setReportType("");

                if (Global.g_postData.getType().equals(Global.POST_TYPE_PHOTO)) {
                    commentData.setBackColor(Global.COMMENT_COLOR10);
                } else {
                    commentData.setBackColor(Utility.setCommentBackColor(Global.g_postData.getBackColor()));
                }

                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                Backendless.Persistence.save(commentData, new AsyncCallback<ActivityPost>() {
                    @Override
                    public void handleResponse(ActivityPost response) {
                        Log.d(TAG, "Saved new comment data");

                        mSVProgressHUD.dismiss();
                        edit_comment.setText("");

//                        if (keyboard.equals("Keyboard_comment")) {
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                        }

                        btn_comment.setImageDrawable(getResources().getDrawable(R.mipmap.comment));
                        txt_comment_count.setText(GlobalSharedData.getFormattedCount(Global.g_postData.getCommentCount() + 1));

                        int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_COMMENT;
                        Global.g_userInfo.setKarmaScore(karmaScore);
                        int commentCount = Global.g_userInfo.getCommentCount() + 1;
                        Global.g_userInfo.setCommentCount(commentCount);

                        GlobalSharedData.updateUserDBData();
                        Utility.karmaInBackground(Global.KARMA_COMMENT);

                        ActivityPostData record = new ActivityPostData();
                        record.setObjectId(response.getObjectId());
                        record.setPostId(response.getPostId());
                        record.setComment(response.getComment());
                        record.setBackColor(response.getBackColor());
                        record.setFromUser(response.getFromUser());
                        record.setToUser(response.getToUser());
                        record.setLatitude(response.getLatitude());
                        record.setLongitude(response.getLongitude());
                        record.setTime(response.getTime());
                        record.setLikeCount(response.getLikeCount());
                        record.setLikeType(response.getLikeType());
                        record.setReportCount(response.getReportCount());
                        record.setReportType(response.getReportType());

                        mAdapter.addItem(0, record);

                        //=============================================

                        int count = Global.g_postData.getCommentCount() + 1;
                        Global.g_postData.setCommentCount(count);

                        ArrayList<String> commentTypeArray = new ArrayList<String>();
                        commentTypeArray = Global.g_postData.getCommentTypeArray();
                        commentTypeArray.add(Global.g_userInfo.getUserID());
                        Global.g_postData.setCommentTypeArray(commentTypeArray);

                        Global.g_postData.setCommentType("");
                        if (Global.g_postData.getCommentTypeArray().size() > 1) {
                            Global.g_postData.setCommentType(Global.g_postData.getCommentTypeArray().get(0));
                            for (int i=1; i<Global.g_postData.getCommentTypeArray().size(); i++) {
                                Global.g_postData.setCommentType(String.format("%s;%s", Global.g_postData.getCommentType(), Global.g_postData.getCommentTypeArray().get(i)));
                            }
                        } else if (Global.g_postData.getCommentTypeArray().size() == 1) {
                            Global.g_postData.setCommentType(Global.g_postData.getCommentTypeArray().get(0));
                        }

                        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                        String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, Global.g_postData.getObjectId());
                        dataQuery.setWhereClause( whereClause );

                        Backendless.Persistence.of( Post.class ).find( dataQuery,
                                new AsyncCallback<BackendlessCollection<Post>>(){
                                    @Override
                                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                                    {
                                        if (postBackendlessCollection.getData().size() > 0) {

                                            Post updatedData = postBackendlessCollection.getData().get(0);
                                            updatedData.setCommentCount(Global.g_postData.getCommentCount());
                                            updatedData.setCommentType(Global.g_postData.getCommentType());

                                            Backendless.Persistence.save(updatedData, new AsyncCallback<Post>() {
                                                @Override
                                                public void handleResponse(Post response) {

                                                    mSVProgressHUD.dismiss();

                                                    Log.d(TAG, "****************** Post Comment is succeed! *************************");
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {

                                                    mSVProgressHUD.dismiss();
                                                    Log.d(TAG, "=========================== Post Comment is failed! ===========================");
                                                }
                                            });

                                        } else {

                                            mSVProgressHUD.dismiss();
                                            Log.d(TAG, "=========================== Post Comment is failed! ===========================");
                                        }
                                    }
                                    @Override
                                    public void handleFault( BackendlessFault fault )
                                    {
                                        mSVProgressHUD.dismiss();
                                        Log.d(TAG, "=========================== Post Comment is failed! ===========================");
                                    }
                                });

                        //=============================================
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d(TAG, String.format("Failed save in background of comment data = %s", fault.toString()));
                        mSVProgressHUD.dismiss();
                        dialog = new NormalAlertDialog.Builder(PostDetailsActivity.this)
                                .setHeight(0.2f)
                                .setWidth(0.8f)
                                .setTitleVisible(true)
                                .setTitleText(getString(R.string.alert))
                                .setTitleTextColor(R.color.black)
                                .setContentText(getString(R.string.alert_new_comment_failed))
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

        onShowPostData();
        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
        initLoadCommentDataDetails();

    }

    public void onShowPostData() {

        if (Global.g_postData.getType().equals(Global.POST_TYPE_TEXT)) {

            img_post.setVisibility(View.INVISIBLE);
            img_overlay.setVisibility(View.INVISIBLE);
            avi_indicator.setVisibility(View.INVISIBLE);
            img_wave.setVisibility(View.INVISIBLE);
            txt_period.setVisibility(View.INVISIBLE);
            btn_play.setVisibility(View.INVISIBLE);
            prog_loading.setVisibility(View.INVISIBLE);
            prog_circle.setVisibility(View.INVISIBLE);

            int minHeight = (int) (75 * getResources().getDisplayMetrics().density);
            int height = (int) (75 * getResources().getDisplayMetrics().density);
            txt_content.setMinHeight(minHeight);
            img_post.getLayoutParams().height = height;

            relativeLayout_post.setBackgroundColor(Color.parseColor(String.format("#%s", Global.g_postData.getBackColor())));
            String str_post = String.format("%s", Global.g_postData.getContent());
            String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(str_post);
            txt_content.setText(fromServerUnicodeDecoded);

            relativeLayout_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativeLayout_report.setVisibility(View.GONE);
                }
            });

        } else if (Global.g_postData.getType().equals(Global.POST_TYPE_AUDIO)) {

            img_post.setVisibility(View.INVISIBLE);
            img_overlay.setVisibility(View.INVISIBLE);
            avi_indicator.setVisibility(View.INVISIBLE);
            txt_content.setVisibility(View.INVISIBLE);
            prog_loading.setVisibility(View.INVISIBLE);
            prog_circle.setVisibility(View.INVISIBLE);

            int minHeight = (int) (75 * getResources().getDisplayMetrics().density);
            int height = (int) (75 * getResources().getDisplayMetrics().density);
            txt_content.setMinHeight(minHeight);
            img_post.getLayoutParams().height = height;

            relativeLayout_post.setBackgroundColor(Color.parseColor(String.format("#%s", Global.g_postData.getBackColor())));
            txt_content.setText(" ");
            txt_period.setText(String.format("%s", Global.g_postData.getPeriod()));
            btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_play));

            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(Global.g_postData.getFilePath());
                mPlayer.prepareAsync();
            } catch (IOException e) {

            }

            btn_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mPlayer!=null && mPlayer.isPlaying()){
                        mPlayer.pause();
                        btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_play));
                    } else {
                        btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_pause));
                        prog_loading.setVisibility(View.VISIBLE);

                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                prog_loading.setVisibility(View.INVISIBLE);
                                prog_circle.setVisibility(View.VISIBLE);

                                if (mPlayer != null) {
                                    mPlayer.start();
                                    btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_pause));

                                    ValueAnimator animator = ValueAnimator.ofInt(0, 100);
                                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            int progress = (int) animation.getAnimatedValue();
                                            prog_circle.setProgress(progress);
                                        }
                                    });
                                    animator.setDuration(mPlayer.getDuration());
                                    animator.start();
                                } else {
                                    btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_play));
                                }
                            }
                        });

                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                if (mPlayer != null) {
//                                    mPlayer.release();
//                                }
                                prog_loading.setVisibility(View.INVISIBLE);
                                prog_circle.setVisibility(View.INVISIBLE);
                                btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_play));
                            }
                        });

                        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                prog_loading.setVisibility(View.INVISIBLE);
                                prog_circle.setVisibility(View.INVISIBLE);
                                btn_play.setImageDrawable(getResources().getDrawable(R.mipmap.voice_play));
                                return true;
                            }
                        });
                    }
                }
            });

            relativeLayout_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativeLayout_report.setVisibility(View.GONE);
                }
            });

        } else if (Global.g_postData.getType().equals(Global.POST_TYPE_PHOTO)) {

            txt_content.setVisibility(View.INVISIBLE);
            img_wave.setVisibility(View.INVISIBLE);
            txt_period.setVisibility(View.INVISIBLE);
            btn_play.setVisibility(View.INVISIBLE);
            prog_loading.setVisibility(View.INVISIBLE);
            prog_circle.setVisibility(View.INVISIBLE);

            int minHeight = (int) (155 * getResources().getDisplayMetrics().density);
            int height = (int) (220 * getResources().getDisplayMetrics().density);
            txt_content.setMinHeight(minHeight);
            img_post.getLayoutParams().height = height;

            txt_content.setText(" ");
            UrlImageViewHelper.setUrlDrawable(img_post, Global.g_postData.getFilePath(), R.mipmap.post_image, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    avi_indicator.setVisibility(View.INVISIBLE);
                }
            });

            img_post.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    Intent intent = new Intent(PostDetailsActivity.this, PhotoDetailsActivity.class);
                    intent.putExtra("photoUrl", Global.g_postData.getFilePath());
                    startActivity(intent);
                }
            });

            relativeLayout_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (relativeLayout_report.getVisibility() == View.VISIBLE) {
                        relativeLayout_report.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(PostDetailsActivity.this, PhotoDetailsActivity.class);
                        intent.putExtra("photoUrl", Global.g_postData.getFilePath());
                        startActivity(intent);
                    }
                }
            });
        }

        txt_time.setText(GlobalSharedData.getFormattedTimeStamp(Global.g_postData.getTime()));
        txt_distance.setText(GlobalSharedData.getFormattedDistance(String.valueOf(Global.g_userInfo.getLatitude()),
                String.valueOf(Global.g_userInfo.getLongitude()),
                Global.g_postData.getLatitude(), Global.g_postData.getLongitude()));

        txt_comment_count.setText(GlobalSharedData.getFormattedCount(Global.g_postData.getCommentCount()));
        txt_like_count.setText(GlobalSharedData.getFormattedCount(Global.g_postData.getLikeCount()));

        if (Global.g_postData.getCommentTypeArray().contains(Global.g_userInfo.getUserID())) {
            btn_comment.setImageDrawable(getResources().getDrawable(R.mipmap.comment));
        } else {
            btn_comment.setImageDrawable(getResources().getDrawable(R.mipmap.comment_normal));
        }

        if (Global.g_postData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
            btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
            btn_dislike.setImageDrawable(getResources().getDrawable(R.mipmap.dislike_normal));
        } else if (Global.g_postData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
            btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like_normal));
            btn_dislike.setImageDrawable(getResources().getDrawable(R.mipmap.dislike));
        } else {
            btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like_normal));
            btn_dislike.setImageDrawable(getResources().getDrawable(R.mipmap.dislike_normal));
        }

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (relativeLayout_report.getVisibility() == View.VISIBLE) {
                    relativeLayout_report.setVisibility(View.GONE);
                } else {
                    relativeLayout_report.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Global.g_postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE)) &&
                        !Global.g_postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                    String item = String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE);
                    ArrayList<String> likeTypeArray = new ArrayList<String>();
                    likeTypeArray = Global.g_postData.getLikeTypeArray();
                    likeTypeArray.add(item);
                    Global.g_postData.setLikeTypeArray(likeTypeArray);
                    int likeCount = Global.g_postData.getLikeCount();
                    likeCount++;
                    Global.g_postData.setLikeCount(likeCount);

                    Global.g_postData.setLikeType("");
                    if (Global.g_postData.getLikeTypeArray().size() > 1) {
                        Global.g_postData.setLikeType(Global.g_postData.getLikeTypeArray().get(0));
                        for (int i=1; i<Global.g_postData.getLikeTypeArray().size(); i++) {
                            Global.g_postData.setLikeType(String.format("%s;%s", Global.g_postData.getLikeType(), Global.g_postData.getLikeTypeArray().get(i)));
                        }
                    } else if (Global.g_postData.getLikeTypeArray().size() == 1) {
                        Global.g_postData.setLikeType(Global.g_postData.getLikeTypeArray().get(0));
                    }

                    btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
                    txt_like_count.setText(GlobalSharedData.getFormattedCount(Global.g_postData.getLikeCount()));

                    Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_LIKE);
                    Global.g_userInfo.setVoteCount(Global.g_userInfo.getVoteCount() + 1);
                    Utility.karmaInBackground(Global.KARMA_VOTE_LIKE);

                    //-----------------------------------------

                    BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                    String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, Global.g_postData.getObjectId());
                    dataQuery.setWhereClause( whereClause );

                    Backendless.Persistence.of( Post.class ).find( dataQuery,
                            new AsyncCallback<BackendlessCollection<Post>>(){
                                @Override
                                public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                                {
                                    if (postBackendlessCollection.getData().size() > 0) {

                                        Post updatedData = postBackendlessCollection.getData().get(0);
                                        updatedData.setLikeCount(Global.g_postData.getLikeCount());
                                        updatedData.setLikeType(Global.g_postData.getLikeType());

                                        Backendless.Persistence.save(updatedData, new AsyncCallback<Post>() {
                                            @Override
                                            public void handleResponse(Post response) {
                                                Log.d(TAG, "****************** Post Like is succeed! *************************");
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                Log.d(TAG, "=========================== Post Like is failed! ===========================");
                                            }
                                        });

                                    } else {
                                        Log.d(TAG, "=========================== Post Like is failed! ===========================");
                                    }
                                }
                                @Override
                                public void handleFault( BackendlessFault fault )
                                {
                                    Log.d(TAG, "=========================== Post Like is failed! ===========================");
                                }
                            });

                    //------------------------------------------

                }
            }
        });

        btn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Global.g_postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))
                        && !Global.g_postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {

                    if (Global.g_postData.getLikeCount() == -4 && !Global.g_postData.getUserID().equals(Global.ADMIN_EMAIL)) {

                        //------------------------------------

                        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                        String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, Global.g_postData.getObjectId());
                        dataQuery.setWhereClause( whereClause );

                        Backendless.Persistence.of( Post.class ).find( dataQuery,
                                new AsyncCallback<BackendlessCollection<Post>>(){
                                    @Override
                                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                                    {
                                        if (postBackendlessCollection.getData().size() > 0) {

                                            Post updatedData = postBackendlessCollection.getData().get(0);

                                            Backendless.Persistence.of(Post.class).remove(updatedData, new AsyncCallback<Long>() {
                                                @Override
                                                public void handleResponse(Long response) {
                                                    Log.d("DislikePost", "=========================== Post Delete is succeed! ===========================");
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Log.d("DislikePost", "=========================== Post Delete is failed! ===========================");
                                                }
                                            });

                                        } else {
                                            Log.d("DislikePost", "=========================== Post Delete is not found! ===========================");
                                        }
                                    }
                                    @Override
                                    public void handleFault( BackendlessFault fault )
                                    {
                                        Log.d("DislikePost", "=========================== Post Delete is failed! ===========================");
                                    }
                                });

                        //-------------------------------------------

                        Log.d(TAG, "****************** Post Dislike is deleted! *************************");

                        if (Global.g_postData.getUserID().equals(Global.g_userInfo.getUserID())) {
                            int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST;
                            Global.g_userInfo.setKarmaScore(karmaScore);
                            GlobalSharedData.updateUserDBData();
                            Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_POST);

                            dialog = new NormalAlertDialog.Builder(PostDetailsActivity.this)
                                    .setHeight(0.21f)
                                    .setWidth(0.8f)
                                    .setTitleVisible(true)
                                    .setTitleText(getString(R.string.karma))
                                    .setTitleTextColor(R.color.black)
                                    .setContentText(getString(R.string.karma_post_5_downvotes))
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

                        } else {
                            int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_DISLIKE;
                            Global.g_userInfo.setKarmaScore(karmaScore);
                            GlobalSharedData.updateUserDBData();
                            Utility.karmaInBackground(Global.KARMA_VOTE_DISLIKE);

                            //------------------------------

                            DeliveryOptions deliveryOptions = new DeliveryOptions();
                            deliveryOptions.addPushSinglecast( Global.g_postData.getUserID() );

                            PublishOptions publishOptions = new PublishOptions();
                            publishOptions.putHeader( "android-ticker-text", getString(R.string.ios_post_deleted) );
                            publishOptions.putHeader( "android-content-title", getString(R.string.ios_post_deleted) );
                            publishOptions.putHeader( "android-content-text", getString(R.string.karma_post_5_downvotes) );
                            publishOptions.putHeader( "ios-alert", getString(R.string.karma_post_5_downvotes) );
                            publishOptions.putHeader( "ios-badge", Global.PUSH_BADGE );
                            publishOptions.putHeader( "ios-sound", Global.PUSH_SOUND );

                            Backendless.Messaging.publish(Global.MESSAGE_CHANNEL, getString(R.string.karma_post_5_downvotes), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                                @Override
                                public void handleResponse(MessageStatus response) {
                                    Log.v(TAG, String.format("delete post showMessageStatus :  %s", response.getStatus().toString()));
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e(TAG, String.format("delete post showMessageStatus :  %s", fault.toString()));
                                }
                            });
                            //---------------------------------
                        }

                        finish();

                    } else {

                        String item = String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE);
                        ArrayList<String> likeTypeArray = new ArrayList<String>();
                        likeTypeArray = Global.g_postData.getLikeTypeArray();
                        likeTypeArray.add(item);
                        Global.g_postData.setLikeTypeArray(likeTypeArray);
                        int likeCount = Global.g_postData.getLikeCount() - 1;
                        Global.g_postData.setLikeCount(likeCount);

                        Global.g_postData.setLikeType("");
                        if (Global.g_postData.getLikeTypeArray().size() > 1) {
                            Global.g_postData.setLikeType(Global.g_postData.getLikeTypeArray().get(0));
                            for (int i=1; i<Global.g_postData.getLikeTypeArray().size(); i++) {
                                Global.g_postData.setLikeType(String.format("%s;%s", Global.g_postData.getLikeType(), Global.g_postData.getLikeTypeArray().get(i)));
                            }
                        } else if (Global.g_postData.getLikeTypeArray().size() == 1) {
                            Global.g_postData.setLikeType(Global.g_postData.getLikeTypeArray().get(0));
                        }

                        btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
                        txt_like_count.setText(GlobalSharedData.getFormattedCount(Global.g_postData.getLikeCount()));

                        Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_VOTE_DISLIKE);
                        Global.g_userInfo.setVoteCount(Global.g_userInfo.getVoteCount() + 1);
                        Utility.karmaInBackground(Global.KARMA_VOTE_DISLIKE);

                        //-----------------------------------------

                        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                        String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, Global.g_postData.getObjectId());
                        dataQuery.setWhereClause( whereClause );

                        Backendless.Persistence.of( Post.class ).find( dataQuery,
                                new AsyncCallback<BackendlessCollection<Post>>(){
                                    @Override
                                    public void handleResponse( BackendlessCollection<Post> postBackendlessCollection )
                                    {
                                        if (postBackendlessCollection.getData().size() > 0) {

                                            Post updatedData = postBackendlessCollection.getData().get(0);
                                            updatedData.setLikeCount(Global.g_postData.getLikeCount());
                                            updatedData.setLikeType(Global.g_postData.getLikeType());

                                            Backendless.Persistence.save(updatedData, new AsyncCallback<Post>() {
                                                @Override
                                                public void handleResponse(Post response) {
                                                    Log.d(TAG, "****************** Post Dislike is succeed! *************************");
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Log.d(TAG, "=========================== Post Dislike is failed! ===========================");
                                                }
                                            });

                                        } else {
                                            Log.d(TAG, "=========================== Post Dislike is failed! ===========================");
                                        }
                                    }
                                    @Override
                                    public void handleFault( BackendlessFault fault )
                                    {
                                        Log.d(TAG, "=========================== Post Dislike is failed! ===========================");
                                    }
                                });

                        //------------------------------------------

                        DeliveryOptions deliveryOptions = new DeliveryOptions();
                        deliveryOptions.addPushSinglecast( Global.g_postData.getUserID() );

                        PublishOptions publishOptions = new PublishOptions();
                        publishOptions.putHeader( "android-ticker-text", getString(R.string.karma_decreased) );
                        publishOptions.putHeader( "android-content-title", getString(R.string.karma_decreased) );
                        publishOptions.putHeader( "android-content-text", getString(R.string.karma_someone_downvote_post) );
                        publishOptions.putHeader( "ios-alert", getString(R.string.karma_someone_downvote_post) );
                        publishOptions.putHeader( "ios-badge", Global.PUSH_BADGE );
                        publishOptions.putHeader( "ios-sound", Global.PUSH_SOUND );

                        Backendless.Messaging.publish(Global.MESSAGE_CHANNEL, getString(R.string.karma_someone_downvote_post), publishOptions, deliveryOptions, new AsyncCallback<MessageStatus>() {
                            @Override
                            public void handleResponse(MessageStatus response) {
                                Log.v(TAG, String.format("showMessageStatus :  %s", response.getStatus().toString()));
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e(TAG, String.format("showMessageStatus :  %s", fault.toString()));
                            }
                        });

                        //------------------------------------------
                    }
                }

            }
        });
    }

    public void initLoadCommentDataDetails() {

        dataCount += Global.LOAD_DATA_COUNT;

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_POST_ID, Global.g_postData.getObjectId());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                    {
                        mSVProgressHUD.dismiss();

                        if (activityPostBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<activityPostBackendlessCollection.getData().size(); i++){
                                ActivityPost activityPost = activityPostBackendlessCollection.getData().get(i);

                                ActivityPostData record = new ActivityPostData();
                                record.setObjectId(activityPost.getObjectId());
                                record.setPostId(activityPost.getPostId());
                                record.setComment(activityPost.getComment());
                                record.setBackColor(activityPost.getBackColor());
                                record.setFromUser(activityPost.getFromUser());
                                record.setToUser(activityPost.getToUser());
                                record.setLatitude(activityPost.getLatitude());
                                record.setLongitude(activityPost.getLongitude());
                                record.setTime(activityPost.getTime());
                                record.setLikeCount(activityPost.getLikeCount());
                                record.setLikeType(activityPost.getLikeType());
                                record.setReportCount(activityPost.getReportCount());
                                record.setReportType(activityPost.getReportType());

                                ArrayList<String> likeTypeArray;
                                ArrayList<String> reportTypeArray;

                                if (record.getLikeType() != null && !record.getLikeType().equals("")) {

                                    if (record.getLikeType().contains(";")) {
                                        String[] itemsLike = record.getLikeType().split(";");
                                        likeTypeArray = new ArrayList<String>(Arrays.asList(itemsLike));
                                        record.setLikeTypeArray(likeTypeArray);
                                    } else {
                                        likeTypeArray = new ArrayList<String>();
                                        likeTypeArray.add(record.getLikeType());
                                        record.setLikeTypeArray(likeTypeArray);
                                    }
                                }

                                if (record.getReportType() != null && !record.getReportType().equals("")) {

                                    if (record.getReportType().contains(";")) {
                                        String[] itemsReport = record.getReportType().split(";");
                                        reportTypeArray = new ArrayList<String>(Arrays.asList(itemsReport));
                                        record.setReportTypeArray(reportTypeArray);
                                    } else {
                                        reportTypeArray = new ArrayList<String>();
                                        reportTypeArray.add(record.getReportType());
                                        record.setReportTypeArray(reportTypeArray);
                                    }
                                }

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    mAdapter.addItem(record);
                                    Log.v(TAG, " ------------------------- comment ----------------------------" + i);
                                }
                            }
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mSVProgressHUD.dismiss();
                    }
                });
    }

    @Override
    public void onRefresh() {
        Log.v(TAG, " ------------------------- onRefresh ----------------------------");
//        mRefreshloadmore.setCanLoadMore(true);

        if (dataCount == 0) {
            dataCount = Global.LOAD_DATA_COUNT;
        }

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(dataCount);
        queryOptions.setOffset(0);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_POST_ID, Global.g_postData.getObjectId());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                    {
                        mAdapter.removeAll();

                        if (activityPostBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<activityPostBackendlessCollection.getData().size(); i++){
                                ActivityPost activityPost = activityPostBackendlessCollection.getData().get(i);

                                ActivityPostData record = new ActivityPostData();
                                record.setObjectId(activityPost.getObjectId());
                                record.setPostId(activityPost.getPostId());
                                record.setComment(activityPost.getComment());
                                record.setBackColor(activityPost.getBackColor());
                                record.setFromUser(activityPost.getFromUser());
                                record.setToUser(activityPost.getToUser());
                                record.setLatitude(activityPost.getLatitude());
                                record.setLongitude(activityPost.getLongitude());
                                record.setTime(activityPost.getTime());
                                record.setLikeCount(activityPost.getLikeCount());
                                record.setLikeType(activityPost.getLikeType());
                                record.setReportCount(activityPost.getReportCount());
                                record.setReportType(activityPost.getReportType());

                                ArrayList<String> likeTypeArray;
                                ArrayList<String> reportTypeArray;

                                if (record.getLikeType() != null && !record.getLikeType().equals("")) {

                                    if (record.getLikeType().contains(";")) {
                                        String[] itemsLike = record.getLikeType().split(";");
                                        likeTypeArray = new ArrayList<String>(Arrays.asList(itemsLike));
                                        record.setLikeTypeArray(likeTypeArray);
                                    } else {
                                        likeTypeArray = new ArrayList<String>();
                                        likeTypeArray.add(record.getLikeType());
                                        record.setLikeTypeArray(likeTypeArray);
                                    }
                                }

                                if (record.getReportType() != null && !record.getReportType().equals("")) {

                                    if (record.getReportType().contains(";")) {
                                        String[] itemsReport = record.getReportType().split(";");
                                        reportTypeArray = new ArrayList<String>(Arrays.asList(itemsReport));
                                        record.setReportTypeArray(reportTypeArray);
                                    } else {
                                        reportTypeArray = new ArrayList<String>();
                                        reportTypeArray.add(record.getReportType());
                                        record.setReportTypeArray(reportTypeArray);
                                    }
                                }

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    mAdapter.addItem(record);
                                }
                            }

                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh finish ==================");

                        } else {
                            mRefreshloadmore.stopRefresh();
                            Log.v(TAG, "============== onRefresh empty ==================");
                        }
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopRefresh();
                        Log.v(TAG, "============== onRefresh failed ==================");
                    }
                });
    }

    @Override
    public void onLoadMore() {
        Log.v(TAG, "---------------------- onLoadMore ---------------------------------");

        QueryOptions queryOptions = new QueryOptions();
        List<String> sortBy = new ArrayList<String>();
        sortBy.add( "time DESC" );
        queryOptions.setSortBy( sortBy );
        queryOptions.setPageSize(Global.LOAD_DATA_COUNT);
        queryOptions.setOffset(dataCount);

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        String whereClause = String.format("%s = '%s'", Global.KEY_POST_ID, Global.g_postData.getObjectId());
        dataQuery.setWhereClause( whereClause );
        dataQuery.setQueryOptions( queryOptions );

        Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                    @Override
                    public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                    {
                        if (activityPostBackendlessCollection.getData().size() > 0) {

                            for (int i=0; i<activityPostBackendlessCollection.getData().size(); i++){
                                ActivityPost activityPost = activityPostBackendlessCollection.getData().get(i);

                                ActivityPostData record = new ActivityPostData();
                                record.setObjectId(activityPost.getObjectId());
                                record.setPostId(activityPost.getPostId());
                                record.setComment(activityPost.getComment());
                                record.setBackColor(activityPost.getBackColor());
                                record.setFromUser(activityPost.getFromUser());
                                record.setToUser(activityPost.getToUser());
                                record.setLatitude(activityPost.getLatitude());
                                record.setLongitude(activityPost.getLongitude());
                                record.setTime(activityPost.getTime());
                                record.setLikeCount(activityPost.getLikeCount());
                                record.setLikeType(activityPost.getLikeType());
                                record.setReportCount(activityPost.getReportCount());
                                record.setReportType(activityPost.getReportType());

                                ArrayList<String> likeTypeArray;
                                ArrayList<String> reportTypeArray;

                                if (record.getLikeType() != null && !record.getLikeType().equals("")) {

                                    if (record.getLikeType().contains(";")) {
                                        String[] itemsLike = record.getLikeType().split(";");
                                        likeTypeArray = new ArrayList<String>(Arrays.asList(itemsLike));
                                        record.setLikeTypeArray(likeTypeArray);
                                    } else {
                                        likeTypeArray = new ArrayList<String>();
                                        likeTypeArray.add(record.getLikeType());
                                        record.setLikeTypeArray(likeTypeArray);
                                    }
                                }

                                if (record.getReportType() != null && !record.getReportType().equals("")) {

                                    if (record.getReportType().contains(";")) {
                                        String[] itemsReport = record.getReportType().split(";");
                                        reportTypeArray = new ArrayList<String>(Arrays.asList(itemsReport));
                                        record.setReportTypeArray(reportTypeArray);
                                    } else {
                                        reportTypeArray = new ArrayList<String>();
                                        reportTypeArray.add(record.getReportType());
                                        record.setReportTypeArray(reportTypeArray);
                                    }
                                }

                                if (!record.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                                    mAdapter.addItem(record);
                                }
                            }
                            mRefreshloadmore.stopLoadMore();
                            Log.v(TAG, "============== onLoadMore finish ==================");

                        } else {

                            mRefreshloadmore.stopLoadMore();
//                            mRefreshloadmore.setCanLoadMore(false);
                            Log.v(TAG, "============== onLoadMore empty ==================");
                        }
                        dataCount += Global.LOAD_DATA_COUNT;
                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        mRefreshloadmore.stopLoadMore();
                        Log.v(TAG, "============== onLoadMore failed ==================");
                    }
                });
    }

    public class Adapter extends BaseAdapter {
        private Context mContext;
        private List<ActivityPostData> mCommentDataList = new ArrayList<>();

        public Adapter(Context context) {
            mContext = context;
        }

        public void addItem(ActivityPostData item) {
            mCommentDataList.add(item);
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mListview);
        }

        public void addItem(int pos, ActivityPostData item) {
            mCommentDataList.add(pos, item);
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mListview);
        }

        public void replaceItem(int pos, ActivityPostData item) {
            mCommentDataList.set(pos, item);
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mListview);
        }

        public void removeItem(int pos) {
            mCommentDataList.remove(pos);
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mListview);
        }

        public void removeAll() {
            mCommentDataList = new ArrayList<>();
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mListview);
        }

        @Override
        public int getCount() {
            return mCommentDataList.size();
        }

        @Override
        public ActivityPostData getItem(int position) {
            return mCommentDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_list_comment, parent, false);
                convertView.setTag(viewHolder = new ViewHolder(convertView));
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final ActivityPostData commentData = mCommentDataList.get(position);

            viewHolder.relativeLayout_back.setBackgroundColor(Color.parseColor(String.format("#%s", commentData.getBackColor())));
            String str_comment = String.format("%s", commentData.getComment());
            String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(str_comment);
            viewHolder.txt_content.setText(fromServerUnicodeDecoded);
            viewHolder.txt_time.setText(GlobalSharedData.getFormattedTimeStamp(commentData.getTime()));
            viewHolder.txt_distance.setText(GlobalSharedData.getFormattedDistance(String.valueOf(Global.g_userInfo.getLatitude()),
                    String.valueOf(Global.g_userInfo.getLongitude()),
                    commentData.getLatitude(), commentData.getLongitude()));
            viewHolder.txt_like_count.setText(GlobalSharedData.getFormattedCount(commentData.getLikeCount()));

            if (commentData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
                viewHolder.btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like));
                viewHolder.btn_dislike.setImageDrawable(getResources().getDrawable(R.mipmap.dislike_normal));
            } else if (commentData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                viewHolder.btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like_normal));
                viewHolder.btn_dislike.setImageDrawable(getResources().getDrawable(R.mipmap.dislike));
            } else {
                viewHolder.btn_like.setImageDrawable(getResources().getDrawable(R.mipmap.like_normal));
                viewHolder.btn_dislike.setImageDrawable(getResources().getDrawable(R.mipmap.dislike_normal));
            }

            if (commentData.getFromUser().equals(Global.g_userInfo.getUserID())) {
                viewHolder.relativeLayout_my.setVisibility(View.VISIBLE);
                viewHolder.txt_my_comment.setVisibility(View.VISIBLE);
            } else {

            }

            viewHolder.btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!commentData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE)) &&
                            !commentData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                        Utility.likeCommentInBackground(commentData, viewHolder, mContext, Adapter.this, position);
                    }
                }
            });

            viewHolder.btn_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!commentData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))
                            && !commentData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
                        Utility.dislikeCommentInBackground(commentData, viewHolder, mContext, Adapter.this, position);
                    }
                }
            });

            viewHolder.btn_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.relativeLayout_report.getVisibility() == View.VISIBLE) {
                        viewHolder.relativeLayout_report.setVisibility(View.GONE);
                    } else {
                        viewHolder.relativeLayout_report.setVisibility(View.VISIBLE);
                    }
                }
            });

            viewHolder.relativeLayout_report.setVisibility(View.GONE);

            viewHolder.relativeLayout_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.relativeLayout_report.setVisibility(View.GONE);
                }
            });

            viewHolder.btn_abuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.relativeLayout_report.setVisibility(View.GONE);

                    if (!commentData.getReportTypeArray().contains(Global.g_userInfo.getUserID())) {
                        int reportCount = commentData.getReportCount() + 1;
                        commentData.setReportCount(reportCount);

                        ArrayList<String> reportTypeArray = new ArrayList<String>();
                        reportTypeArray = commentData.getReportTypeArray();
                        reportTypeArray.add(Global.g_userInfo.getUserID());
                        commentData.setReportTypeArray(reportTypeArray);

                        commentData.setReportType("");
                        if (commentData.getReportTypeArray().size() > 1) {
                            commentData.setReportType(commentData.getReportTypeArray().get(0));
                            for (int i=1; i<commentData.getReportTypeArray().size(); i++) {
                                commentData.setReportType(String.format("%s;%s", commentData.getReportType(), commentData.getReportTypeArray().get(i)));
                            }
                        } else if (commentData.getReportTypeArray().size() == 1) {
                            commentData.setReportType(commentData.getReportTypeArray().get(0));
                        }

                        final String objectID = commentData.getObjectId();
                        final int report_Count = commentData.getReportCount();
                        final String report_type = commentData.getReportType();

                        removeItem(position);

                        Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_ABUSE);
                        GlobalSharedData.updateUserDBData();
                        Utility.karmaInBackground(Global.KARMA_COMMENT_ABUSE);

                        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                        String whereClause = String.format("%s = '%s'", Global.KEY_OBJECT_ID, objectID);
                        dataQuery.setWhereClause( whereClause );

                        Backendless.Persistence.of( ActivityPost.class ).find( dataQuery,
                                new AsyncCallback<BackendlessCollection<ActivityPost>>(){
                                    @Override
                                    public void handleResponse( BackendlessCollection<ActivityPost> activityPostBackendlessCollection )
                                    {
                                        if (activityPostBackendlessCollection.getData().size() > 0) {

                                            ActivityPost updatedData = activityPostBackendlessCollection.getData().get(0);
                                            updatedData.setReportCount(report_Count);
                                            updatedData.setReportType(report_type);

                                            Backendless.Persistence.save(updatedData, new AsyncCallback<ActivityPost>() {
                                                @Override
                                                public void handleResponse(ActivityPost response) {
                                                    Log.d(TAG, "****************** Comment Report is succeed! *************************");
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Log.d(TAG, "=========================== Comment Report is failed! ===========================");
                                                }
                                            });

                                        } else {
                                            Log.d(TAG, "=========================== Comment Report is not found! ===========================");
                                        }
                                    }
                                    @Override
                                    public void handleFault( BackendlessFault fault )
                                    {
                                        Log.d(TAG, "=========================== Comment Report is failed! ===========================");
                                    }
                                });

                        //------------------------------------------

                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            public RelativeLayout relativeLayout_back;
            public EmojiTextView txt_content;
//            public TextView txt_content
            public TextView txt_time, txt_distance, txt_like_count, txt_my_comment;
            public ImageButton btn_like, btn_dislike, btn_report;
            public RelativeLayout relativeLayout_report, btn_abuse, relativeLayout_my;

            ViewHolder(View rootView) {
                initView(rootView);
            }

            private void initView(View rootView) {

                relativeLayout_back = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_comment);
//                txt_content = (TextView)rootView.findViewById(R.id.textView_text);
                txt_content = (EmojiTextView)rootView.findViewById(R.id.textView_text);
                txt_time = (TextView)rootView.findViewById(R.id.textView_time);
                txt_distance = (TextView)rootView.findViewById(R.id.textView_location);
                txt_like_count = (TextView)rootView.findViewById(R.id.textView_like_count);
                btn_like = (ImageButton)rootView.findViewById(R.id.imageButton_like);
                btn_dislike = (ImageButton)rootView.findViewById(R.id.imageButton_dislike);
                btn_report = (ImageButton)rootView.findViewById(R.id.imageButton_report);
                relativeLayout_report = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_report);
                btn_abuse = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_abuse);
                relativeLayout_my = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_my);
                txt_my_comment = (TextView)rootView.findViewById(R.id.textView_my_comment);

            }
        }

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
