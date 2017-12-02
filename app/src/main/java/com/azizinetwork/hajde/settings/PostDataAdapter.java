package com.azizinetwork.hajde.settings;

import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.activity.PhotoDetailsActivity;
import com.azizinetwork.hajde.activity.PostDetailsActivity;
import com.azizinetwork.hajde.model.parse.PostData;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.vanniktech.emoji.EmojiTextView;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PostDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<PostData> mPostDataList = new ArrayList<>();
    private MediaPlayer mPlayer = new MediaPlayer();

    public PostDataAdapter(Context context) {
        mContext = context;
    }

    public void addItem(PostData item) {
        mPostDataList.add(item);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mPostDataList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void replaceItem(int pos, PostData item) {
        mPostDataList.set(pos, item);
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        mPostDataList.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPostDataList.size();
    }

    @Override
    public PostData getItem(int position) {
        return mPostDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_list_post, parent, false);
            convertView.setTag(viewHolder = new ViewHolder(convertView));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PostData postData = mPostDataList.get(position);

        if (postData.getType().equals(Global.POST_TYPE_TEXT) || postData.getType().equals(Global.POST_TYPE_AUDIO)) {

            if (postData.getType().equals(Global.POST_TYPE_TEXT)) {

                viewHolder.relativeLayout_image.setVisibility(View.GONE);
                viewHolder.relativeLayout_text_audio.setVisibility(View.VISIBLE);
                viewHolder.img_wave.setVisibility(View.INVISIBLE);
                viewHolder.txt_content.setVisibility(View.VISIBLE);
                viewHolder.txt_period.setVisibility(View.INVISIBLE);
                viewHolder.btn_play.setVisibility(View.INVISIBLE);
                viewHolder.prog_loading.setVisibility(View.INVISIBLE);
                viewHolder.prog_circle.setVisibility(View.INVISIBLE);

                viewHolder.relativeLayout_text_audio.setBackgroundColor(Color.parseColor(String.format("#%s", postData.getBackColor())));
                String str_post = String.format("%s", postData.getContent());
                String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(str_post);
                viewHolder.txt_content.setText(fromServerUnicodeDecoded);

            } else if (postData.getType().equals(Global.POST_TYPE_AUDIO)) {

                viewHolder.relativeLayout_image.setVisibility(View.GONE);
                viewHolder.relativeLayout_text_audio.setVisibility(View.VISIBLE);
                viewHolder.txt_content.setVisibility(View.INVISIBLE);
                viewHolder.txt_period.setVisibility(View.VISIBLE);
                viewHolder.btn_play.setVisibility(View.VISIBLE);
                viewHolder.img_wave.setVisibility(View.VISIBLE);
                viewHolder.prog_loading.setVisibility(View.INVISIBLE);
                viewHolder.prog_circle.setVisibility(View.INVISIBLE);

                viewHolder.relativeLayout_text_audio.setBackgroundColor(Color.parseColor(String.format("#%s", postData.getBackColor())));
                viewHolder.txt_content.setText(" ");
                viewHolder.txt_period.setText(String.format("%s", postData.getPeriod()));
                viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_play));

                final MediaPlayer mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(postData.getFilePath());
                    mPlayer.prepareAsync();
                } catch (IOException e) {

                }

                viewHolder.btn_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(mPlayer!=null && mPlayer.isPlaying()){
                            mPlayer.pause();
                            viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_play));
                        } else {
                            if (mPlayer == null) {
                                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                try {
                                    mPlayer.setDataSource(postData.getFilePath());
                                    mPlayer.prepareAsync();
                                } catch (IOException e) {

                                }
                            }

                            viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_pause));
                            viewHolder.prog_loading.setVisibility(View.VISIBLE);
                            viewHolder.prog_circle.setVisibility(View.INVISIBLE);

                            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    viewHolder.prog_loading.setVisibility(View.INVISIBLE);
                                    viewHolder.prog_circle.setVisibility(View.VISIBLE);

                                    if (mPlayer != null) {
                                        mPlayer.start();
                                        viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_pause));

                                        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
                                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                int progress = (int) animation.getAnimatedValue();
                                                viewHolder.prog_circle.setProgress(progress);
                                            }
                                        });
                                        animator.setDuration(mPlayer.getDuration());
                                        animator.start();
                                    } else {
                                        viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_play));
                                    }
                                }
                            });

                            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
