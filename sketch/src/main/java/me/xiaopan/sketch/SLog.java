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
 * Sketch 日志
 */
public class SLog {
    private static final String TAG = "Sketch";

    static Proxy proxy = new ProxyImpl();

    /**
     * 设置日志代理，你可以借此自定义日志的输出方式
     *
     * @param proxy null: 恢复为默认的日志代理
     */
    public static void setProxy(Proxy proxy) {
        if (SLog.proxy != proxy) {
            SLog.proxy.onReplaced();
            SLog.proxy = proxy != null ? proxy : new ProxyImpl();
        }
    }

    private static String transformLog(String name, String format, Object... args) {
        if (format == null) {
            format = "";
        }

        if (args != null && args.length > 0) {
            if (!TextUtils.isEmpty(name)) {
                return name + ". " + String.format(format, args);
            } else {
                return String.format(format, args);
            }
        } else {
            // args 为空说明 format 就是日志
            if (!TextUtils.isEmpty(name)) {
                return name + ". " + format;
            } else {
                return format;
            }
        }
    }

    // TODO: 2017/8/25 加入 level 控制，所有的日志点 都要过滤level，并且梳理所有日志，选择合适的类型
    // TODO: 2017/8/25 梳理日志方法，省略所有tag，name 不再绑定到tag上，name 改叫 scope
    // TODO: 2017/8/27 所有的w和e日志不加过滤条件
    // TODO: 2017/8/27 SLogType 挪进 SLog 并简化
    // TODO: 2017/8/27 大量 ImageZoomer 中的 not working 应该属于 debug 日志，得加过滤

    public static int v(String name, String format, Object... args) {
        return proxy.v(TAG, transformLog(name, format, args));
    }

    public static int v(String name, String msg) {
        return proxy.v(TAG, transformLog(name, msg, (Object[]) null));
    }


    public static int d(String name, String format, Object... args) {
        return proxy.d(TAG, transformLog(name, format, args));
    }

    public static int d(String name, String msg) {
        return proxy.d(TAG, transformLog(name, msg, (Object[]) null));
    }


    public static int i(String name, String format, Object... args) {
        return proxy.i(TAG, transformLog(name, format, args));
    }

    public static int i(String name, String msg) {
        return proxy.i(TAG, transformLog(name, msg, (Object[]) null));
    }


    public static int w(String name, String format, Object... args) {
        return proxy.w(TAG, transformLog(name, format, args));
    }

    public static int w(String name, String msg) {
        return proxy.w(TAG, transformLog(name, msg, (Object[]) null));
    }


    public static int e(String name, String format, Object... args) {
        return proxy.e(TAG, transformLog(name, format, args));
    }

    public static int e(String name, String msg) {
        return proxy.e(TAG, transformLog(name, msg, (Object[]) null));
    }

    /**
     * Log 代理器，你可以借此自定义日志的输出方式
     */
    public interface Proxy {
        int v(String tag, String msg);

        int v(String tag, String msg, Throwable tr);

        int d(String tag, String msg);

        int d(String tag, String msg, Throwable tr);

        int i(String tag, String msg);

        int i(String tag, String msg, Throwable tr);

        int w(String tag, String msg);

        int w(String tag, String msg, Throwable tr);

        int w(String tag, Throwable tr);

        int e(String tag, String msg);

        int e(String tag, String msg, Throwable tr);

        void onReplaced();
    }

    private static class ProxyImpl implements Proxy {

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
