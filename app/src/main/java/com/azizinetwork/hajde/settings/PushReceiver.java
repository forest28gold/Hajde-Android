package com.azizinetwork.hajde.settings;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.azizinetwork.hajde.R;
import com.azizinetwork.hajde.model.parse.UserData;
import com.backendless.push.BackendlessBroadcastReceiver;


public class PushReceiver extends BackendlessBroadcastReceiver
{
    private final static String TAG = "PushService";

    @Override
    public void onRegistered(Context context, String registrationId )
    {
        Log.d(TAG, "device registered  = " + registrationId);
    }

    @Override
    public void onUnregistered(Context context, Boolean unregistered )
    {
        Log.e(TAG, "device unregistered");
    }

    @Override
    public boolean onMessage( Context context, Intent intent )
    {
        String message = intent.getStringExtra( "message" );
        Log.d(TAG, "Push message received. Message: " + message);

        if (message.contains("post") && message.contains("downvotes")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setPostCount(Global.g_userInfo.getPostCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_POST);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setPostCount(userData.getPostCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_DELETE_POST);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_post_5_downvotes), context);

        } else if (message.contains("comment") && message.contains("downvotes")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setCommentCount(Global.g_userInfo.getCommentCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_COMMENT);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setCommentCount(userData.getCommentCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_DELETE_COMMENT);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_comment_5_downvotes), context);

        } else if (message.contains("post") && message.contains("downvote")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_POST);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_POST);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }

            onShowMessage(context.getString(R.string.karma_someone_downvote_post), context);

        } else if (message.contains("comment") && message.contains("downvote")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_COMMENT);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_COMMENT);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_someone_downvote_comment), context);

        } else if (message.contains("post") && message.contains("report")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setPostCount(Global.g_userInfo.getPostCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_REPORT_DELETE_POST);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setPostCount(userData.getPostCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_REPORT_DELETE_POST);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_delete_post), context);

        } else if (message.contains("comment") && message.contains("report")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setCommentCount(Global.g_userInfo.getCommentCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_REPORT_DELETE_COMMENT);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setCommentCount(userData.getCommentCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_REPORT_DELETE_COMMENT);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_delete_comment), context);
        }

        if (message.contains("postimi") && message.contains("votime")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setPostCount(Global.g_userInfo.getPostCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_POST);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setPostCount(userData.getPostCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_DELETE_POST);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_post_5_downvotes), context);

        } else if (message.contains("komenti") && message.contains("votime")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setCommentCount(Global.g_userInfo.getCommentCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_DELETE_COMMENT);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setCommentCount(userData.getCommentCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_DELETE_COMMENT);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_comment_5_downvotes), context);

        } else if (message.contains("postimi") && message.contains("votimit")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_POST);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_POST);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_someone_downvote_post), context);

        } else if (message.contains("komenti") && message.contains("votimit")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_DECREASE_COMMENT);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DECREASE_POST);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_DECREASE_COMMENT);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_someone_downvote_comment), context);

        } else if (message.contains("postimi") && message.contains("raportimit")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setPostCount(Global.g_userInfo.getPostCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_REPORT_DELETE_POST);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setPostCount(userData.getPostCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_REPORT_DELETE_POST);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_delete_post), context);

        } else if (message.contains("komenti") && message.contains("raportimit")) {

            if (Global.g_userInfo != null) {
                Global.g_userInfo.setKarmaScore(Global.g_userInfo.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                Global.g_userInfo.setCommentCount(Global.g_userInfo.getCommentCount() - 1);
                GlobalSharedData.updateUserDBData();
                Utility.karmaDecreaseInBackground(Global.KARMA_REPORT_DELETE_COMMENT);
            } else {
                DBService dbService = new DBService(context);
                UserData userData = onGetPushUserDBData(dbService);
                if (userData != null) {
                    userData.setKarmaScore(userData.getKarmaScore() + Global.KARMA_SCORE_DELETE_POST);
                    userData.setCommentCount(userData.getCommentCount() - 1);
                    updatePushUserDBData(dbService, userData);

                    intent = new Intent(context, KarmaService.class);
                    intent.putExtra("type", Global.KARMA_REPORT_DELETE_COMMENT);
                    intent.putExtra("userID", userData.getUserID());
                    context.startService(intent);
                }
            }
            onShowMessage(context.getString(R.string.karma_delete_comment), context);
        }

        return true;
    }

    @Override
    public void onError( Context context, String message )
    {
        Log.e(TAG, "Push message error:  " + message);
    }

    public UserData onGetPushUserDBData(DBService dbService) {

        UserData userData = new UserData();

        String sql = "select * from " + Global.LOCAL_TABLE_USER;
        Cursor cursor = dbService.query(sql, null);
        if (cursor != null) {
            int nums = cursor.getCount();
            if (nums > 0) {
                cursor.moveToFirst();

                userData.setUserID(cursor.getString(1));
                userData.setDeviceUUID(cursor.getString(2));
                userData.setPassword(cursor.getString(3));
                userData.setSignup(cursor.getString(4));
                userData.setLastLogin(cursor.getString(5));
                userData.setLanguage(cursor.getString(6));
                userData.setCountry(cursor.getString(7));
                userData.setSpentTime(Integer.parseInt(cursor.getString(8)));
                userData.setPostCount(Integer.parseInt(cursor.getString(9)));
                userData.setCommentCount(Integer.parseInt(cursor.getString(10)));
                userData.setVoteCount(Integer.parseInt(cursor.getString(11)));
                userData.setKarmaScore(Integer.parseInt(cursor.getString(12)));
                userData.setDailyLoginCount(Integer.parseInt(cursor.getString(13)));
                cursor.close();
                return userData;
            }
        }

        return null;
    }

    public void updatePushUserDBData(DBService dbService, UserData userData) {

        String sql_setting = "select * from " + Global.LOCAL_TABLE_USER;
        Cursor cursor = dbService.query(sql_setting, null);
        if (cursor != null) {
            int nums = cursor.getCount();
            if (nums > 0) {
                String sql = "delete from " + Global.LOCAL_TABLE_USER;
                Object[] args = new Object[] {};
                dbService.execSQL(sql, args);

                insertPushUserDBData(dbService, userData);
            } else {
                insertPushUserDBData(dbService, userData);
            }
        }
    }

    public void insertPushUserDBData(DBService dbService, UserData userData) {

        String sql_user = "insert into " + Global.LOCAL_TABLE_USER + " ("
                + Global.LOCAL_FIELD_USER_ID + ", "
                + Global.LOCAL_FIELD_DEVICE_UUID + ", "
                + Global.LOCAL_FIELD_PASSWORD + ", "
                + Global.LOCAL_FIELD_SIGNUP + ", "
                + Global.LOCAL_FIELD_LAST_LOGIN + ", "
                + Global.LOCAL_FIELD_LANGUAGE + ", "
                + Global.LOCAL_FIELD_COUNTRY + ", "
                + Global.LOCAL_FIELD_SPENT_TIME + ", "
                + Global.LOCAL_FIELD_POST_COUNT + ", "
                + Global.LOCAL_FIELD_COMMENT_COUNT + ", "
                + Global.LOCAL_FIELD_VOTE_COUNT + ", "
                + Global.LOCAL_FIELD_KARMA_SCORE + ", "
                + Global.LOCAL_FIELD_LOGIN_COUNT
                + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] args_user = new Object[] { userData.getUserID(), userData.getDeviceUUID(),
                userData.getPassword(), userData.getSignup(), userData.getLastLogin(),
                userData.getLanguage(), userData.getCountry(),
                String.valueOf(userData.getSpentTime()), String.valueOf(userData.getPostCount()),
                String.valueOf(userData.getCommentCount()), String.valueOf(userData.getVoteCount()),
                String.valueOf(userData.getKarmaScore()), String.valueOf(userData.getDailyLoginCount())};
        dbService.execSQL(sql_user, args_user);
    }

    public void onShowMessage(String message, Context context) {

//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View toastView = toast.getView();
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(14);
        toastMessage.setTextColor(Color.BLACK);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setPadding(20, 10, 20, 10);
        toastView.setBackgroundColor(Color.WHITE);
        toast.show();
    }
}