//                                    if (mPlayer != null) {
//                                        mPlayer.release();
//                                    }
                                    viewHolder.prog_circle.setVisibility(View.INVISIBLE);
                                    viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_play));
                                }
                            });

                            mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                    viewHolder.prog_loading.setVisibility(View.INVISIBLE);
                                    viewHolder.prog_circle.setVisibility(View.INVISIBLE);
                                    viewHolder.btn_play.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.voice_play));
                                    return true;
                                }
                            });
                        }
                    }
                });
            }

            viewHolder.btn_report.setVisibility(View.INVISIBLE);

            viewHolder.txt_time.setText(GlobalSharedData.getFormattedTimeStamp(postData.getTime()));
            viewHolder.txt_distance.setText(GlobalSharedData.getFormattedDistance(String.valueOf(Global.g_userInfo.getLatitude()),
                    String.valueOf(Global.g_userInfo.getLongitude()),
                    postData.getLatitude(), postData.getLongitude()));

            viewHolder.txt_comment_count.setText(GlobalSharedData.getFormattedCount(postData.getCommentCount()));
            viewHolder.txt_like_count.setText(GlobalSharedData.getFormattedCount(postData.getLikeCount()));

            if (postData.getCommentTypeArray().contains(Global.g_userInfo.getUserID())) {
                viewHolder.btn_comment.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.comment));
            } else {
                viewHolder.btn_comment.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.comment_normal));
            }

            if (postData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
                viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like));
                viewHolder.btn_dislike.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.dislike_normal));
            } else if (postData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like_normal));
                viewHolder.btn_dislike.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.dislike));
            } else {
                viewHolder.btn_like.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like_normal));
                viewHolder.btn_dislike.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.dislike_normal));
            }

            viewHolder.relativeLayout_text_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Global.g_postData = new PostData();
                    Global.g_postData = mPostDataList.get(position);
                    Intent i = new Intent(mContext, PostDetailsActivity.class);
                    i.putExtra("comment", "Keyboard");
                    mContext.startActivity(i);
                }
            });

            viewHolder.btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE)) &&
                            !postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                        Utility.likePostInBackground(postData, viewHolder, mContext, PostDataAdapter.this, position);
                    }
                }
            });

            viewHolder.btn_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))
                            && !postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
                        Utility.dislikePostInBackground(postData, viewHolder, mContext, PostDataAdapter.this, position);
                    }
                }
            });

            viewHolder.btn_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Global.g_postData = new PostData();
                    Global.g_postData = mPostDataList.get(position);
                    Intent i = new Intent(mContext, PostDetailsActivity.class);
                    i.putExtra("comment", "Keyboard_comment");
                    mContext.startActivity(i);
                }
            });

        } else if (postData.getType().equals(Global.POST_TYPE_PHOTO)) {

            viewHolder.relativeLayout_text_audio.setVisibility(View.GONE);
            viewHolder.relativeLayout_image.setVisibility(View.VISIBLE);

            UrlImageViewHelper.setUrlDrawable(viewHolder.img_post, postData.getFilePath(), R.mipmap.post_image, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    viewHolder.avi_indicator.setVisibility(View.INVISIBLE);
                }
            });

            viewHolder.img_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Global.g_postData = new PostData();
                    Global.g_postData = mPostDataList.get(position);
                    Intent i = new Intent(mContext, PostDetailsActivity.class);
                    i.putExtra("comment", "Keyboard");
                    mContext.startActivity(i);
                }
            });

            viewHolder.img_post.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    Intent intent = new Intent(mContext, PhotoDetailsActivity.class);
                    intent.putExtra("photoUrl", mPostDataList.get(position).getFilePath());
                    mContext.startActivity(intent);
                    return true;
                }
            });

            viewHolder.btn_report_image.setVisibility(View.INVISIBLE);

            viewHolder.txt_time_image.setText(GlobalSharedData.getFormattedTimeStamp(postData.getTime()));
            viewHolder.txt_distance_image.setText(GlobalSharedData.getFormattedDistance(String.valueOf(Global.g_userInfo.getLatitude()),
                    String.valueOf(Global.g_userInfo.getLongitude()),
                    postData.getLatitude(), postData.getLongitude()));

            viewHolder.txt_comment_count_image.setText(GlobalSharedData.getFormattedCount(postData.getCommentCount()));
            viewHolder.txt_like_count_image.setText(GlobalSharedData.getFormattedCount(postData.getLikeCount()));

            if (postData.getCommentTypeArray().contains(Global.g_userInfo.getUserID())) {
                viewHolder.btn_comment_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.comment));
            } else {
                viewHolder.btn_comment_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.comment_normal));
            }

            if (postData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
                viewHolder.btn_like_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like));
                viewHolder.btn_dislike_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.dislike_normal));
            } else if (postData.getLikeType().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                viewHolder.btn_like_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like_normal));
                viewHolder.btn_dislike_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.dislike));
            } else {
                viewHolder.btn_like_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.like_normal));
                viewHolder.btn_dislike_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.dislike_normal));
            }

            viewHolder.btn_like_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE)) &&
                            !postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))) {
                        Utility.likePostInBackground(postData, viewHolder, mContext, PostDataAdapter.this, position);
                    }
                }
            });

            viewHolder.btn_dislike_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.DISLIKE_TYPE))
                            && !postData.getLikeTypeArray().contains(String.format("%s:%s", Global.g_userInfo.getUserID(), Global.LIKE_TYPE))) {
                        Utility.dislikePostInBackground(postData, viewHolder, mContext, PostDataAdapter.this, position);
                    }
                }
            });

            viewHolder.btn_comment_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Global.g_postData = new PostData();
                    Global.g_postData = mPostDataList.get(position);
                    Intent i = new Intent(mContext, PostDetailsActivity.class);
                    i.putExtra("comment", "Keyboard_comment");
                    mContext.startActivity(i);
                }
            });
        }

        return convertView;
    }

    public class ViewHolder {
        public RelativeLayout relativeLayout_text_audio;
        public ImageView img_wave;
//        public TextView txt_content;
        public EmojiTextView txt_content;
        public TextView txt_period, txt_time, txt_distance, txt_comment_count, txt_like_count;
        public ImageButton btn_play, btn_comment, btn_like, btn_dislike, btn_report;
        public ProgressBar prog_loading;
        public CircleProgressBar prog_circle;

        public RelativeLayout relativeLayout_image;
        public ImageView img_post, img_overlay;
        public AVLoadingIndicatorView avi_indicator;
        public TextView txt_time_image, txt_distance_image, txt_comment_count_image, txt_like_count_image;
        public ImageButton btn_comment_image, btn_like_image, btn_dislike_image, btn_report_image;


        ViewHolder(View rootView) {
            initView(rootView);
        }

        private void initView(View rootView) {

            relativeLayout_text_audio = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_text_audio);
            img_wave = (ImageView)rootView.findViewById(R.id.imageView_wave);
//            txt_content = (TextView)rootView.findViewById(R.id.textView_text);
            txt_content = (EmojiTextView)rootView.findViewById(R.id.textView_text);

            txt_time = (TextView)rootView.findViewById(R.id.textView_time);
            txt_period = (TextView)rootView.findViewById(R.id.textView_period);
            txt_distance = (TextView)rootView.findViewById(R.id.textView_location);
            txt_comment_count = (TextView)rootView.findViewById(R.id.textView_comment_count);
            txt_like_count = (TextView)rootView.findViewById(R.id.textView_like_count);
            btn_play = (ImageButton)rootView.findViewById(R.id.imageButton_play);
            prog_loading = (ProgressBar) rootView.findViewById(R.id.audio_progress);
            prog_circle = (CircleProgressBar) rootView.findViewById(R.id.custom_progress);
            btn_comment = (ImageButton)rootView.findViewById(R.id.imageButton_comment);
            btn_like = (ImageButton)rootView.findViewById(R.id.imageButton_like);
            btn_dislike = (ImageButton)rootView.findViewById(R.id.imageButton_dislike);
            btn_report = (ImageButton)rootView.findViewById(R.id.imageButton_report);

            relativeLayout_image = (RelativeLayout)rootView.findViewById(R.id.relativeLayout_image);
            img_post = (ImageView)rootView.findViewById(R.id.imageView_image);
            img_overlay = (ImageView)rootView.findViewById(R.id.imageView_overlay);
            avi_indicator = (AVLoadingIndicatorView) rootView.findViewById(R.id.indicator);
            txt_time_image = (TextView)rootView.findViewById(R.id.textView_time_image);
            txt_distance_image = (TextView)rootView.findViewById(R.id.textView_location_image);
            txt_comment_count_image = (TextView)rootView.findViewById(R.id.textView_comment_count_image);
            txt_like_count_image = (TextView)rootView.findViewById(R.id.textView_like_count_image);
            btn_comment_image = (ImageButton)rootView.findViewById(R.id.imageButton_comment_image);
            btn_like_image = (ImageButton)rootView.findViewById(R.id.imageButton_like_image);
            btn_dislike_image = (ImageButton)rootView.findViewById(R.id.imageButton_dislike_image);
            btn_report_image = (ImageButton)rootView.findViewById(R.id.imageButton_report_image);

        }
    }
}
