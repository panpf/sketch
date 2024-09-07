/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

/**
 * Get the platform default log pipeline
 *
 * @see com.github.panpf.sketch.core.android.test.util.LoggerAndroidTest.testDefaultLogPipeline
 * @see com.github.panpf.sketch.core.nonandroid.test.util.LoggerNonAndroidTest.testDefaultLogPipeline
 */
internal expect fun defaultLogPipeline(): Logger.Pipeline

/**
 * Used to print log
 *
 * @see com.github.panpf.sketch.core.common.test.util.LoggerTest
 */
class Logger(
    level: Level = Level.Info,
    private val pipeline: Pipeline = defaultLogPipeline(),
) {

    companion object {
        const val TAG: String = "Sketch"
    }

    var level: Level = level
        set(value) {
            if (value != field) {
                val oldValue = field
                field = value
                val msg = "Logger. setLevel. $oldValue -> $value"
                pipeline.log(level = Level.Warn, tag = TAG, msg = msg, tr = null)
            }
        }

    fun isLoggable(level: Level): Boolean {
        return level >= this.level
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(msg: String) {
        if (isLoggable(Level.Verbose)) {
            pipeline.log(Level.Verbose, TAG, msg, null)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(lazyMsg: () -> String) {
        if (isLoggable(Level.Verbose)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Verbose, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(tr: Throwable?, msg: String) {
        if (isLoggable(Level.Verbose)) {
            pipeline.log(level = Level.Verbose, tag = TAG, msg = msg, tr = tr)
        }
    }

    /**
     * Print a log with the VERBOSE level
     */
    fun v(tr: Throwable?, lazyMsg: () -> String) {
        if (isLoggable(Level.Verbose)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Verbose, tag = TAG, msg = msg, tr = tr)
        }
    }


    /**
     * Print a log with the DEBUG level
     */
    fun d(msg: String) {
        if (isLoggable(Level.Debug)) {
            pipeline.log(level = Level.Debug, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(lazyMsg: () -> String) {
        if (isLoggable(Level.Debug)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Debug, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(tr: Throwable?, msg: String) {
        if (isLoggable(Level.Debug)) {
            pipeline.log(level = Level.Debug, tag = TAG, msg = msg, tr = tr)
        }
    }

    /**
     * Print a log with the DEBUG level
     */
    fun d(tr: Throwable?, lazyMsg: () -> String) {
        if (isLoggable(Level.Debug)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Debug, tag = TAG, msg = msg, tr = tr)
        }
    }


    /**
     * Print a log with the INFO level
     */
    fun i(msg: String) {
        if (isLoggable(Level.Info)) {
            pipeline.log(level = Level.Info, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(lazyMsg: () -> String) {
        if (isLoggable(Level.Info)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Info, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(tr: Throwable?, msg: String) {
        if (isLoggable(Level.Info)) {
            pipeline.log(level = Level.Info, tag = TAG, msg = msg, tr = tr)
        }
    }

    /**
     * Print a log with the INFO level
     */
    fun i(tr: Throwable?, lazyMsg: () -> String) {
        if (isLoggable(Level.Info)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Info, tag = TAG, msg = msg, tr = tr)
        }
    }


    /**
     * Print a log with the WARN level
     */
    fun w(msg: String) {
        if (isLoggable(Level.Warn)) {
            pipeline.log(level = Level.Warn, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(lazyMsg: () -> String) {
        if (isLoggable(Level.Warn)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Warn, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(tr: Throwable?, msg: String) {
        if (isLoggable(Level.Warn)) {
            pipeline.log(level = Level.Warn, tag = TAG, msg = msg, tr = tr)
        }
    }

    /**
     * Print a log with the WARN level
     */
    fun w(tr: Throwable?, lazyMsg: () -> String) {
        if (isLoggable(Level.Warn)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Warn, tag = TAG, msg = msg, tr = tr)
        }
    }


    /**
     * Print a log with the ERROR level
     */
    fun e(msg: String) {
        if (isLoggable(Level.Error)) {
            pipeline.log(level = Level.Error, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(lazyMsg: () -> String) {
        if (isLoggable(Level.Error)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Error, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(tr: Throwable?, msg: String) {
        if (isLoggable(Level.Error)) {
            pipeline.log(level = Level.Error, tag = TAG, msg = msg, tr = tr)
        }
    }

    /**
     * Print a log with the ERROR level
     */
    fun e(tr: Throwable?, lazyMsg: () -> String) {
        if (isLoggable(Level.Error)) {
            val msg = lazyMsg()
            pipeline.log(level = Level.Error, tag = TAG, msg = msg, tr = tr)
        }
    }


    /**
     * Print a log with the specified level
     */
    fun log(level: Level, msg: String) {
        if (isLoggable(level)) {
            pipeline.log(level = level, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the specified level
     */
    fun log(level: Level, lazyMsg: () -> String) {
        if (isLoggable(level)) {
            val msg = lazyMsg()
            pipeline.log(level = level, tag = TAG, msg = msg, tr = null)
        }
    }

    /**
     * Print a log with the specified level
     */
    fun log(level: Level, tr: Throwable?, msg: String) {
        if (isLoggable(level)) {
            pipeline.log(level = level, tag = TAG, msg = msg, tr = tr)
        }
    }

    /**
     * Print a log with the specified level
     */
    fun log(level: Level, tr: Throwable?, lazyMsg: () -> String) {
        if (isLoggable(level)) {
            val msg = lazyMsg()
            pipeline.log(level = level, tag = TAG, msg = msg, tr = tr)
        }
    }


    /**
     * Flush the log pipeline
     */
    fun flush() {
        pipeline.flush()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Logger
        if (level != other.level) return false
        if (pipeline != other.pipeline) return false
        return true
    }

    override fun hashCode(): Int {
        var result = level.hashCode()
        result = 31 * result + (pipeline.hashCode())
        return result
    }

    override fun toString(): String {
        return "Logger(level=$level, pipeline=$pipeline)"
    }

    /**
     * Priority constant for the println method.
     *
     * @see com.github.panpf.sketch.core.common.test.util.LoggerTest.testLevelEnum
     */
    enum class Level {
        /**
         * Priority constant for the println method; use Log.v.
         */
        Verbose,

        /**
         * Priority constant for the println method; use Log.d.
         */
        Debug,

        /**
         * Priority constant for the println method; use Log.i.
         */
        Info,

        /**
         * Priority constant for the println method; use Log.w.
         */
        Warn,

        /**
         * Priority constant for the println method; use Log.e.
         */
        Error,

        /**
         * Priority constant for the println method.
         */
        Assert,
    }

    /**
     * The pipeline of the log
     */
    interface Pipeline {
        fun log(level: Level, tag: String, msg: String, tr: Throwable?)
        fun flush()
    }
}