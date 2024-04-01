package com.github.panpf.sketch.util

actual fun platformLogPipeline(): Logger.Pipeline = StdLogPipeline()

class StdLogPipeline : Logger.Pipeline {

    override fun log(level: Int, tag: String, msg: String, tr: Throwable?) {
        if (tr != null) {
            val trString = tr.stackTraceToString()
            println("${Logger.levelName(level)}. $tag. $msg. \n$trString")
        } else {
            println("${Logger.levelName(level)}. $tag. $msg")
        }
    }

    override fun flush() {

    }

    override fun toString(): String = "StdLogPipeline"

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