package com.azizinetwork.hajde.settings;

import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalSharedData implements Serializable {
    private static final long serialVersionUID = 1L;

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,8}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(Calendar.getInstance().getTime());
    }

    public static String getFormattedCount(int count) {
        String strCount = "0";
        if (count < 1000) {
            strCount = String.valueOf(count);
        } else if (count >= 1000 && count < 1000000) {
            float count_dec = count / 1000;
            strCount = String.format("%.0fK",count_dec);
        } else {
            float count_dec = count / 1000000;
            strCount = String.format("%.0fM",count_dec);
        }
        return strCount;
    }

    public static String getFormattedTimeStamp(String time) {

        String timeformat = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(time);
        } catch (ParseException e) {

        }

        JTimeAgo jTimeAgo = new JTimeAgo(convertedDate);
        timeformat = jTimeAgo.getTimeAgoSimple();

        return timeformat;
    }

    public static void getSpentTimeStamp(int spentTime) {

        Global.g_spentDays = spentTime / (3600 * 24);
        Global.g_spentHours = spentTime / 3600;
        Global.g_spentMins = (spentTime % 3600) / 60;
        int second = spentTime % 60;

//        Log.e("Spent Time", String.format("=================== %d:%d:%d:%d ====================", Global.g_spentDays, Global.g_spentHours, Global.g_spentMins, second));
    }

    public static String getFormattedDistance(String from_latitude, String from_longitude, String to_latitude, String to_longitude) {

        String strDistance = "";

        Location from_location = new Location("From Location");
        from_location.setLatitude(Float.parseFloat(from_latitude));
        from_location.setLongitude(Float.parseFloat(from_longitude));

        Location to_locations=new Location("To Location");
        to_locations.setLatitude(Float.parseFloat(to_latitude));
        to_locations.setLongitude(Float.parseFloat(to_longitude));

        double distance = from_location.distanceTo(to_locations);

        if (distance < 1000) {
            strDistance = String.format("%.0fm", distance);
        } else {
            strDistance = String.format("%.0fkm", distance/1000);
        }
        return strDistance;
    }

    public static int getDistance(String from_latitude, String from_longitude, String to_latitude, String to_longitude) {

        Location from_location = new Location("From Location");
        from_location.setLatitude(Float.parseFloat(from_latitude));
        from_location.setLongitude(Float.parseFloat(from_longitude));

        Location to_locations=new Location("To Location");
        to_locations.setLatitude(Float.parseFloat(to_latitude));
        to_locations.setLongitude(Float.parseFloat(to_longitude));

        double distance = from_location.distanceTo(to_locations);

        int kilometers = (int) (distance / 1000);

        return kilometers;
    }

    public static void updateUserDBData() {

        String sql_setting = "select * from " + Global.LOCAL_TABLE_USER;
        Cursor cursor = Global.dbService.query(sql_setting, null);
        if (cursor != null) {
            int nums = cursor.getCount();
            if (nums > 0) {
                String sql = "delete from " + Global.LOCAL_TABLE_USER;
                Object[] args = new Object[] {};
                Global.dbService.execSQL(sql, args);

                insertUserDBData();
            } else {
                insertUserDBData();
            }
        }
    }

    public static void insertUserDBData() {

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
        Object[] args_user = new Object[] { Global.g_userInfo.getUserID(), Global.g_userInfo.getDeviceUUID(),
                Global.g_userInfo.getPassword(), Global.g_userInfo.getSignup(), Global.g_userInfo.getLastLogin(),
                Global.g_userInfo.getLanguage(), Global.g_userInfo.getCountry(),
                String.valueOf(Global.g_userInfo.getSpentTime()), String.valueOf(Global.g_userInfo.getPostCount()),
                String.valueOf(Global.g_userInfo.getCommentCount()), String.valueOf(Global.g_userInfo.getVoteCount()),
                String.valueOf(Global.g_userInfo.getKarmaScore()), String.valueOf(Global.g_userInfo.getDailyLoginCount())};
        Global.dbService.execSQL(sql_user, args_user);
    }

}
