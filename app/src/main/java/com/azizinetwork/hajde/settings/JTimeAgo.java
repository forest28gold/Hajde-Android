package com.azizinetwork.hajde.settings;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class JTimeAgo extends Date {

    private Locale locale;

    public JTimeAgo() {
        locale = new Locale("en", "US");
    }

    public JTimeAgo(Date date) {
        this();
        this.setTime(date.getTime());
    }

    /**
     * Returns simple string
     * "1m" , "2h" , "3d" etc
     *
     * @return - present time
     */
    public String getTimeAgoSimple() {
        Date now = Calendar.getInstance().getTime();
        long deltaMillis = intervalInMillis(this, now);
        long deltaSeconds = TimeUnit.MILLISECONDS.toSeconds(deltaMillis);
        long deltaMinutes = TimeUnit.MILLISECONDS.toMinutes(deltaMillis);
        if (deltaSeconds <= 0) {
            return "just now";
        } else if (deltaSeconds < 60) {
            return String.format("%ds", deltaSeconds);
        } else if (deltaMinutes < 60) {
            return String.format("%dm", deltaMinutes);
        } else if (deltaMinutes < (24 * 60)) {
            return String.format("%dh", TimeUnit.MINUTES.toHours(deltaMinutes));
        } else if (deltaMinutes < (24 * 60 * 7)) {
            return String.format("%dd", deltaMinutes / (60 * 24));
        } else if (deltaMinutes < (24 * 60 * 31)) {
            return String.format("%dw", deltaMinutes / (60 * 24 * 7));
        } else if (deltaMinutes < (24 * 60 * 365.25)) {
            return String.format("%dmo", deltaMinutes / (60 * 24 * 30));
        }
        return String.format("%dyr", deltaMinutes / (60 * 24 * 365));
    }


    /**
     * Returns more complex string like "a minute ago" , "a second ago"
     *
     * @return
     */
    public String getTimeAgo() {
        Date now = Calendar.getInstance().getTime();
        long deltaMillis = intervalInMillis(this, now);
        long deltaSeconds = TimeUnit.MILLISECONDS.toSeconds(deltaMillis);
        long deltaMinutes = TimeUnit.MILLISECONDS.toMinutes(deltaMillis);
        if (deltaSeconds < 5) {
            return "justNow";
        } else if (deltaSeconds < 60) {
            return String.format("%d %s %s", deltaSeconds, "seconds", "ago");
        } else if (deltaSeconds < 120) {
            return "minute" + " " + "ago";
        } else if (deltaMinutes < 60) {
            return String.format("%d %s %s", deltaMinutes, "minutes", "ago");
        } else if (deltaMinutes < 120) {
            return "hour" + " " + "ago";
        } else if (deltaMinutes < (24 * 60)) {
            return String.format("%d %s %s", deltaMinutes / 60, "hours", "ago");
        } else if (deltaMinutes < (24 * 60 * 2)) {
            return "yesterday";
        } else if (deltaMinutes < (24 * 60 * 7)) {
            return String.format("%d %s %s", deltaMinutes / (60 * 24), "days", "ago");
        } else if (deltaMinutes < (24 * 60 * 14)) {
            return "lastWeek";
        } else if (deltaMinutes < (24 * 60 * 31)) {
            return String.format("%d %s %s", deltaMinutes / (60 * 24 * 7), "weeks", "ago");
        } else if (deltaMinutes < (24 * 60 * 61)) {
            return "lastMonth";
        } else if (deltaMinutes < (24 * 60 * 365.25)) {
            return String.format("%d %s %s", deltaMinutes / (60 * 24 * 30), "months", "ago");
        } else if (deltaMinutes < (24 * 60 * 731)) {
            return "lastYear";
        }
        return String.format("%d %s %s", deltaMinutes / (60 * 24 * 365), "years", "ago");
    }


    private long intervalInMillis(Date first, Date second) {
        return Math.abs(first.getTime() - second.getTime());
    }
}
