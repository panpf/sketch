/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch;

import android.text.TextUtils;
import android.util.Log;

/**
 * Sketch日志
 */
public class SLog {
    private static final String TAG_NAME = "%s-%s";
    private static final String DEFAULT_FORMAL = "%s";
    private static SLogTracker logTracker;

    @SuppressWarnings("unused")
    public static SLogTracker getLogTracker() {
        return logTracker;
    }

    @SuppressWarnings("unused")
    public static void setLogTracker(SLogTracker logTracker) {
        if (SLog.logTracker != logTracker) {
            if (SLog.logTracker != null) {
                SLog.logTracker.close();
            }

            SLog.logTracker = logTracker;
        }
    }

    public static void v(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAL;
        }

        String msg = String.format(format, args);
        Log.v(tag, msg);

        if (logTracker != null) {
            logTracker.v(tag, msg);
        }
    }

    public static void v(SLogType type, String format, Object... args) {
        v(type, null, format, args);
    }

    public static void v(String name, String format, Object... args) {
        v(null, name, format, args);
    }

    public static void v(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        Log.v(tag, msg);

        if (logTracker != null) {
            logTracker.v(tag, msg);
        }
    }

    public static void v(SLogType type, String text) {
        v(type, null, text);
    }

    public static void v(String name, String msg) {
        v(null, name, msg);
    }

    public static void v(String text) {
        v(null, null, text);
    }


    public static void i(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAL;
        }

        String msg = String.format(format, args);
        Log.i(tag, msg);

        if (logTracker != null) {
            logTracker.i(tag, msg);
        }
    }

    public static void i(SLogType type, String format, Object... args) {
        i(type, null, format, args);
    }

    public static void i(String name, String format, Object... args) {
        i(null, name, format, args);
    }

    public static void i(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        Log.i(tag, msg);

        if (logTracker != null) {
            logTracker.i(tag, msg);
        }
    }

    public static void i(SLogType type, String text) {
        i(type, null, text);
    }

    public static void i(String name, String msg) {
        i(null, name, msg);
    }

    public static void i(String text) {
        i(null, null, text);
    }


    public static void d(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAL;
        }

        String msg = String.format(format, args);
        Log.d(tag, msg);

        if (logTracker != null) {
            logTracker.d(tag, msg);
        }
    }

    public static void d(SLogType type, String format, Object... args) {
        d(type, null, format, args);
    }

    public static void d(String name, String format, Object... args) {
        d(null, name, format, args);
    }

    public static void d(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        Log.d(tag, msg);

        if (logTracker != null) {
            logTracker.d(tag, msg);
        }
    }

    public static void d(SLogType type, String text) {
        d(type, null, text);
    }

    public static void d(String name, String msg) {
        d(null, name, msg);
    }

    public static void d(String text) {
        d(null, null, text);
    }


    public static void w(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAL;
        }

        String msg = String.format(format, args);
        Log.w(tag, msg);

        if (logTracker != null) {
            logTracker.w(tag, msg);
        }
    }

    public static void w(SLogType type, String format, Object... args) {
        w(type, null, format, args);
    }

    public static void w(String name, String format, Object... args) {
        w(null, name, format, args);
    }

    public static void w(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        Log.w(tag, msg);

        if (logTracker != null) {
            logTracker.w(tag, msg);
        }
    }

    public static void w(SLogType type, String text) {
        w(type, null, text);
    }

    public static void w(String name, String msg) {
        w(null, name, msg);
    }

    public static void w(String text) {
        w(null, null, text);
    }


    public static void e(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = DEFAULT_FORMAL;
        }

        String msg = String.format(format, args);
        Log.e(tag, msg);

        if (logTracker != null) {
            logTracker.e(tag, msg);
        }
    }

    public static void e(SLogType type, String format, Object... args) {
        e(type, null, format, args);
    }

    public static void e(String name, String format, Object... args) {
        e(null, name, format, args);
    }

    public static void e(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(TAG_NAME, Sketch.TAG, name);
        }

        Log.e(tag, msg);

        if (logTracker != null) {
            logTracker.e(tag, msg);
        }
    }

    public static void e(SLogType type, String text) {
        e(type, null, text);
    }

    public static void e(String name, String msg) {
        e(null, name, msg);
    }

    public static void e(String text) {
        e(null, null, text);
    }
}
