/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch

import android.text.TextUtils
import android.util.Log
import androidx.annotation.IntDef

class SLog {
    companion object {
        const val VERBOSE = 1
        const val DEBUG = 2
        const val INFO = 4
        const val WARNING = 8
        const val ERROR = 16
        const val NONE = 32
        const val NAME_VERBOSE = "VERBOSE"
        const val NAME_DEBUG = "DEBUG"
        const val NAME_INFO = "INFO"
        const val NAME_WARNING = "WARNING"
        const val NAME_ERROR = "ERROR"
        const val NAME_NONE = "NONE"
        private const val TAG = "Sketch"
        private var level = 0
        private var proxy: Proxy = DefaultProxy()

        init {
            setLevel(INFO)
        }

        @JvmStatic
        fun setProxy(proxy: Proxy?) {
            if (SLog.proxy !== proxy) {
                SLog.proxy.onReplaced()
                SLog.proxy = proxy ?: DefaultProxy()
            }
        }

        @JvmStatic
        fun isLoggable(@Level level: Int): Boolean {
            return level >= SLog.level
        }

        @Level
        @JvmStatic
        fun getLevel(): Int {
            return level
        }

        @JvmStatic
        fun setLevel(@Level level: Int) {
            if (SLog.level != level) {
                val oldLevelName = levelName
                SLog.level = level
                Log.w(TAG, "SLog. " + String.format("setLevel. %s -> %s", oldLevelName, levelName))
            }
        }

        @JvmStatic
        val levelName: String
            get() = when (level) {
                VERBOSE -> NAME_VERBOSE
                DEBUG -> NAME_DEBUG
                INFO -> NAME_INFO
                WARNING -> NAME_WARNING
                ERROR -> NAME_ERROR
                NONE -> NAME_NONE
                else -> "UNKNOWN(" + level + ")"
            }

        @JvmStatic
        private fun joinMessage(module: String?, formatOrLog: String, vararg args: Any?): String {
            if (TextUtils.isEmpty(formatOrLog)) {
                return ""
            }
            return if (args.isNotEmpty()) {
                if (!TextUtils.isEmpty(module)) {
                    module + ". " + String.format(formatOrLog, *args)
                } else {
                    String.format(formatOrLog, *args)
                }
            } else {
                if (!TextUtils.isEmpty(module)) {
                    "$module. $formatOrLog"
                } else {
                    formatOrLog
                }
            }
        }

        @JvmStatic
        fun v(msg: String): Int {
            return if (isLoggable(VERBOSE)) proxy.v(
                TAG,
                joinMessage(null, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun vf(format: String, vararg args: Any): Int {
            return if (isLoggable(VERBOSE)) proxy.v(TAG, joinMessage(null, format, *args)) else 0
        }

        @JvmStatic
        fun vm(module: String, msg: String): Int {
            return if (isLoggable(VERBOSE)) proxy.v(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun vmf(module: String, format: String, vararg args: Any): Int {
            return if (isLoggable(VERBOSE)) proxy.v(TAG, joinMessage(module, format, *args)) else 0
        }

        @JvmStatic
        fun d(msg: String): Int {
            return if (isLoggable(DEBUG)) proxy.d(
                TAG,
                joinMessage(null, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun df(format: String, vararg args: Any): Int {
            return if (isLoggable(DEBUG)) proxy.d(TAG, joinMessage(null, format, *args)) else 0
        }

        @JvmStatic
        fun dm(module: String, msg: String): Int {
            return if (isLoggable(DEBUG)) proxy.d(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun dmf(module: String, format: String, vararg args: Any): Int {
            return if (isLoggable(DEBUG)) proxy.d(TAG, joinMessage(module, format, *args)) else 0
        }

        @JvmStatic
        fun i(msg: String): Int {
            return if (isLoggable(INFO)) proxy.i(
                TAG,
                joinMessage(null, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun iff(format: String, vararg args: Any): Int {
            return if (isLoggable(INFO)) proxy.i(TAG, joinMessage(null, format, *args)) else 0
        }

        @JvmStatic
        fun im(module: String, msg: String): Int {
            return if (isLoggable(INFO)) proxy.i(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun imf(module: String, format: String, vararg args: Any): Int {
            return if (isLoggable(INFO)) proxy.i(TAG, joinMessage(module, format, *args)) else 0
        }

        @JvmStatic
        fun w(msg: String): Int {
            return if (isLoggable(WARNING)) proxy.w(
                TAG,
                joinMessage(null, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun wf(format: String, vararg args: Any): Int {
            return if (isLoggable(WARNING)) proxy.w(TAG, joinMessage(null, format, *args)) else 0
        }

        @JvmStatic
        fun wm(module: String, msg: String): Int {
            return if (isLoggable(WARNING)) proxy.w(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun wmf(module: String, format: String, vararg args: Any): Int {
            return if (isLoggable(WARNING)) proxy.w(TAG, joinMessage(module, format, *args)) else 0
        }

        @JvmStatic
        fun wmt(module: String, tr: Throwable, msg: String): Int {
            return if (isLoggable(WARNING)) proxy.w(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!),
                tr
            ) else 0
        }

        @JvmStatic
        fun wmtf(module: String, tr: Throwable, format: String, vararg args: Any): Int {
            return if (isLoggable(WARNING)) proxy.w(
                TAG,
                joinMessage(module, format, *args),
                tr
            ) else 0
        }

        @JvmStatic
        fun e(msg: String): Int {
            return if (isLoggable(ERROR)) proxy.e(
                TAG,
                joinMessage(null, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun ef(format: String, vararg args: Any): Int {
            return if (isLoggable(ERROR)) proxy.e(TAG, joinMessage(null, format, *args)) else 0
        }

        @JvmStatic
        fun em(module: String, msg: String): Int {
            return if (isLoggable(ERROR)) proxy.e(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!)
            ) else 0
        }

        @JvmStatic
        fun emf(module: String, format: String, vararg args: Any): Int {
            return if (isLoggable(ERROR)) proxy.e(TAG, joinMessage(module, format, *args)) else 0
        }

        @JvmStatic
        fun emt(module: String, tr: Throwable, msg: String): Int {
            return if (isLoggable(ERROR)) proxy.e(
                TAG,
                joinMessage(module, msg, *(null as Array<Any?>?)!!),
                tr
            ) else 0
        }

        @JvmStatic
        fun emtf(module: String, tr: Throwable, format: String, vararg args: Any): Int {
            return if (isLoggable(ERROR)) proxy.e(
                TAG,
                joinMessage(module, format, *args),
                tr
            ) else 0
        }

        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        @Target(
            AnnotationTarget.VALUE_PARAMETER,
            AnnotationTarget.FIELD,
            AnnotationTarget.FUNCTION,
            AnnotationTarget.PROPERTY_GETTER,
            AnnotationTarget.PROPERTY_SETTER,
            AnnotationTarget.LOCAL_VARIABLE
        )
        @IntDef(
            VERBOSE, DEBUG, INFO, WARNING, ERROR, NONE
        )
        annotation class Level
    }

    interface Proxy {
        fun v(tag: String, msg: String): Int
        fun v(tag: String, msg: String, tr: Throwable?): Int
        fun d(tag: String, msg: String): Int
        fun d(tag: String, msg: String, tr: Throwable?): Int
        fun i(tag: String, msg: String): Int
        fun i(tag: String, msg: String, tr: Throwable?): Int
        fun w(tag: String, msg: String): Int
        fun w(tag: String, msg: String, tr: Throwable?): Int
        fun e(tag: String, msg: String): Int
        fun e(tag: String, msg: String, tr: Throwable?): Int
        fun onReplaced()
    }

    private class DefaultProxy : Proxy {
        override fun v(tag: String, msg: String): Int {
            return Log.v(tag, msg)
        }

        override fun v(tag: String, msg: String, tr: Throwable?): Int {
            return Log.v(tag, msg, tr)
        }

        override fun d(tag: String, msg: String): Int {
            return Log.d(tag, msg)
        }

        override fun d(tag: String, msg: String, tr: Throwable?): Int {
            return Log.d(tag, msg, tr)
        }

        override fun i(tag: String, msg: String): Int {
            return Log.i(tag, msg)
        }

        override fun i(tag: String, msg: String, tr: Throwable?): Int {
            return Log.i(tag, msg, tr)
        }

        override fun w(tag: String, msg: String): Int {
            return Log.w(tag, msg)
        }

        override fun w(tag: String, msg: String, tr: Throwable?): Int {
            return Log.w(tag, msg, tr)
        }

        override fun e(tag: String, msg: String): Int {
            return Log.e(tag, msg)
        }

        override fun e(tag: String, msg: String, tr: Throwable?): Int {
            return Log.e(tag, msg, tr)
        }

        override fun onReplaced() {
            // do nothing
        }
    }
}