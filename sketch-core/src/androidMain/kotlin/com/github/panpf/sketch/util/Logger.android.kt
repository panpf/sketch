package com.github.panpf.sketch.util

import android.util.Log

actual fun platformLogPipeline(): Logger.Pipeline = AndroidLogPipeline()

class AndroidLogPipeline : Logger.Pipeline {

    override fun log(level: Int, tag: String, msg: String, tr: Throwable?) {
        when (level) {
            Logger.Verbose -> Log.v(tag, msg, tr)
            Logger.Debug -> Log.d(tag, msg, tr)
            Logger.Info -> Log.i(tag, msg, tr)
            Logger.Warn -> Log.w(tag, msg, tr)
            Logger.Error -> Log.e(tag, msg, tr)
            Logger.Assert -> Log.wtf(tag, msg, tr)
        }
    }

    override fun flush() {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is AndroidLogPipeline
    }

    override fun hashCode(): Int {
        return this@AndroidLogPipeline::class.hashCode()
    }

    override fun toString(): String = "AndroidLogPipeline"
}