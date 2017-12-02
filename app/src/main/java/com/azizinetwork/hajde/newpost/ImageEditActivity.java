package com.azizinetwork.hajde.newpost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.backend.Post;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.azizinetwork.hajde.settings.LinearColorPickerView;
import com.azizinetwork.hajde.settings.Utility;
import com.azizinetwork.hajde.sticker.ImageSticker;
import com.azizinetwork.hajde.sticker.StickerView;
import com.azizinetwork.hajde.sticker.TextSticker;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.vanniktech.emoji.EmojiTextView;
import com.wevey.selector.dialog.NormalAlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

public class ImageEditActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private final static String TAG = "ImageEditActivity";
    private NormalAlertDialog dialog;
    private SVProgressHUD mSVProgressHUD;

    ImageView img_post;
    RelativeLayout relativeLayout_top, relativeLayout_bottom, relativeLayout_middle, relativeLayout_overlay;
    ImageButton btn_close, btn_emoji, btn_text, btn_paint, btn_delete;
    Button btn_save, btn_send;
    CircleImageView img_color;
    LinearColorPickerView mVerticfalLCPV;
    DrawableView drawableView;
    FrameLayout frag_emoji;
    EditText edit_txt;
    StickerView stickerView;
    TextSticker txt_sticker;

    private int bmpCount = 0;
    Typeface typeface;
    private DrawableViewConfig config;
    private String hexColor = "#00DDFF";
    private String strPost = "";
    private boolean toggleEditIsOn, toggleEmojiIsOn, toggleTextIsOn, togglePainIsOn;
    private int screen_height, screen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        mSVProgressHUD = new SVProgressHUD(this);
        typeface = Typeface.createFromAsset(this.getAssets(),"fonts/AdobeGothicStd-Bold.otf");

        img_post = (ImageView)findViewById(R.id.imageView_post);
        relativeLayout_top = (RelativeLayout)findViewById(R.id.relativeLayout_top);
        relativeLayout_bottom = (RelativeLayout)findViewById(R.id.relativeLayout_bottom);
        relativeLayout_middle = (RelativeLayout)findViewById(R.id.relativeLayout_middle);
        relativeLayout_overlay = (RelativeLayout)findViewById(R.id.relativeLayout_overlay);
        btn_close = (ImageButton)findViewById(R.id.imageButton_close);
        btn_emoji = (ImageButton)findViewById(R.id.imageButton_emoji);
        btn_text = (ImageButton)findViewById(R.id.imageButton_text);
        btn_paint = (ImageButton)findViewById(R.id.imageButton_paint);
        btn_save = (Button)findViewById(R.id.button_image_save);
        btn_send = (Button)findViewById(R.id.button_image_send);
        btn_delete = (ImageButton)findViewById(R.id.imageButton_delete);
        img_color = (CircleImageView)findViewById(R.id.imageView_color);
        mVerticfalLCPV = (LinearColorPickerView) findViewById(R.id.lcpv_v);
        drawableView = (DrawableView)findViewById(R.id.paintView);
        edit_txt = (EditText) findViewById(R.id.editText_text);
        edit_txt.setTypeface(typeface);
        frag_emoji = (FrameLayout)findViewById(R.id.emojicons);
        getSupportFragmentManager().beginTransaction().replace(R.id.emojicons, EmojiconsFragment.newInstance(false)).commit();
        stickerView = (StickerView) findViewById(R.id.sticker_view);

        img_post.setImageBitmap(Global.g_bitmap);

        if (Global.g_cameraIsOn) {
            img_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            img_post.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screen_height = displaymetrics.heightPixels;
        screen_width = displaymetrics.widthPixels;

        config = new DrawableViewConfig();
        config.setStrokeColor(Color.parseColor(hexColor));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(9.5f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(screen_height);
        config.setCanvasWidth(screen_width);
        drawableView.setConfig(config);

        txt_sticker = new TextSticker(strPost,60);
        txt_sticker.getmMatrix().postTranslate(screen_width/2-145, screen_height/2-145);
        stickerView.addSticker(txt_sticker);
        txt_sticker.setFocused(false);
        stickerView.setTextColor(Color.parseColor("#ffffff"));

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                img_post.buildDrawingCache();
                Bitmap img_bmap = img_post.getDrawingCache();
                Bitmap bitmap = overlay(img_bmap, getBitmapFromView(stickerView));
                bitmap = overlay(bitmap, drawableView.obtainBitmap());

                saveImageToGallery(getBaseContext(), bitmap);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                int randomID = (int) (900000000 * Math.random()) + 100000000;
                String prefixName = Integer.toString(randomID);
                String fileName = String.format("%s_%s.jpeg", prefixName, (System.currentTimeMillis()/1000));

                img_post.buildDrawingCache();
                Bitmap img_bmap = img_post.getDrawingCache();
                Bitmap bitmap = overlay(img_bmap, getBitmapFromView(stickerView));
                bitmap = overlay(bitmap, drawableView.obtainBitmap());

                Backendless.Files.Android.upload(bitmap, Bitmap.CompressFormat.JPEG, 100, fileName, Global.BACKEND_URL_IMAGE, new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile response) {
                        Log.d(TAG, String.format("image file -> %s", response.getFileURL().toString()));

                        Post post = new Post();
                        post.setType(Global.POST_TYPE_PHOTO);
                        post.setFilePath(response.getFileURL().toString());
                        post.setUserID(Global.g_userInfo.getUserID());
                        post.setLatitude(Float.toString(Global.g_userInfo.getLatitude()));
                        post.setLongitude(Float.toString(Global.g_userInfo.getLongitude()));
                        post.setTime(GlobalSharedData.getCurrentDate());
                        post.setCommentCount(0);
                        post.setLikeCount(0);
                        post.setReportCount(0);
                        post.setCommentType("");
                        post.setLikeType("");
                        post.setReportType("");
                        post.setImgHeight(img_post.getHeight());

                        Backendless.Persistence.save(post, new AsyncCallback<Post>() {
                            @Override
                            public void handleResponse(Post response) {
                                Log.d(TAG, "Saved new image post data");

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
                                Log.d(TAG, String.format("Failed save in background of image post data = %s", fault.toString()));
                                mSVProgressHUD.dismiss();
                                dialog = new NormalAlertDialog.Builder(ImageEditActivity.this)
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

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.d(TAG, String.format("Failed save in background of image post data = %s", fault.toString()));
                        mSVProgressHUD.dismiss();
                        dialog = new NormalAlertDialog.Builder(ImageEditActivity.this)
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

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleEditIsOn) {
                    initSetDrawView();
                } else {
                    finish();
                }
            }
        });

        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleEmojiIsOn) {
                    initSetDrawView();
                } else {
                    toggleEditIsOn = true; toggleEmojiIsOn = true; toggleTextIsOn = false; togglePainIsOn = false;
                    btn_close.setBackgroundResource(R.mipmap.camera_back);
                    btn_emoji.setBackgroundResource(R.mipmap.camera_emoji);
                    btn_text.setBackgroundResource(R.mipmap.camera_text_normal);
                    btn_paint.setBackgroundResource(R.mipmap.camera_edit_normal);

                    img_color.setVisibility(View.INVISIBLE);
                    mVerticfalLCPV.setVisibility(View.INVISIBLE);
                    btn_delete.setVisibility(View.INVISIBLE);
                    relativeLayout_bottom.setVisibility(View.INVISIBLE);
                    relativeLayout_top.setBackgroundColor(getResources().getColor(R.color.transparent_grey));
                    relativeLayout_overlay.setVisibility(View.INVISIBLE);

                    frag_emoji.setVisibility(View.VISIBLE);
                    edit_txt.setVisibility(View.INVISIBLE);
                    drawableView.setEnabled(false);
                }
            }
        });

        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleTextIsOn) {
                    initSetDrawView();
                } else {
                    toggleEditIsOn = true; toggleEmojiIsOn = false; toggleTextIsOn = true; togglePainIsOn = false;
                    btn_close.setBackgroundResource(R.mipmap.camera_back);
                    btn_emoji.setBackgroundResource(R.mipmap.camera_emoji_normal);
                    btn_text.setBackgroundResource(R.mipmap.camera_text);
                    btn_paint.setBackgroundResource(R.mipmap.camera_edit_normal);

                    img_color.setVisibility(View.INVISIBLE);
                    mVerticfalLCPV.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.INVISIBLE);
                    relativeLayout_bottom.setVisibility(View.INVISIBLE);
                    relativeLayout_top.setBackgroundColor(getResources().getColor(R.color.transparent));
                    relativeLayout_overlay.setVisibility(View.VISIBLE);

                    edit_txt.setText(strPost);

                    frag_emoji.setVisibility(View.INVISIBLE);
                    drawableView.setEnabled(false);

                    if (!strPost.equals("")) {
                        edit_txt.setVisibility(View.INVISIBLE);
                        txt_sticker.setFocused(true);
                        stickerView.postInvalidate();
                    } else {
                        edit_txt.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        btn_paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (togglePainIsOn) {
                    initSetDrawView();
                } else {
                    toggleEditIsOn = true; toggleEmojiIsOn = false; toggleTextIsOn = false; togglePainIsOn = true;
                    btn_close.setBackgroundResource(R.mipmap.camera_back);
                    btn_emoji.setBackgroundResource(R.mipmap.camera_emoji_normal);
                    btn_text.setBackgroundResource(R.mipmap.camera_text_normal);
                    btn_paint.setBackgroundResource(R.mipmap.edit_color);

                    img_color.setVisibility(View.VISIBLE);
                    mVerticfalLCPV.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                    relativeLayout_bottom.setVisibility(View.INVISIBLE);
                    relativeLayout_top.setBackgroundColor(getResources().getColor(R.color.transparent));
                    relativeLayout_overlay.setVisibility(View.INVISIBLE);

                    frag_emoji.setVisibility(View.INVISIBLE);
                    edit_txt.setVisibility(View.INVISIBLE);
                    img_color.setColorFilter(Color.parseColor(hexColor));
                    drawableView.setEnabled(true);
                    config.setStrokeColor(Color.parseColor(hexColor));
                    drawableView.setConfig(config);
                }
            }
        });

        mVerticfalLCPV.setOnSelectedColorChangedListener(new LinearColorPickerView.OnSelectedColorChanged() {
            @Override
            public void onSelectedColorChanged(int color) {
                img_color.setColorFilter(color);
                hexColor = String.format("#%06X", (0xFFFFFF & color));

                if (toggleTextIsOn) {
                    stickerView.setTextColor(Color.parseColor(hexColor));
                    stickerView.invalidate();
                } else if (togglePainIsOn) {
                    config.setStrokeColor(Color.parseColor(hexColor));
                    drawableView.setConfig(config);
                }
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (togglePainIsOn) {
                    drawableView.clear();
                } else if (toggleTextIsOn) {
                    stickerView.updateStickerText("");
                }
            }
        });

        relativeLayout_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "============== Touch =================");
                if (toggleTextIsOn && edit_txt.getVisibility() == View.VISIBLE) {
                    Log.v(TAG, "============== Visible Touch =================");
                    edit_txt.setVisibility(View.INVISIBLE);
                    txt_sticker.setFocused(true);
                    boolean stickerIsOn = stickerView.updateStickerText(strPost);
                    if (!stickerIsOn) {
                        txt_sticker = new TextSticker(strPost,60);
                        txt_sticker.getmMatrix().postTranslate(screen_width/2-145, screen_height/2-145);
                        stickerView.addSticker(txt_sticker);
                        txt_sticker.setFocused(true);
                        stickerView.setTextColor(Color.parseColor("#ffffff"));
                    }
                    relativeLayout_overlay.setVisibility(View.INVISIBLE);
                } else if (toggleTextIsOn && edit_txt.getVisibility() == View.INVISIBLE) {
                    Log.v(TAG, "==============Invisible Touch =================");
                    edit_txt.setVisibility(View.VISIBLE);
                    edit_txt.setText(strPost);
                    relativeLayout_overlay.setVisibility(View.VISIBLE);
                }
            }
        });

        edit_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strPost = edit_txt.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        initSetDrawView();
    }

    private void setResultOkSoSecondActivityWontBeShown() {
        Intent intent = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, intent);
        } else {
            getParent().setResult(Activity.RESULT_OK, intent);
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }

    public void initSetDrawView() {

        btn_close.setBackgroundResource(R.mipmap.camera_close);
        btn_emoji.setBackgroundResource(R.mipmap.camera_emoji);
        btn_text.setBackgroundResource(R.mipmap.camera_text);
        btn_paint.setBackgroundResource(R.mipmap.camera_edit);

        toggleEditIsOn = false; toggleEmojiIsOn = false; toggleTextIsOn = false; togglePainIsOn = false;

        img_color.setVisibility(View.INVISIBLE);
        mVerticfalLCPV.setVisibility(View.INVISIBLE);
        btn_delete.setVisibility(View.INVISIBLE);
        relativeLayout_bottom.setVisibility(View.VISIBLE);
        relativeLayout_top.setBackgroundColor(getResources().getColor(R.color.transparent));
        relativeLayout_overlay.setVisibility(View.INVISIBLE);

        hexColor = "#00DDFF";
        img_color.setColorFilter(Color.parseColor(hexColor));
        edit_txt.setText(strPost);

        frag_emoji.setVisibility(View.INVISIBLE);
        edit_txt.setVisibility(View.INVISIBLE);
        drawableView.setEnabled(false);
        txt_sticker.setFocused(false);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        initSetDrawView();

        Bitmap bmp_emoji = getBitmapData(bmpCount, emojicon.getEmoji());

        ImageSticker imageSticker = new ImageSticker(bmp_emoji);
        imageSticker.getmMatrix().postTranslate(screen_width/2 - 145, screen_height/2 - 145);
        stickerView.addSticker(imageSticker);
        bmpCount++;
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
//        EmojiconsFragment.backspace(mEditEmojicon);
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "hajde");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            dialog = new NormalAlertDialog.Builder(ImageEditActivity.this)
                    .setHeight(0.2f)
                    .setWidth(0.8f)
                    .setTitleVisible(true)
                    .setTitleText(getString(R.string.alert))
                    .setTitleTextColor(R.color.black)
                    .setContentText(getString(R.string.save_failed))
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

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            dialog = new NormalAlertDialog.Builder(ImageEditActivity.this)
                    .setHeight(0.2f)
                    .setWidth(0.8f)
                    .setTitleVisible(true)
                    .setTitleText(getString(R.string.alert))
                    .setTitleTextColor(R.color.black)
                    .setContentText(getString(R.string.saved_successfully))
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

        } catch (FileNotFoundException e) {
            dialog = new NormalAlertDialog.Builder(ImageEditActivity.this)
                    .setHeight(0.2f)
                    .setWidth(0.8f)
                    .setTitleVisible(true)
                    .setTitleText(getString(R.string.alert))
                    .setTitleTextColor(R.color.black)
                    .setContentText(getString(R.string.save_failed))
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
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
    }

    public Bitmap getBitmapData(int index, String emoji) {

        Bitmap bitmap = null;

        switch (index) {
            case 0:
                EmojiTextView txt_emoji0 = (EmojiTextView) findViewById(R.id.emojiTextView0);
                txt_emoji0.setText(emoji);
                txt_emoji0.buildDrawingCache();
                bitmap = txt_emoji0.getDrawingCache();
                break;
            case 1:
                EmojiTextView txt_emoji1 = (EmojiTextView) findViewById(R.id.emojiTextView1);
                txt_emoji1.setText(emoji);
                txt_emoji1.buildDrawingCache();
                bitmap = txt_emoji1.getDrawingCache();
                break;
            case 2:
                EmojiTextView txt_emoji2 = (EmojiTextView) findViewById(R.id.emojiTextView2);
                txt_emoji2.setText(emoji);
                txt_emoji2.buildDrawingCache();
                bitmap = txt_emoji2.getDrawingCache();
                break;
            case 3:
                EmojiTextView txt_emoji3 = (EmojiTextView) findViewById(R.id.emojiTextView3);
                txt_emoji3.setText(emoji);
                txt_emoji3.buildDrawingCache();
                bitmap = txt_emoji3.getDrawingCache();
                break;
            case 4:
                EmojiTextView txt_emoji4 = (EmojiTextView) findViewById(R.id.emojiTextView4);
                txt_emoji4.setText(emoji);
                txt_emoji4.buildDrawingCache();
                bitmap = txt_emoji4.getDrawingCache();
                break;
            case 5:
                EmojiTextView txt_emoji5 = (EmojiTextView) findViewById(R.id.emojiTextView5);
                txt_emoji5.setText(emoji);
                txt_emoji5.buildDrawingCache();
                bitmap = txt_emoji5.getDrawingCache();
                break;
            case 6:
                EmojiTextView txt_emoji6 = (EmojiTextView) findViewById(R.id.emojiTextView6);
                txt_emoji6.setText(emoji);
                txt_emoji6.buildDrawingCache();
                bitmap = txt_emoji6.getDrawingCache();
                break;
            case 7:
                EmojiTextView txt_emoji7 = (EmojiTextView) findViewById(R.id.emojiTextView7);
                txt_emoji7.setText(emoji);
                txt_emoji7.buildDrawingCache();
                bitmap = txt_emoji7.getDrawingCache();
                break;
            case 8:
                EmojiTextView txt_emoji8 = (EmojiTextView) findViewById(R.id.emojiTextView8);
                txt_emoji8.setText(emoji);
                txt_emoji8.buildDrawingCache();
                bitmap = txt_emoji8.getDrawingCache();
                break;
            case 9:
                EmojiTextView txt_emoji9 = (EmojiTextView) findViewById(R.id.emojiTextView9);
                txt_emoji9.setText(emoji);
                txt_emoji9.buildDrawingCache();
                bitmap = txt_emoji9.getDrawingCache();
                break;
            case 10:
                EmojiTextView txt_emoji10 = (EmojiTextView) findViewById(R.id.emojiTextView10);
                txt_emoji10.setText(emoji);
                txt_emoji10.buildDrawingCache();
                bitmap = txt_emoji10.getDrawingCache();
                break;
            case 11:
                EmojiTextView txt_emoji11 = (EmojiTextView) findViewById(R.id.emojiTextView11);
                txt_emoji11.setText(emoji);
                txt_emoji11.buildDrawingCache();
                bitmap = txt_emoji11.getDrawingCache();
                break;
            case 12:
                EmojiTextView txt_emoji12 = (EmojiTextView) findViewById(R.id.emojiTextView12);
                txt_emoji12.setText(emoji);
                txt_emoji12.buildDrawingCache();
                bitmap = txt_emoji12.getDrawingCache();
                break;
            case 13:
                EmojiTextView txt_emoji13 = (EmojiTextView) findViewById(R.id.emojiTextView13);
                txt_emoji13.setText(emoji);
                txt_emoji13.buildDrawingCache();
                bitmap = txt_emoji13.getDrawingCache();
                break;
            case 14:
                EmojiTextView txt_emoji14 = (EmojiTextView) findViewById(R.id.emojiTextView14);
                txt_emoji14.setText(emoji);
                txt_emoji14.buildDrawingCache();
                bitmap = txt_emoji14.getDrawingCache();
                break;
            case 15:
                EmojiTextView txt_emoji15 = (EmojiTextView) findViewById(R.id.emojiTextView15);
                txt_emoji15.setText(emoji);
                txt_emoji15.buildDrawingCache();
                bitmap = txt_emoji15.getDrawingCache();
                break;
            case 16:
                EmojiTextView txt_emoji16 = (EmojiTextView) findViewById(R.id.emojiTextView16);
                txt_emoji16.setText(emoji);
                txt_emoji16.buildDrawingCache();
                bitmap = txt_emoji16.getDrawingCache();
                break;
            case 17:
                EmojiTextView txt_emoji17 = (EmojiTextView) findViewById(R.id.emojiTextView17);
                txt_emoji17.setText(emoji);
                txt_emoji17.buildDrawingCache();
                bitmap = txt_emoji17.getDrawingCache();
                break;
            case 18:
                EmojiTextView txt_emoji18 = (EmojiTextView) findViewById(R.id.emojiTextView18);
                txt_emoji18.setText(emoji);
                txt_emoji18.buildDrawingCache();
                bitmap = txt_emoji18.getDrawingCache();
                break;
            case 19:
                EmojiTextView txt_emoji19 = (EmojiTextView) findViewById(R.id.emojiTextView19);
                txt_emoji19.setText(emoji);
                txt_emoji19.buildDrawingCache();
                bitmap = txt_emoji19.getDrawingCache();
                break;
            case 20:
                EmojiTextView txt_emoji20 = (EmojiTextView) findViewById(R.id.emojiTextView20);
                txt_emoji20.setText(emoji);
                txt_emoji20.buildDrawingCache();
                bitmap = txt_emoji20.getDrawingCache();
                break;
            case 21:
                EmojiTextView txt_emoji21 = (EmojiTextView) findViewById(R.id.emojiTextView21);
                txt_emoji21.setText(emoji);
                txt_emoji21.buildDrawingCache();
                bitmap = txt_emoji21.getDrawingCache();
                break;
            case 22:
                EmojiTextView txt_emoji22 = (EmojiTextView) findViewById(R.id.emojiTextView22);
                txt_emoji22.setText(emoji);
                txt_emoji22.buildDrawingCache();
                bitmap = txt_emoji22.getDrawingCache();
                break;
            case 23:
                EmojiTextView txt_emoji23 = (EmojiTextView) findViewById(R.id.emojiTextView23);
                txt_emoji23.setText(emoji);
                txt_emoji23.buildDrawingCache();
                bitmap = txt_emoji23.getDrawingCache();
                break;
            case 24:
                EmojiTextView txt_emoji24 = (EmojiTextView) findViewById(R.id.emojiTextView24);
                txt_emoji24.setText(emoji);
                txt_emoji24.buildDrawingCache();
                bitmap = txt_emoji24.getDrawingCache();
                break;
            case 25:
                EmojiTextView txt_emoji25 = (EmojiTextView) findViewById(R.id.emojiTextView25);
                txt_emoji25.setText(emoji);
                txt_emoji25.buildDrawingCache();
                bitmap = txt_emoji25.getDrawingCache();
                break;
            case 26:
                EmojiTextView txt_emoji26 = (EmojiTextView) findViewById(R.id.emojiTextView26);
                txt_emoji26.setText(emoji);
                txt_emoji26.buildDrawingCache();
                bitmap = txt_emoji26.getDrawingCache();
                break;
            case 27:
                EmojiTextView txt_emoji27 = (EmojiTextView) findViewById(R.id.emojiTextView27);
                txt_emoji27.setText(emoji);
                txt_emoji27.buildDrawingCache();
                bitmap = txt_emoji27.getDrawingCache();
                break;
            case 28:
                EmojiTextView txt_emoji28 = (EmojiTextView) findViewById(R.id.emojiTextView28);
                txt_emoji28.setText(emoji);
                txt_emoji28.buildDrawingCache();
                bitmap = txt_emoji28.getDrawingCache();
                break;
            case 29:
                EmojiTextView txt_emoji29 = (EmojiTextView) findViewById(R.id.emojiTextView29);
                txt_emoji29.setText(emoji);
                txt_emoji29.buildDrawingCache();
                bitmap = txt_emoji29.getDrawingCache();
                break;
            case 30:
                EmojiTextView txt_emoji30 = (EmojiTextView) findViewById(R.id.emojiTextView30);
                txt_emoji30.setText(emoji);
                txt_emoji30.buildDrawingCache();
                bitmap = txt_emoji30.getDrawingCache();
                break;
            default:
                EmojiTextView txt_emoji = (EmojiTextView) findViewById(R.id.emojiTextView);
                txt_emoji.setText(emoji);
                txt_emoji.buildDrawingCache();
                bitmap = txt_emoji.getDrawingCache();
                break;
        }

        return bitmap;
    }

}
