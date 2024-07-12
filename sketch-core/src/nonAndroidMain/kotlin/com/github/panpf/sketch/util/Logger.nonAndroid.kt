package com.github.panpf.sketch.util

actual fun platformLogPipeline(): Logger.Pipeline = PrintLogPipeline

object PrintLogPipeline : Logger.Pipeline {

    override fun log(level: Logger.Level, tag: String, msg: String, tr: Throwable?) {
        if (tr != null) {
            val trString = tr.stackTraceToString()
            println("$level. $tag. $msg. \n$trString")
        } else {
            println("$level. $tag. $msg")
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

    override fun toString(): String = "PrintLogPipeline"
}