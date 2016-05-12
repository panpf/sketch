package me.xiaopan.sketch.util;

import android.util.Log;

public class Stopwatch {
    private static Stopwatch instance;
    private long startTime;
    private long lastTime;
    private String name;
    private StringBuilder builder;
    private String logTag;

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

    public void start(String logTag, String name) {
        this.logTag = logTag;
        this.name = name;
        startTime = System.currentTimeMillis();
        lastTime = startTime;
        builder = new StringBuilder();
    }

    public void record(String nodeName) {
        if (builder != null) {
            long currentTime = System.currentTimeMillis();
            long useTime = currentTime - lastTime;
            lastTime = currentTime;

            if (builder.length() == 0 && name != null) {
                builder.append(name).append(": ");
            } else {
                builder.append(", ");
            }

            builder.append(nodeName).append(":").append(useTime).append("ms");
        }
    }

    public void print() {
        if (builder != null) {
            long totalTime = System.currentTimeMillis() - startTime;

            if (builder.length() == 0 && name != null) {
                builder.append(name).append(": ");
            } else {
                builder.append(". ");
            }

            builder.append("total of ").append(totalTime).append("ms");
            Log.d(logTag, builder.toString());
            builder = null;
        }
    }
}
