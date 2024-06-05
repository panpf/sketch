package com.github.panpf.sketch.util

actual fun platformLogPipeline(): Logger.Pipeline = PrintLogPipeline()

class PrintLogPipeline : Logger.Pipeline {

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is PrintLogPipeline
    }

    override fun hashCode(): Int {
        return this@PrintLogPipeline::class.hashCode()
    }

    override fun toString(): String = "PrintLogPipeline"
}