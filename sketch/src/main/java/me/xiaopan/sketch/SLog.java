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
    private static final String FORMAT_TAG = "%s-%s";
    private static final String FORMAT_MESSAGE_DEFAULT = "%s";

    static SLogProxy proxy = new SLogProxyImpl();

    public static int fv(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = FORMAT_MESSAGE_DEFAULT;
        }

        String msg = String.format(format, args);
        return proxy.v(tag, msg);
    }

    @SuppressWarnings("unused")
    public static int fv(SLogType type, String format, Object... args) {
        return fv(type, null, format, args);
    }

    @SuppressWarnings("unused")
    public static int fv(String name, String format, Object... args) {
        return fv(null, name, format, args);
    }

    @SuppressWarnings("unused")
    public static int fv(String format, Object... args) {
        return fv(null, null, format, args);
    }

    public static int v(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        return proxy.v(tag, msg);
    }

    public static int v(SLogType type, String msg) {
        return v(type, null, msg);
    }

    public static int v(String name, String msg) {
        return v(null, name, msg);
    }

    public static int v(String msg) {
        return v(null, null, msg);
    }


    public static int fi(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = FORMAT_MESSAGE_DEFAULT;
        }

        String msg = String.format(format, args);
        return proxy.i(tag, msg);
    }

    public static int fi(SLogType type, String format, Object... args) {
        return fi(type, null, format, args);
    }

    public static int fi(String name, String format, Object... args) {
        return fi(null, name, format, args);
    }

    public static int fi(String format, Object... args) {
        return fi(null, null, format, args);
    }

    public static int i(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        return proxy.i(tag, msg);
    }

    public static int i(SLogType type, String msg) {
        return i(type, null, msg);
    }

    public static int i(String name, String msg) {
        return i(null, name, msg);
    }

    public static int i(String msg) {
        return i(null, null, msg);
    }


    public static int fd(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = FORMAT_MESSAGE_DEFAULT;
        }

        String msg = String.format(format, args);
        return proxy.d(tag, msg);
    }

    public static int fd(SLogType type, String format, Object... args) {
        return fd(type, null, format, args);
    }

    public static int fd(String name, String format, Object... args) {
        return fd(null, name, format, args);
    }

    public static int fd(String format, Object... args) {
        return fd(null, null, format, args);
    }

    public static int d(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        return proxy.d(tag, msg);
    }

    public static int d(SLogType type, String msg) {
        return d(type, null, msg);
    }

    public static int d(String name, String msg) {
        return d(null, name, msg);
    }

    public static int d(String msg) {
        return d(null, null, msg);
    }


    public static int fw(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = FORMAT_MESSAGE_DEFAULT;
        }

        String msg = String.format(format, args);
        return proxy.w(tag, msg);
    }

    public static int fw(SLogType type, String format, Object... args) {
        return fw(type, null, format, args);
    }

    public static int fw(String name, String format, Object... args) {
        return fw(null, name, format, args);
    }

    public static int fw(String format, Object... args) {
        return fw(null, null, format, args);
    }

    public static int w(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        return proxy.w(tag, msg);
    }

    public static int w(SLogType type, String msg) {
        return w(type, null, msg);
    }

    public static int w(String name, String msg) {
        return w(null, name, msg);
    }

    public static int w(String msg) {
        return w(null, null, msg);
    }


    public static int fe(SLogType type, String name, String format, Object... args) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        if (TextUtils.isEmpty(format)) {
            format = FORMAT_MESSAGE_DEFAULT;
        }

        String msg = String.format(format, args);
        return proxy.e(tag, msg);
    }

    @SuppressWarnings("unused")
    public static int fe(SLogType type, String format, Object... args) {
        return fe(type, null, format, args);
    }

    public static int fe(String name, String format, Object... args) {
        return fe(null, name, format, args);
    }

    @SuppressWarnings("unused")
    public static int fe(String format, Object... args) {
        return fe(null, null, format, args);
    }

    public static int e(SLogType type, String name, String msg) {
        if (type != null && !type.isEnabled()) {
            return 0;
        }

        String tag = Sketch.TAG;
        if (!TextUtils.isEmpty(name)) {
            tag = String.format(FORMAT_TAG, Sketch.TAG, name);
        }

        return proxy.e(tag, msg);
    }

    public static int e(SLogType type, String msg) {
        return e(type, null, msg);
    }

    public static int e(String name, String msg) {
        return e(null, name, msg);
    }

    public static int e(String msg) {
        return e(null, null, msg);
    }

    static class SLogProxyImpl implements SLogProxy {

        @Override
        public int v(String tag, String msg) {
            return Log.v(tag, msg);
        }

        @Override
        public int v(String tag, String msg, Throwable tr) {
            return Log.v(tag, msg, tr);
        }

        @Override
        public int d(String tag, String msg) {
            return Log.d(tag, msg);
        }

        @Override
        public int d(String tag, String msg, Throwable tr) {
            return Log.d(tag, msg, tr);
        }

        @Override
        public int i(String tag, String msg) {
            return Log.i(tag, msg);
        }

        @Override
        public int i(String tag, String msg, Throwable tr) {
            return Log.i(tag, msg, tr);
        }

        @Override
        public int w(String tag, String msg) {
            return Log.w(tag, msg);
        }

        @Override
        public int w(String tag, String msg, Throwable tr) {
            return Log.w(tag, msg, tr);
        }

        @Override
        public int w(String tag, Throwable tr) {
            return Log.w(tag, tr);
        }

        @Override
        public int e(String tag, String msg) {
            return Log.e(tag, msg);
        }

        @Override
        public int e(String tag, String msg, Throwable tr) {
            return Log.e(tag, msg, tr);
        }

        @Override
        public void onReplaced() {
            // do nothing
        }
    }
}
