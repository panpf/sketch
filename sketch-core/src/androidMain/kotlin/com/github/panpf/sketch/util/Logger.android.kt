package com.github.panpf.sketch.util

import android.util.Log

actual fun platformLogPipeline(): Logger.Pipeline = AndroidLogPipeline()

class AndroidLogPipeline : Logger.Pipeline {

    override fun log(level: Int, tag: String, msg: String, tr: Throwable?) {
        when (level) {
            Logger.VERBOSE -> Log.v(tag, msg, tr)
            Logger.DEBUG -> Log.d(tag, msg, tr)
            Logger.INFO -> Log.i(tag, msg, tr)
            Logger.WARN -> Log.w(tag, msg, tr)
            Logger.ERROR -> Log.e(tag, msg, tr)
            Logger.ASSERT -> Log.wtf(tag, msg, tr)
        }
    }

    override fun flush() {

    }

    override fun toString(): String = "AndroidLogPipeline"

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
}