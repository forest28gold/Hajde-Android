package com.azizinetwork.hajde.settings;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.azizinetwork.hajde.model.backend.Karma;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class KarmaService extends Service {

    private static final String TAG = "KarmaService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "KramaService Stopped!!!");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "KramaService create!!!");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Backendless.initApp( this, Global.BACKEND_APP_ID, Global.BACKEND_SECRET_KEY, Global.BACKEND_VERSION_NUM );

        String userID = intent.getStringExtra("userID");
        String type = intent.getStringExtra("type");

        Karma karma = new Karma();
        karma.setUserID(userID);
        karma.setTime(getPushCurrentDate());
        karma.setType(type);

        if (type.equals(Global.KARMA_DECREASE_POST) || type.equals(Global.KARMA_DECREASE_COMMENT)) {
            karma.setBackColor(Global.COLOR4);
            karma.setScore(Global.KARMA_SCORE_DECREASE_POST);
        } else if (type.equals(Global.KARMA_DECREASE_DELETE_POST) || type.equals(Global.KARMA_DECREASE_DELETE_COMMENT)) {
            karma.setBackColor(Global.COLOR4);
            karma.setScore(Global.KARMA_SCORE_DELETE_POST);
        } else if (type.equals(Global.KARMA_REPORT_DELETE_POST) || type.equals(Global.KARMA_REPORT_DELETE_COMMENT)) {
            karma.setBackColor(Global.COLOR7);
            karma.setScore(Global.KARMA_SCORE_DELETE_POST);
        }

        //------------------------------
        Backendless.Persistence.save(karma, new AsyncCallback<Karma>() {
            @Override
            public void handleResponse(Karma response) {
                Log.d(TAG, "Saved karma data");
                onDestroy();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, String.format("Failed save in background of karma data, = %s", fault.toString()));
                onDestroy();
            }
        });
        //------------------------------
    }

    public String getPushCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(Calendar.getInstance().getTime());
    }
}
