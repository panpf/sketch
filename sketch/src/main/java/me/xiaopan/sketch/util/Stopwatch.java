package me.xiaopan.sketch.util;

import android.util.Log;

import java.text.DecimalFormat;

public class Stopwatch {
    private static Stopwatch instance;
    private long startTime;
    private long lastTime;
    private long decodeCount;
    private long useTimeCount;
    private StringBuilder builder;
    private String logTag;
    private String logName;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static Stopwatch with() {
        if (instance == null) {
            synchronized (Stopwatch.class) {
                if (instance == null) {
                    instance = new Stopwatch();
                }
            }
        }
        return instance;
    }

    public void start(String logTag, String logName) {
        this.logTag = logTag;
        this.logName = logName;
        startTime = System.currentTimeMillis();
        lastTime = startTime;
        builder = new StringBuilder();
    }

    public void record(String nodeName) {
        if (builder != null) {
            long currentTime = System.currentTimeMillis();
            long useTime = currentTime - lastTime;
            lastTime = currentTime;

            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(nodeName).append(":").append(useTime).append("ms");
        }
    }

    public void print(String requestId) {
        if (builder != null) {
            long totalTime = System.currentTimeMillis() - startTime;

            if (builder.length() > 0) {
                builder.append(". ");
            }

            builder.append("useTime").append("=").append(totalTime).append("ms");

            if ((Long.MAX_VALUE - decodeCount) < 1 || (Long.MAX_VALUE - useTimeCount) < totalTime) {
                decodeCount = 0;
                useTimeCount = 0;
            }
            decodeCount++;
            useTimeCount += totalTime;

            Log.d(logTag, SketchUtils.concat(logName,
                    " - ", builder.toString(),
                    ", ", "average", "=", decimalFormat.format((double) useTimeCount / decodeCount), "ms",
                    " - ", requestId));
            builder = null;
        }
    }
}
