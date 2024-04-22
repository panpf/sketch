/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.test.utils

import java.io.InputStream

class SlowInputStream(private val inputStream: InputStream, val readDelayMillis: Long) :
    InputStream() {

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