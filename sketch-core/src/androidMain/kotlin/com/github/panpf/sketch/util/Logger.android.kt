package com.github.panpf.sketch.util

import android.util.Log

actual fun platformLogPipeline(): Logger.Pipeline = AndroidLogPipeline

object AndroidLogPipeline : Logger.Pipeline {

    override fun log(level: Logger.Level, tag: String, msg: String, tr: Throwable?) {
        when (level) {
            Logger.Level.Verbose -> Log.v(tag, msg, tr)
            Logger.Level.Debug -> Log.d(tag, msg, tr)
            Logger.Level.Info -> Log.i(tag, msg, tr)
            Logger.Level.Warn -> Log.w(tag, msg, tr)
            Logger.Level.Error -> Log.e(tag, msg, tr)
            Logger.Level.Assert -> Log.wtf(tag, msg, tr)
        }
    }

    override fun flush() {

    }

    @Suppress("RedundantOverride")
    override fun equals(other: Any?): Boolean {
        // If you add construction parameters to this class, you need to change it here
        return super.equals(other)
    }

    @Suppress("RedundantOverride")
    override fun hashCode(): Int {
        // If you add construction parameters to this class, you need to change it here
        return super.hashCode()
    }

    override fun toString(): String = "AndroidLogPipeline"
}