package com.azizinetwork.hajde.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.more.PrivacyPolicyActivity;
import com.azizinetwork.hajde.more.TermsOfUseActivity;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.azizinetwork.hajde.settings.Utility;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.wevey.selector.dialog.NormalAlertDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class GetStartActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private final static String TAG = "GetStartActivity";
    Locale myLocale;
    private MediaPlayer mp;
    private String deviceID = "";
    private SVProgressHUD mSVProgressHUD;
    private NormalAlertDialog dialog;
    private boolean signup_isOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstart);
        Log.d(TAG, "onCreate");

        signup_isOn = false;

////        Timestamp timestamp = 1478309871000;
////
//        try{
//            Calendar calendar = Calendar.getInstance();
//            TimeZone tz = TimeZone.getDefault();
//            calendar.setTimeInMillis(Long.parseLong("4133811600000"));
//            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date currenTimeZone = (Date) calendar.getTime();
//            Log.d(TAG, "============ Expire Date =========" + sdf.format(currenTimeZone));
//        }catch (Exception e) {
//        }

        String dtStart = "2100-12-30 09:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dtStart);
        } catch (ParseException e) {
        }

        List<String> channelList = new ArrayList<>();
        channelList.add(Global.MESSAGE_CHANNEL);

        Backendless.Messaging.registerDevice(Global.GCM_SENDER_ID, channelList, date);
