/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

import android.annotation.SuppressLint;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sketch 日志，分为两个维度来控制日志的开关，一个级别，一个是类型，级别之间单选互斥，类型之间多选共存
 */
public class SLog {

    /**
     * 日志级别 - 最详细的
     */
    public static final int LEVEL_VERBOSE = 0x01;

    /**
     * 日志级别 - 输出 debug 以上日志
     */
    public static final int LEVEL_DEBUG = 0x01 << 1;

    /**
     * 日志级别 - 输出 info 以上日志
     */
    public static final int LEVEL_INFO = 0x01 << 2;

    /**
     * 日志级别 - 输出 warning 以上日志
     */
    public static final int LEVEL_WARNING = 0x01 << 3;

    /**
     * 日志级别 - 输出 error 以上日志
     */
    public static final int LEVEL_ERROR = 0x01 << 4;

    /**
     * 日志级别 - 啥日志也不输出
     */
    public static final int LEVEL_NONE = 0x01 << 5;

    /**
     * 日志类型 - 执行流程相关日志
     */
    public static final int TYPE_FLOW = 0x01 << 16;

    /**
     * 日志类型 - 内存缓存、bitmap pool、磁盘缓存相关日志
     */
    public static final int TYPE_CACHE = 0x01 << 17;

    /**
     * 日志类型 - commit()和解码耗时相关日志
     */
    public static final int TYPE_TIME = 0x01 << 18;

    /**
     * 日志类型 - 缩放相关日志
     */
    public static final int TYPE_ZOOM = 0x01 << 19;

    /**
     * 日志类型 - 缩放中分块显示相关日志
     */
    public static final int TYPE_ZOOM_BLOCK_DISPLAY = 0x01 << 20;

    public static final String LEVEL_NAME_VERBOSE = "VERBOSE";
    public static final String LEVEL_NAME_DEBUG = "DEBUG";
    public static final String LEVEL_NAME_INFO = "INFO";
    public static final String LEVEL_NAME_WARNING = "WARNING";
    public static final String LEVEL_NAME_ERROR = "ERROR";
    public static final String LEVEL_NAME_NONE = "NONE";

    private static final String TAG = "Sketch";
    private static final String NAME = "SLog";
    private static int levelAndTypeFlags;
    private static Proxy proxy = new ProxyImpl();

    static {
        setLevel(LEVEL_INFO);
    }

    /**
     * 设置日志代理，你可以借此自定义日志的输出方式
     *
     * @param proxy null: 恢复为默认的日志代理
     */
    public static void setProxy(@Nullable Proxy proxy) {
        if (SLog.proxy != proxy) {
            SLog.proxy.onReplaced();
            SLog.proxy = proxy != null ? proxy : new ProxyImpl();
        }
    }

    /**
     * 判断指定类型或级别的日志是否可用
     *
     * @param mask 取值范围 {@link #LEVEL_VERBOSE}, {@link #LEVEL_DEBUG}, {@link #LEVEL_INFO},
     *             {@link #LEVEL_WARNING}, {@link #LEVEL_ERROR}, {@link #LEVEL_NONE},
     *             {@link #TYPE_CACHE}, {@link #TYPE_FLOW}, {@link #TYPE_TIME}, {@link #TYPE_ZOOM}, {@link #TYPE_ZOOM_BLOCK_DISPLAY}
     */
    public static boolean isLoggable(int mask) {
        /* 高 16 位，低 16 位分开判断。因为低 16 位是互斥并且区分大小关系，高 16 位是共存关系 */

        int low16BitsMask = 0xFFFF;
        //noinspection NumericOverflow
        int high16BitsMask = 0xFFFF << 16;

        // 取出 mask 的低 16 位并处理一下，因为低 16 是互斥的关系，因此只能保留最高的一个 1
        int maskLow16Bits = low16One(mask & low16BitsMask);
        // 取出 mask 的高 16 位
        int maskHigh16Bits = mask & high16BitsMask;

        // 取出 flag 的低 16 位
        int flagLow16Bits = SLog.levelAndTypeFlags & low16BitsMask;
        // 取出 flag 的高 16 位
        int flagHigh16Bits = SLog.levelAndTypeFlags & high16BitsMask;

        boolean low16BitsCheckResult = maskLow16Bits == 0 || flagLow16Bits != 0 && maskLow16Bits >= flagLow16Bits;
        boolean high16BitsCheckResult = maskHigh16Bits == 0 || flagHigh16Bits != 0 && (flagHigh16Bits & maskHigh16Bits) == maskHigh16Bits;
        return low16BitsCheckResult && high16BitsCheckResult;
    }

