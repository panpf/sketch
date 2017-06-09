package me.xiaopan.sketchsample.bean;

import android.content.Context;
import android.text.format.Formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoItem {
    public String title;
    public String path;
    public String mimeType;
    public long duration;
    public long date;
    public long size;

    private String tempFormattedSize;
    private String tempFormattedDuration;
    private String tempFormattedDate;

    public String getTempFormattedDate() {
        if (tempFormattedDate == null) {
            tempFormattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(date));
        }
        return tempFormattedDate;
    }

    public String getTempFormattedSize(Context context) {
        if (tempFormattedSize == null) {
            tempFormattedSize = Formatter.formatFileSize(context, size);
        }
        return tempFormattedSize;
    }

    public String getTempFormattedDuration() {
        if (tempFormattedDuration == null) {
            long second = duration / 1000;
            long secondsRemaining = second % 60;
            long minute = second / 60;
            StringBuilder builder = new StringBuilder();
            if (minute <= 0) {
                builder.append("00");
            } else if (minute < 10) {
                builder.append("0" + minute);
            } else {
                builder.append(minute);
            }

            builder.append(":");

            if (secondsRemaining <= 0) {
                builder.append("00");
            } else if (secondsRemaining < 10) {
                builder.append("0" + secondsRemaining);
            } else {
                builder.append(secondsRemaining);
            }
            tempFormattedDuration = builder.toString();
        }
        return tempFormattedDuration;
    }
}
