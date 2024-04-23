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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is StdLogPipeline
    }

    override fun hashCode(): Int {
        return this@StdLogPipeline::class.hashCode()
    }

    override fun toString(): String = "StdLogPipeline"
}