    /**
     * 获取日志级别
     *
     * @return 取值范围 {@link #LEVEL_VERBOSE}, {@link #LEVEL_DEBUG}, {@link #LEVEL_INFO},
     * {@link #LEVEL_WARNING}, {@link #LEVEL_ERROR}, {@link #LEVEL_NONE}
     */
    @SuppressLint("WrongConstant")
    @Level
    public static int getLevel() {
        if (isLoggable(LEVEL_VERBOSE)) {
            return LEVEL_VERBOSE;
        } else if (isLoggable(LEVEL_DEBUG)) {
            return LEVEL_DEBUG;
        } else if (isLoggable(LEVEL_INFO)) {
            return LEVEL_INFO;
        } else if (isLoggable(LEVEL_WARNING)) {
            return LEVEL_WARNING;
        } else if (isLoggable(LEVEL_ERROR)) {
            return LEVEL_ERROR;
        } else if (isLoggable(LEVEL_NONE)) {
            return LEVEL_NONE;
        } else {
            return 0;
        }
    }

    /**
     * 设置日志级别
     *
     * @param level 取值范围 {@link #LEVEL_VERBOSE}, {@link #LEVEL_DEBUG}, {@link #LEVEL_INFO},
     *              {@link #LEVEL_WARNING}, {@link #LEVEL_ERROR}, {@link #LEVEL_NONE}
     */
    public static void setLevel(@Level int level) {
        int low16BitsMask = 0xFFFF;
        // noinspection NumericOverflow
        int high16BitsMask = 0xFFFF << 16;

        // 取出 mask 的低 16 位并处理一下，因为低 16 是互斥的关系，因此只能保留最高的一个 1
        int maskLow16Bits = low16One(level & low16BitsMask);

        int newFlag = SLog.levelAndTypeFlags;
        if (maskLow16Bits != 0) {
            // 因为低 16 位是单选互斥关系，所以原 flag 要清空 低 16 位，保留高 16 位，以此作为新 flag 的基础
            int resetLow16BitFlags = newFlag & high16BitsMask;
            // 低 16 位赋值
            newFlag = resetLow16BitFlags | maskLow16Bits;
        }

        String oldLevelName = getLevelName();
        SLog.levelAndTypeFlags = newFlag;
        String newLevelName = getLevelName();

        android.util.Log.w(TAG, String.format("%s. setLevel. %s -> %s", NAME, oldLevelName, newLevelName));
    }

    /**
     * 获取日志级别名称
     */
    public static String getLevelName() {
        if (isLoggable(LEVEL_VERBOSE)) {
            return LEVEL_NAME_VERBOSE;
        } else if (isLoggable(LEVEL_DEBUG)) {
            return LEVEL_NAME_DEBUG;
        } else if (isLoggable(LEVEL_INFO)) {
            return LEVEL_NAME_INFO;
        } else if (isLoggable(LEVEL_WARNING)) {
            return LEVEL_NAME_WARNING;
        } else if (isLoggable(LEVEL_ERROR)) {
            return LEVEL_NAME_ERROR;
        } else if (isLoggable(LEVEL_NONE)) {
            return LEVEL_NAME_NONE;
        } else {
            return "UNKNOWN(" + getLevel() + ")";
        }
    }

