package com.github.panpf.sketch.test.util

import java.io.InputStream

class SlowInputStream(private val inputStream: InputStream, val readDelayMillis: Long) : InputStream() {

    override fun read(): Int {
        Thread.sleep(readDelayMillis)
        return inputStream.read()
    }

    override fun read(b: ByteArray?): Int {
        Thread.sleep(readDelayMillis)
        return inputStream.read(b)
    }

    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        Thread.sleep(readDelayMillis)
        return inputStream.read(b, off, len)
    }

    override fun close() {
        return inputStream.close()
    }

    override fun skip(n: Long): Long {
        return inputStream.skip(n)
    }

    override fun available(): Int {
        return inputStream.available()
    }

    override fun mark(readlimit: Int) {
        inputStream.mark(readlimit)
    }

    override fun reset() {
        inputStream.reset()
    }

    override fun markSupported(): Boolean {
        return inputStream.markSupported()
    }
}