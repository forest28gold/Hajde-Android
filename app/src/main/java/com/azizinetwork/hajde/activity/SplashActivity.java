package com.azizinetwork.hajde.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.parse.UserData;
import com.azizinetwork.hajde.settings.DBService;
import com.azizinetwork.hajde.settings.Global;
import com.azizinetwork.hajde.settings.GlobalSharedData;
import com.azizinetwork.hajde.settings.LocationService;
import com.azizinetwork.hajde.settings.Utility;
import com.backendless.Backendless;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends Activity {

    private final static String TAG = "SplashActivity";
    Locale myLocale;
    private Dialog diaPermission = null;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertRequestPermissions();
        } else {
            initSettings();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void insertRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Contacts");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Location");
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Microphone");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Phone");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SMS");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale

                if(diaPermission == null){
                    diaPermission = new android.app.Dialog(this, R.style.CustomTheme);
                    diaPermission.setContentView(R.layout.dia_permission);
                    Window drawWin = diaPermission.getWindow();
                    WindowManager.LayoutParams diaParam = drawWin.getAttributes();
                    diaParam.gravity = Gravity.CENTER;
                    drawWin.setAttributes(diaParam);
                    diaPermission.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    LinearLayout linearLayout = (LinearLayout)diaPermission.findViewById(R.id.linearLayout_permission);

                    for (int i = 0; i < permissionsNeeded.size(); i++) {
                        LinearLayout newCell = (LinearLayout) (View.inflate(this, R.layout.row_list_permission, null));
                        ImageView img_perm = (ImageView)newCell.findViewById(R.id.imageView_perm);
                        TextView txt_perm = (TextView)newCell.findViewById(R.id.textView_perm);

                        if (permissionsNeeded.get(i).equals("Camera")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_camera));
                            txt_perm.setText(R.string.perm_camera);
                        } else if (permissionsNeeded.get(i).equals("Contacts")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_contacts));
                            txt_perm.setText(R.string.perm_contacts);
                        } else if (permissionsNeeded.get(i).equals("Location")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_location));
                            txt_perm.setText(R.string.perm_location);
                        } else if (permissionsNeeded.get(i).equals("Microphone")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_microphone));
                            txt_perm.setText(R.string.perm_microphone);
                        } else if (permissionsNeeded.get(i).equals("Phone")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_phone));
                            txt_perm.setText(R.string.perm_phone);
                        } else if (permissionsNeeded.get(i).equals("SMS")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_sms));
                            txt_perm.setText(R.string.perm_sms);
                        } else if (permissionsNeeded.get(i).equals("Storage")) {
                            img_perm.setImageDrawable(getResources().getDrawable(R.mipmap.perm_storage));
                            txt_perm.setText(R.string.perm_storage);
                        }

                        newCell.setTag(i);
                        registerForContextMenu(newCell);
                        linearLayout.addView(newCell);
                    }

                    Button btn_continue = (Button)diaPermission.findViewById(R.id.button_continue);
                    btn_continue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            diaPermission.dismiss();
                            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        }
                    });
                }
                diaPermission.show();
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        } else {
            initSettings();
            return;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                    initSettings();

                } else {
                    // Permission Denied
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        insertRequestPermissions();
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initSettings() {

        Global.dbService = new DBService(this);
        Global.g_userInfo = new UserData();

        Backendless.initApp( this, Global.BACKEND_APP_ID, Global.BACKEND_SECRET_KEY, Global.BACKEND_VERSION_NUM );

        onCheckUserDBData();

        WaitThread waitThread = new WaitThread();
        waitThread.execute();

        if (!isServiceRunning(LocationService.class)){
            Global.g_locationService = new Intent(SplashActivity.this, LocationService.class);
            startService(Global.g_locationService);
        }
    }

    //=========================================================================

    class WaitThread extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            if (Global.g_userInfo.getSignup() != null) {

                if (Global.g_userInfo.getSignup().equals(Global.USER_LOGIN)) {

                    String currentLoginTime = GlobalSharedData.getCurrentDate();
                    try {
                        if (getDailyLoginIsOn(currentLoginTime, Global.g_userInfo.getLastLogin())) {
                            int karmaScore = Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_LOGIN;
                            Global.g_userInfo.setKarmaScore(karmaScore);
                            Utility.karmaInBackground(Global.KARMA_LOGIN);
                            GlobalSharedData.updateUserDBData();
                        }
                    } catch (ParseException e) {

                    }

                    setLocale(Global.g_userInfo.getLanguage());

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Locale current = getResources().getConfiguration().locale;
                    String current_lang = current.toString();
                    if (current_lang.contains("en")) {
                        Global.g_userInfo.setLanguage(Global.LANG_ENG);
                    } else if (current_lang.contains("sq")) {
                        Global.g_userInfo.setLanguage(Global.LANG_ALB);
                    } else {
                        Global.g_userInfo.setLanguage(Global.LANG_ALB);
                    }

                    setLocale(Global.LANG_ALB);

                    Intent intent = new Intent(SplashActivity.this, GetStartActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {

                Locale current = getResources().getConfiguration().locale;
                String current_lang = current.toString();
                if (current_lang.contains("en")) {
                    Global.g_userInfo.setLanguage(Global.LANG_ENG);
                } else if (current_lang.contains("sq")) {
                    Global.g_userInfo.setLanguage(Global.LANG_ALB);
                } else {
                    Global.g_userInfo.setLanguage(Global.LANG_ALB);
                }

                setLocale(Global.LANG_ALB);

                Intent intent = new Intent(SplashActivity.this, GetStartActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }

    public void onCheckUserDBData() {

        String sql = "select * from " + Global.LOCAL_TABLE_USER;
        Cursor cursor = Global.dbService.query(sql, null);
        startManagingCursor(cursor);
        if (cursor != null) {
            int nums = cursor.getCount();
            if (nums > 0) {
                while (cursor.moveToNext()) {
                    Global.g_userInfo.setUserID(cursor.getString(1));
                    Global.g_userInfo.setDeviceUUID(cursor.getString(2));
                    Global.g_userInfo.setPassword(cursor.getString(3));
                    Global.g_userInfo.setSignup(cursor.getString(4));
                    Global.g_userInfo.setLastLogin(cursor.getString(5));
                    Global.g_userInfo.setLanguage(cursor.getString(6));
                    Global.g_userInfo.setCountry(cursor.getString(7));
                    Global.g_userInfo.setSpentTime(Integer.parseInt(cursor.getString(8)));
                    Global.g_userInfo.setPostCount(Integer.parseInt(cursor.getString(9)));
                    Global.g_userInfo.setCommentCount(Integer.parseInt(cursor.getString(10)));
                    Global.g_userInfo.setVoteCount(Integer.parseInt(cursor.getString(11)));
                    Global.g_userInfo.setKarmaScore(Integer.parseInt(cursor.getString(12)));
                    Global.g_userInfo.setDailyLoginCount(Integer.parseInt(cursor.getString(13)));
                }
                stopManagingCursor(cursor);
                cursor.close();
                // dbService.close();
                return;
            }
        }
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

    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