    /**
     * 开启指定类型的日志
     *
     * @param type 取值范围 {@link #TYPE_CACHE}, {@link #TYPE_FLOW}, {@link #TYPE_TIME}, {@link #TYPE_ZOOM}, {@link #TYPE_ZOOM_BLOCK_DISPLAY}
     */
    public static void openType(@Type int type) {
        //noinspection NumericOverflow
        int high16BitsMask = 0xFFFF << 16;

        // 取出 mask 的高 16 位
        int maskHigh16Bits = type & high16BitsMask;

        int newFlag = SLog.levelAndTypeFlags;
        if (maskHigh16Bits != 0) {
            newFlag = newFlag | maskHigh16Bits; // 高 16 位赋值
        }

        String oldTypeNames = getTypeNames();
        SLog.levelAndTypeFlags = newFlag;
        String newTypeNames = getTypeNames();

        android.util.Log.w(TAG, String.format("%s. openType: %s -> %s", NAME, oldTypeNames, newTypeNames));
    }

    /**
     * 关闭指定类型的日志
     *
     * @param type 取值范围 {@link #TYPE_CACHE}, {@link #TYPE_FLOW}, {@link #TYPE_TIME}, {@link #TYPE_ZOOM}, {@link #TYPE_ZOOM_BLOCK_DISPLAY}
     */
    public static void closeType(@Type int type) {
        //noinspection NumericOverflow
        int high16BitsMask = 0xFFFF << 16;

        // 取出 mask 的高 16 位
        int maskHigh16Bits = type & high16BitsMask;

        String oldTypeNames = getTypeNames();
        //noinspection WrongConstant
        SLog.levelAndTypeFlags &= ~maskHigh16Bits;
        String newTypeNames = getTypeNames();

        android.util.Log.w(TAG, String.format("%s. closeType: %s -> %s", NAME, oldTypeNames, newTypeNames));
    }

    public static String getTypeNames() {
        StringBuilder builder = new StringBuilder();
        if (isLoggable(TYPE_FLOW)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("FLOW");
        }
        if (isLoggable(TYPE_CACHE)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("CACHE");
        }
        if (isLoggable(TYPE_ZOOM)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("ZOOM");
        }
        if (isLoggable(TYPE_ZOOM_BLOCK_DISPLAY)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("ZOOM_BLOCK_DISPLAY");
        }
        if (isLoggable(TYPE_TIME)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("TIME");
        }
        if (builder.length() == 0) {
            builder.append("NONE");
        }
        return builder.toString();
    }

    /**
     * 低 16 位只能有一个 1，保留最高的一个
     */
    private static int low16One(int mask) {
        int maskLow16Bits = mask & 0xFFFF;

        StringBuilder builder = new StringBuilder();
        builder.append("1");

        int low16StringLength = Integer.toBinaryString(maskLow16Bits).length();
        for (int index = 1, length = low16StringLength - 1; index <= length; index++) {
            builder.append("0");
        }
        String fixLow16MaskString = builder.toString();
        int fixLow16Mask = parseUnsignedInt(fixLow16MaskString, 2);

        int finalLow16 = mask & fixLow16Mask;
        //noinspection NumericOverflow
        int high16 = mask & (0xFFFF << 16);
        return high16 | finalLow16;
    }

