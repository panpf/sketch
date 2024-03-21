package com.github.panpf.sketch.util

import java.io.ByteArrayOutputStream
import java.io.PrintStream

actual fun platformLogPipeline(): Logger.Pipeline = JvmLogPipeline()

class JvmLogPipeline : Logger.Pipeline {

    override fun log(level: Int, tag: String, msg: String, tr: Throwable?) {
        if (tr != null) {
            val trString = stackTraceToString(tr)
            println("${Logger.levelName(level)}. $tag. $msg. $trString")
        } else {
            println("${Logger.levelName(level)}. $tag. $msg")
        }
    }

    private fun stackTraceToString(throwable: Throwable): String {
        val arrayOutputStream = ByteArrayOutputStream()
        val printWriter = PrintStream(arrayOutputStream)
        throwable.printStackTrace(printWriter)
        return String(arrayOutputStream.toByteArray())
    }

    override fun flush() {

    }

    override fun toString(): String = "JvmLogPipeline"

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