//        Backendless.Messaging.registerDevice(Global.GCM_SENDER_ID, Global.MESSAGE_CHANNEL);
        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                deviceID = response.getDeviceId();
                Log.d(TAG, String.format("registered device id = %s", deviceID));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                deviceID = "";
                Log.e(TAG, String.format("register of device is failed. = %s", fault.toString()));
            }
        });

        mSVProgressHUD = new SVProgressHUD(this);

        ((SurfaceView) findViewById(R.id.surfaceView)).getHolder().addCallback(this);

        ImageButton btn_getStarted = (ImageButton)findViewById(R.id.imageButton_getStarted);
        btn_getStarted.setOnClickListener(this);

        Button btn_terms_use = (Button)findViewById(R.id.button_terms_use);
        btn_terms_use.setOnClickListener(this);

        Button btn_privacy_policy = (Button)findViewById(R.id.button_privacy_policy);
        btn_privacy_policy.setOnClickListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        try {
            initPlayer();
        } catch (IOException e) {

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initPlayer() throws IOException {
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_bg);
        mp = new MediaPlayer();
        mp.setDataSource(this, video);
        mp.setDisplay(((SurfaceView) findViewById(R.id.surfaceView)).getHolder());
        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mp.prepare();
        mp.setLooping(true);
        mp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        if (mp.isPlaying()){
            mp.stop();
            mp.release();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButton_getStarted) {
            onGetStarted();
        } else if (view.getId() == R.id.button_terms_use) {
            Intent i = new Intent(GetStartActivity.this, TermsOfUseActivity.class);
            startActivity(i);
        } else if (view.getId() == R.id.button_privacy_policy) {
            Intent i = new Intent(GetStartActivity.this, PrivacyPolicyActivity.class);
            startActivity(i);
        }
    }

    public void onGetStarted() {

        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

//        onLoginHajde("42f7630204a0afc3", Global.HAJDE_PASSWORD);

        if (deviceID.equals("")) {

            Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
                @Override
                public void handleResponse(DeviceRegistration response) {
                    deviceID = response.getDeviceId();
                    Log.d(TAG, String.format("registered device id = %s", deviceID));

                    onSignupHajde(deviceID, Global.HAJDE_PASSWORD);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    deviceID = "";
                    Log.e(TAG, String.format("register of device is failed. = %s", fault.toString()));

                    mSVProgressHUD.dismiss();
                    dialog = new NormalAlertDialog.Builder(GetStartActivity.this)
                            .setHeight(0.2f)
                            .setWidth(0.8f)
                            .setTitleVisible(true)
                            .setTitleText(getString(R.string.alert))
                            .setTitleTextColor(R.color.black)
                            .setContentText(getString(R.string.alert_login))
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

        } else {
            onSignupHajde(deviceID, Global.HAJDE_PASSWORD);
        }
    }

    public void onSignupHajde(final String deviceUUID, final String password) {

        BackendlessUser user = new BackendlessUser();
        user.setPassword(password);
        user.setProperty(Global.KEY_DEVICE_UUID, deviceUUID);

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {

                mSVProgressHUD.dismiss();
                Log.d(TAG, String.format("Registered to Hajde with device id = %s", deviceUUID));

                Global.g_userInfo.setUserID(deviceUUID);
                Global.g_userInfo.setDeviceUUID(deviceUUID);
                Global.g_userInfo.setPassword(password);
                Global.g_userInfo.setSignup(Global.USER_LOGIN);

                Global.g_userInfo.setSpentTime(0);
                Global.g_userInfo.setKarmaScore(0);
                Global.g_userInfo.setPostCount(0);
                Global.g_userInfo.setCommentCount(0);
                Global.g_userInfo.setVoteCount(0);
                Global.g_userInfo.setDailyLoginCount(0);
                Global.g_userInfo.setLanguage(Global.LANG_ALB);

                Global.g_userInfo.setLastLogin(user.getProperty("created").toString());

                GlobalSharedData.updateUserDBData();
                onGotoHajde();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                Log.e(TAG, String.format("Register is failed. = %s", fault.toString()));

                signup_isOn = true;
                onLoginHajde(deviceUUID, password);
            }
        });
    }

    public void onLoginHajde(final String deviceUUID, final String password) {

        Backendless.UserService.login(deviceUUID, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {

                mSVProgressHUD.dismiss();
                Log.d(TAG, String.format("Logged In to Hajde with device id = %s", deviceUUID));

                Global.g_userInfo.setUserID(deviceUUID);
                Global.g_userInfo.setDeviceUUID(deviceUUID);
                Global.g_userInfo.setPassword(password);
                Global.g_userInfo.setSignup(Global.USER_LOGIN);

                Global.g_userInfo.setSpentTime(0);
                Global.g_userInfo.setKarmaScore(0);
                Global.g_userInfo.setPostCount(0);
                Global.g_userInfo.setCommentCount(0);
                Global.g_userInfo.setVoteCount(0);
                Global.g_userInfo.setDailyLoginCount(0);

                Global.g_userInfo.setLastLogin(user.getProperty("created").toString());

                if (!signup_isOn) {
                    String currentLoginTime = GlobalSharedData.getCurrentDate();
                    try {
                        if (getDailyLoginIsOn(currentLoginTime, Global.g_userInfo.getLastLogin())) {
                            int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_LOGIN;
                            Global.g_userInfo.setKarmaScore(karmaScore);
                            Utility.karmaInBackground(Global.KARMA_LOGIN);
                        }
                    } catch (ParseException e) {

                    }

                    setLocale(Global.g_userInfo.getLanguage());
                }

                GlobalSharedData.updateUserDBData();
                onGotoHajde();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

                mSVProgressHUD.dismiss();
                Log.e(TAG, String.format("Login is failed. = %s", fault.toString()));

                dialog = new NormalAlertDialog.Builder(GetStartActivity.this)
                        .setHeight(0.2f)
                        .setWidth(0.8f)
                        .setTitleVisible(true)
                        .setTitleText(getString(R.string.alert))
                        .setTitleTextColor(R.color.black)
                        .setContentText(getString(R.string.alert_login))
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

    public void setLocale(String selectedLanguage) {

        if(selectedLanguage.equals(Global.LANG_ALB))
            myLocale = new Locale("sq");
        else
            myLocale = Locale.ENGLISH;

        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        this.getApplicationContext().getResources().updateConfiguration(config, this.getApplicationContext().getResources().getDisplayMetrics());

    }

    public void onGotoHajde() {
        Intent i = new Intent(GetStartActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public boolean getDailyLoginIsOn(String currentLoginTime, String createdTime) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date endDate = simpleDateFormat.parse(currentLoginTime);
        Date startDate = simpleDateFormat.parse(createdTime);

        long diffInMs = endDate.getTime() - startDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        int daySince = (int) (diffInSec / (3600 * 24));

        Log.d(TAG, String.format("Daily Login Count = %d, Last Login Count = %d", daySince, Global.g_userInfo.getDailyLoginCount()));

        if (daySince > Global.g_userInfo.getDailyLoginCount()) {
            Log.d(TAG, "**************** Daily Login is OK ***********************");
            return true;
        } else {
            Log.d(TAG, "================== Daily Login is Bad =========================");
            return false;
        }
    }

}