    /**
     * 将二进制字符串转成整型
     *
     * @param s     待转换的二进制字符串
     * @param radix 一般为2
     * @return 整型
     * @throws NumberFormatException 字符串异常
     */
    public static int parseUnsignedInt(@NonNull String s, @SuppressWarnings("SameParameterValue") int radix) throws NumberFormatException {
        //noinspection ConstantConditions
        if (s == null) {
            throw new NumberFormatException("null");
        }

        int len = s.length();
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar == '-') {
                throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", s));
            } else {
                if (len <= 5 || // Integer.MAX_VALUE in Character.MAX_RADIX is 6 digits
                        (radix == 10 && len <= 9)) { // Integer.MAX_VALUE in base 10 is 10 digits
                    return Integer.parseInt(s, radix);
                } else {
                    long ell = Long.parseLong(s, radix);
                    if ((ell & 0xffff_ffff_0000_0000L) == 0) {
                        return (int) ell;
                    } else {
                        throw new NumberFormatException(String.format("String value %s exceeds range of unsigned int.", s));
                    }
                }
            }
        } else {
            throw new NumberFormatException("For input string: \"" + s + "\"");
        }
    }

    /**
     * 组装最终的日志，将 scope 和 log 连接起来
     *
     * @param scope       表示当前日志所处的位置
     * @param formatOrLog 分两种情况，如果 args 不为 null 且长度大于0，那么这就是日志的格式化模板，例如 "position=%d"；否则这就是具体的日志，例如 “position=5”
     * @param args        用于填充 format 格式化模板的数据集合
     * @return args 不为 null 且长度大于 0 时返回 "${scope}. ${String.format(format, args)}"，否则返回 "${scope}. ${format}"
     */
    private static String assembleLog(@Nullable String scope, @NonNull String formatOrLog, @Nullable Object... args) {
        if (TextUtils.isEmpty(formatOrLog)) {
            return "";
        }

        if (args != null && args.length > 0) {
            if (!TextUtils.isEmpty(scope)) {
                return scope + ". " + String.format(formatOrLog, args);
            } else {
                return String.format(formatOrLog, args);
            }
        } else {
            // args 为空说明 format 就是日志
            if (!TextUtils.isEmpty(scope)) {
                return scope + ". " + formatOrLog;
            } else {
                return formatOrLog;
            }
        }
    }

    /* *********************************** VERBOSE *************************************** */

    /**
     * 输出 VERBOSE 级别的日志，受 isLoggable(LEVEL_VERBOSE) 控制
     *
     * @param scope  表示当前日志所处的位置
     * @param format 分两种情况，如果 args 不为 null 且长度大于0，那么这就是日志的格式化模板，例如 "position=%d"；否则这就是具体的日志，例如 “position=5”
     * @param args   用于填充 format 格式化模板的数据集合
     * @return 总共输出了多少个字符
     */
    public static int v(@Nullable String scope, @NonNull String format, @NonNull Object... args) {
        if (!isLoggable(LEVEL_VERBOSE)) {
            return 0;
        }
        return proxy.v(TAG, assembleLog(scope, format, args));
    }

    /**
     * 输出 VERBOSE 级别的日志，受 isLoggable(LEVEL_VERBOSE) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int v(@Nullable String scope, @NonNull String log) {
        if (!isLoggable(LEVEL_VERBOSE)) {
            return 0;
        }
        return proxy.v(TAG, assembleLog(scope, log, (Object[]) null));
    }


    /* *********************************** DEBUG *************************************** */

    /**
     * 输出 DEBUG 级别的日志，受 isLoggable(LEVEL_DEBUG) 控制
     *
     * @param scope  表示当前日志所处的位置
     * @param format 分两种情况，如果 args 不为 null 且长度大于0，那么这就是日志的格式化模板，例如 "position=%d"；否则这就是具体的日志，例如 “position=5”
     * @param args   用于填充 format 格式化模板的数据集合
     * @return 总共输出了多少个字符
     */
    public static int d(@Nullable String scope, @NonNull String format, @NonNull Object... args) {
        if (!isLoggable(LEVEL_DEBUG)) {
            return 0;
        }
        return proxy.d(TAG, assembleLog(scope, format, args));
    }

    /**
     * 输出 DEBUG 级别的日志，受 isLoggable(LEVEL_DEBUG) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int d(@Nullable String scope, @NonNull String log) {
        if (!isLoggable(LEVEL_DEBUG)) {
            return 0;
        }
        return proxy.d(TAG, assembleLog(scope, log, (Object[]) null));
    }

    /**
     * 输出 DEBUG 级别的日志，受 isLoggable(LEVEL_DEBUG) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param tr    异常
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int d(@Nullable String scope, @NonNull Throwable tr, @NonNull String log) {
        if (!isLoggable(LEVEL_DEBUG)) {
            return 0;
        }
        return proxy.d(TAG, assembleLog(scope, log, (Object[]) null), tr);
    }


    /* *********************************** INFO *************************************** */

    /**
     * 输出 INFO 级别的日志，受 isLoggable(LEVEL_INFO) 控制
     *
     * @param scope  表示当前日志所处的位置
     * @param format 分两种情况，如果 args 不为 null 且长度大于0，那么这就是日志的格式化模板，例如 "position=%d"；否则这就是具体的日志，例如 “position=5”
     * @param args   用于填充 format 格式化模板的数据集合
     * @return 总共输出了多少个字符
     */
    public static int i(@Nullable String scope, @NonNull String format, @NonNull Object... args) {
        if (!isLoggable(LEVEL_INFO)) {
            return 0;
        }
        return proxy.i(TAG, assembleLog(scope, format, args));
    }

    /**
     * 输出 INFO 级别的日志，受 isLoggable(LEVEL_INFO) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int i(@Nullable String scope, @NonNull String log) {
        if (!isLoggable(LEVEL_INFO)) {
            return 0;
        }
        return proxy.i(TAG, assembleLog(scope, log, (Object[]) null));
    }


    /* *********************************** WARNING *************************************** */

    /**
     * 输出 WARNING 级别的日志，受 isLoggable(LEVEL_WARNING) 控制
     *
     * @param scope  表示当前日志所处的位置
     * @param format 分两种情况，如果 args 不为 null 且长度大于0，那么这就是日志的格式化模板，例如 "position=%d"；否则这就是具体的日志，例如 “position=5”
     * @param args   用于填充 format 格式化模板的数据集合
     * @return 总共输出了多少个字符
     */
    public static int w(@Nullable String scope, @NonNull String format, @NonNull Object... args) {
        if (!isLoggable(LEVEL_WARNING)) {
            return 0;
        }
        return proxy.w(TAG, assembleLog(scope, format, args));
    }

    /**
     * 输出 WARNING 级别的日志，受 isLoggable(LEVEL_WARNING) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int w(@Nullable String scope, @NonNull String log) {
        if (!isLoggable(LEVEL_WARNING)) {
            return 0;
        }
        return proxy.w(TAG, assembleLog(scope, log, (Object[]) null));
    }

    /**
     * 输出 WARNING 级别的日志，受 isLoggable(LEVEL_WARNING) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param tr    异常
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int w(@Nullable String scope, @NonNull Throwable tr, @NonNull String log) {
        if (!isLoggable(LEVEL_WARNING)) {
            return 0;
        }
        return proxy.w(TAG, assembleLog(scope, log, (Object[]) null), tr);
    }


    /* *********************************** ERROR *************************************** */

    /**
     * 输出 ERROR 级别的日志，受 isLoggable(LEVEL_ERROR) 控制
     *
     * @param scope  表示当前日志所处的位置
     * @param format 分两种情况，如果 args 不为 null 且长度大于0，那么这就是日志的格式化模板，例如 "position=%d"；否则这就是具体的日志，例如 “position=5”
     * @param args   用于填充 format 格式化模板的数据集合
     * @return 总共输出了多少个字符
     */
    public static int e(@Nullable String scope, @NonNull String format, @NonNull Object... args) {
        if (!isLoggable(LEVEL_ERROR)) {
            return 0;
        }
        return proxy.e(TAG, assembleLog(scope, format, args));
    }

    /**
     * 输出 ERROR 级别的日志，受 isLoggable(LEVEL_ERROR) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int e(@Nullable String scope, @NonNull String log) {
        if (!isLoggable(LEVEL_ERROR)) {
            return 0;
        }
        return proxy.e(TAG, assembleLog(scope, log, (Object[]) null));
    }

    /**
     * 输出 ERROR 级别的日志，受 isLoggable(LEVEL_ERROR) 控制
     *
     * @param scope 表示当前日志所处的位置
     * @param tr    异常
     * @param log   日志内容
     * @return 总共输出了多少个字符
     */
    public static int e(@Nullable String scope, @NonNull Throwable tr, @NonNull String log) {
        if (!isLoggable(LEVEL_ERROR)) {
            return 0;
        }
        return proxy.e(TAG, assembleLog(scope, log, (Object[]) null), tr);
    }

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
    @IntDef({
            LEVEL_VERBOSE,
            LEVEL_DEBUG,
            LEVEL_INFO,
            LEVEL_WARNING,
            LEVEL_ERROR,
            LEVEL_NONE,
    })
    public @interface Level {

    }

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
    @IntDef({
            TYPE_CACHE,
            TYPE_ZOOM_BLOCK_DISPLAY,
            TYPE_FLOW,
            TYPE_TIME,
            TYPE_ZOOM,
    })
    public @interface Type {

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
