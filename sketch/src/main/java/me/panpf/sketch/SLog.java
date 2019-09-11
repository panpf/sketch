/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class SLog {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 4;
    public static final int WARNING = 8;
    public static final int ERROR = 16;
    public static final int NONE = 32;

    public static final String NAME_VERBOSE = "VERBOSE";
    public static final String NAME_DEBUG = "DEBUG";
    public static final String NAME_INFO = "INFO";
    public static final String NAME_WARNING = "WARNING";
    public static final String NAME_ERROR = "ERROR";
    public static final String NAME_NONE = "NONE";

    private static final String TAG = "Sketch";
    private static int level;
    private static Proxy proxy = new DefaultProxy();

    static {
        setLevel(INFO);
    }

    public static void setProxy(@Nullable Proxy proxy) {
        if (SLog.proxy != proxy) {
            SLog.proxy.onReplaced();
            SLog.proxy = proxy != null ? proxy : new DefaultProxy();
        }
    }

    public static boolean isLoggable(@Level int level) {
        return level >= SLog.level;
    }

    @Level
    public static int getLevel() {
        return level;
    }

    public static void setLevel(@Level int level) {
        if (SLog.level != level) {
            String oldLevelName = getLevelName();
            SLog.level = level;
            Log.w(TAG, "SLog. " + String.format("setLevel. %s -> %s", oldLevelName, getLevelName()));
        }
    }

    @NonNull
    public static String getLevelName() {
        switch (level) {
            case VERBOSE:
                return NAME_VERBOSE;
            case DEBUG:
                return NAME_DEBUG;
            case INFO:
                return NAME_INFO;
            case WARNING:
                return NAME_WARNING;
            case ERROR:
                return NAME_ERROR;
            case NONE:
                return NAME_NONE;
            default:
                return "UNKNOWN(" + level + ")";
        }
    }

    private static String joinMessage(@Nullable String module, @NonNull String formatOrLog, @Nullable Object... args) {
        if (TextUtils.isEmpty(formatOrLog)) {
            return "";
        }

        if (args != null && args.length > 0) {
            if (!TextUtils.isEmpty(module)) {
                return module + ". " + String.format(formatOrLog, args);
            } else {
                return String.format(formatOrLog, args);
            }
        } else {
            if (!TextUtils.isEmpty(module)) {
                return module + ". " + formatOrLog;
            } else {
                return formatOrLog;
            }
        }
    }


    public static int v(@NonNull String msg) {
        return isLoggable(VERBOSE) ? proxy.v(TAG, joinMessage(null, msg, (Object[]) null)) : 0;
    }

    public static int vf(@NonNull String format, @NonNull Object... args) {
        return isLoggable(VERBOSE) ? proxy.v(TAG, joinMessage(null, format, args)) : 0;
    }

    public static int vm(@NonNull String module, @NonNull String msg) {
        return isLoggable(VERBOSE) ? proxy.v(TAG, joinMessage(module, msg, (Object[]) null)) : 0;
    }

    public static int vmf(@NonNull String module, @NonNull String format, @NonNull Object... args) {
        return isLoggable(VERBOSE) ? proxy.v(TAG, joinMessage(module, format, args)) : 0;
    }


    public static int d(@NonNull String msg) {
        return isLoggable(DEBUG) ? proxy.d(TAG, joinMessage(null, msg, (Object[]) null)) : 0;
    }

    public static int df(@NonNull String format, @NonNull Object... args) {
        return isLoggable(DEBUG) ? proxy.d(TAG, joinMessage(null, format, args)) : 0;
    }

    public static int dm(@NonNull String module, @NonNull String msg) {
        return isLoggable(DEBUG) ? proxy.d(TAG, joinMessage(module, msg, (Object[]) null)) : 0;
    }

    public static int dmf(@NonNull String module, @NonNull String format, @NonNull Object... args) {
        return isLoggable(DEBUG) ? proxy.d(TAG, joinMessage(module, format, args)) : 0;
    }


    public static int i(@NonNull String msg) {
        return isLoggable(INFO) ? proxy.i(TAG, joinMessage(null, msg, (Object[]) null)) : 0;
    }

    public static int iff(@NonNull String format, @NonNull Object... args) {
        return isLoggable(INFO) ? proxy.i(TAG, joinMessage(null, format, args)) : 0;
    }

    public static int im(@NonNull String module, @NonNull String msg) {
        return isLoggable(INFO) ? proxy.i(TAG, joinMessage(module, msg, (Object[]) null)) : 0;
    }

    public static int imf(@NonNull String module, @NonNull String format, @NonNull Object... args) {
        return isLoggable(INFO) ? proxy.i(TAG, joinMessage(module, format, args)) : 0;
    }


    public static int w(@NonNull String msg) {
        return isLoggable(WARNING) ? proxy.w(TAG, joinMessage(null, msg, (Object[]) null)) : 0;
    }

    public static int wf(@NonNull String format, @NonNull Object... args) {
        return isLoggable(WARNING) ? proxy.w(TAG, joinMessage(null, format, args)) : 0;
    }

    public static int wm(@NonNull String module, @NonNull String msg) {
        return isLoggable(WARNING) ? proxy.w(TAG, joinMessage(module, msg, (Object[]) null)) : 0;
    }

    public static int wmf(@NonNull String module, @NonNull String format, @NonNull Object... args) {
        return isLoggable(WARNING) ? proxy.w(TAG, joinMessage(module, format, args)) : 0;
    }

    public static int wmt(@NonNull String module, @NonNull Throwable tr, @NonNull String msg) {
        return isLoggable(WARNING) ? proxy.w(TAG, joinMessage(module, msg, (Object[]) null), tr) : 0;
    }

    public static int wmtf(@NonNull String module, @NonNull Throwable tr, @NonNull String format, @NonNull Object... args) {
        return isLoggable(WARNING) ? proxy.w(TAG, joinMessage(module, format, args), tr) : 0;
    }


    public static int e(@NonNull String msg) {
        return isLoggable(ERROR) ? proxy.e(TAG, joinMessage(null, msg, (Object[]) null)) : 0;
    }

    public static int ef(@NonNull String format, @NonNull Object... args) {
        return isLoggable(ERROR) ? proxy.e(TAG, joinMessage(null, format, args)) : 0;
    }

    public static int em(@NonNull String module, @NonNull String msg) {
        return isLoggable(ERROR) ? proxy.e(TAG, joinMessage(module, msg, (Object[]) null)) : 0;
    }

    public static int emf(@NonNull String module, @NonNull String format, @NonNull Object... args) {
        return isLoggable(ERROR) ? proxy.e(TAG, joinMessage(module, format, args)) : 0;
    }

    public static int emt(@NonNull String module, @NonNull Throwable tr, @NonNull String msg) {
        return isLoggable(ERROR) ? proxy.e(TAG, joinMessage(module, msg, (Object[]) null), tr) : 0;
    }

    public static int emtf(@NonNull String module, @NonNull Throwable tr, @NonNull String format, @NonNull Object... args) {
        return isLoggable(ERROR) ? proxy.e(TAG, joinMessage(module, format, args), tr) : 0;
    }


    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
    @IntDef({VERBOSE, DEBUG, INFO, WARNING, ERROR, NONE,})
    public @interface Level {
    }

    public interface Proxy {
        int v(@NonNull String tag, @NonNull String msg);

        int v(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr);

        int d(@NonNull String tag, @NonNull String msg);

        int d(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr);

        int i(@NonNull String tag, @NonNull String msg);

        int i(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr);

        int w(@NonNull String tag, @NonNull String msg);

        int w(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr);

        int e(@NonNull String tag, @NonNull String msg);

        int e(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr);

        void onReplaced();
    }

    private static class DefaultProxy implements Proxy {

        @Override
        public int v(@NonNull String tag, @NonNull String msg) {
            return Log.v(tag, msg);
        }

        @Override
        public int v(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) {
            return Log.v(tag, msg, tr);
        }

        @Override
        public int d(@NonNull String tag, @NonNull String msg) {
            return Log.d(tag, msg);
        }

        @Override
        public int d(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) {
            return Log.d(tag, msg, tr);
        }

        @Override
        public int i(@NonNull String tag, @NonNull String msg) {
            return Log.i(tag, msg);
        }

        @Override
        public int i(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) {
            return Log.i(tag, msg, tr);
        }

        @Override
        public int w(@NonNull String tag, @NonNull String msg) {
            return Log.w(tag, msg);
        }

        @Override
        public int w(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) {
            return Log.w(tag, msg, tr);
        }

        @Override
        public int e(@NonNull String tag, @NonNull String msg) {
            return Log.e(tag, msg);
        }

        @Override
        public int e(@NonNull String tag, @NonNull String msg, @Nullable Throwable tr) {
            return Log.e(tag, msg, tr);
        }

        @Override
        public void onReplaced() {
            // do nothing
        }
    }
}
