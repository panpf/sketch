/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.cache.internal

import okio.Buffer
import okio.IOException
import okio.Sink

/** A sink that never throws [IOException]s, even if the underlying sink does. */
internal class FaultHidingSink(
    private val delegate: Sink,
    private val onException: (IOException) -> Unit,
) : Sink by delegate {

    private var hasErrors = false

    override fun write(source: Buffer, byteCount: Long) {
        if (hasErrors) {
            source.skip(byteCount)
            return
        }
        try {
            delegate.write(source, byteCount)
        } catch (e: IOException) {
            hasErrors = true
            onException(e)
        }
    }

    override fun flush() {
        try {
            delegate.flush()
        } catch (e: IOException) {
            hasErrors = true
            onException(e)
        }
    }

    override fun close() {
        try {
            delegate.close()
        } catch (e: IOException) {
            hasErrors = true
            onException(e)
        }
    }
}
