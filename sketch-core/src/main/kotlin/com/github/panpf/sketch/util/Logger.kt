/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.util

import android.util.Log

class Logger constructor(
    level: Level = Level.INFO,
    private val proxy: Proxy = LogProxy()
) {

    companion object {
        const val TAG = "Sketch"
    }

    var level: Level = level
        set(value) {
            val oldLevel = field
            field = value
            val newLevel = value.name
            Log.w(TAG, "Logger. setLevel. $oldLevel -> $newLevel")
        }

    fun isLoggable(level: Level): Boolean {
        return level >= this.level
    }


    fun v(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.VERBOSE)) {
            proxy.v(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun v(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.VERBOSE)) {
            proxy.v(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun d(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.DEBUG)) {
            proxy.d(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun d(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.DEBUG)) {
            proxy.d(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun i(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.INFO)) {
            proxy.i(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun i(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.INFO)) {
            proxy.i(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun w(module: String, msg: String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, msg), null)
        }
    }

    fun w(module: String, throwable: Throwable?, msg: String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, msg), throwable)
        }
    }

    fun w(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun w(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.WARNING)) {
            proxy.w(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun e(module: String, msg: String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, msg), null)
        }
    }

    fun e(module: String, throwable: Throwable?, msg: String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, msg), throwable)
        }
    }

    fun e(module: String, lazyMessage: () -> String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, lazyMessage()), null)
        }
    }

    fun e(module: String, throwable: Throwable?, lazyMessage: () -> String) {
        if (isLoggable(Level.ERROR)) {
            proxy.e(TAG, joinModuleAndMsg(module, lazyMessage()), throwable)
        }
    }


    fun flush() {
        proxy.flush()
    }

    private fun joinModuleAndMsg(module: String?, msg: String): String {
        // TODO threadName is not displayed by default
        val threadName = Thread.currentThread().name.let {
            // kotlin coroutine thread name 'DefaultDispatcher-worker-1' change to 'worker1'
            if (it.startsWith("DefaultDispatcher-worker-")) {
                it.replace("DefaultDispatcher-worker-", "work")
            } else {
                it
            }
        }
        return if (module?.isNotEmpty() == true) {
            "$threadName - $module. $msg"
        } else {
            "$threadName - $msg"
        }
    }

    override fun toString(): String = "Logger(level=$level,proxy=$proxy)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Logger
        if (level != other.level) return false
        if (proxy != other.proxy) return false
        return true
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + proxy.hashCode()
        return result
    }


    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        NONE,
    }

    interface Proxy {
        fun v(tag: String, msg: String, tr: Throwable?)
        fun d(tag: String, msg: String, tr: Throwable?)
        fun i(tag: String, msg: String, tr: Throwable?)
        fun w(tag: String, msg: String, tr: Throwable?)
        fun e(tag: String, msg: String, tr: Throwable?)
        fun flush()
    }

    class LogProxy : Proxy {
        override fun v(tag: String, msg: String, tr: Throwable?) {
            Log.v(tag, msg, tr)
        }

        override fun d(tag: String, msg: String, tr: Throwable?) {
            Log.d(tag, msg, tr)
        }

        override fun i(tag: String, msg: String, tr: Throwable?) {
            Log.i(tag, msg, tr)
        }

        override fun w(tag: String, msg: String, tr: Throwable?) {
            Log.w(tag, msg, tr)
        }

        override fun e(tag: String, msg: String, tr: Throwable?) {
            Log.e(tag, msg, tr)
        }

        override fun flush() {

        }

        override fun toString(): String = "LogProxy"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}