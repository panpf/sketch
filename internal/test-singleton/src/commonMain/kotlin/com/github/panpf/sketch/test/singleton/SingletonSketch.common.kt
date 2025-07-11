/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.test.singleton

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.test.singleton.SingletonSketch.get
import com.github.panpf.sketch.test.singleton.SingletonSketch.setSafe
import com.github.panpf.sketch.test.singleton.SingletonSketch.setUnsafe
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlin.jvm.JvmStatic

object SingletonSketch {

    private val reference = atomic<Any?>(null)
    private val defaultCreated = atomic(false)

    /**
     * Get the singleton [Sketch].
     */
    fun get(context: PlatformContext): Sketch {
        return (reference.value as? Sketch) ?: initialSketch(context)
    }

    /**
     * Set the [Factory] that will be used to lazily create the singleton [Sketch].
     *
     * This function is similar to [setUnsafe] except:
     *
     * - If an [Sketch] has already been created it **will not** be replaced with [factory].
     * - If the default [Sketch] has already been created, an error will be thrown as it
     *   indicates [setSafe] is being called too late and after [get] has already been called.
     * - It's safe to call [setSafe] multiple times.
     *
     * The factory is guaranteed to be invoked at most once.
     */
    @JvmStatic
    fun setSafe(factory: Factory) {
        val value = reference.value
        if (value is Sketch) {
            if (defaultCreated.value) {
                error(
                    """The default Sketch has already been created. This indicates that
                    'setSafe' is being called after the first 'get' call. Ensure that 'setSafe' is
                    called before any Sketch API usages (e.g. `load`, `AsyncImage`,
                    `rememberAsyncImagePainter`, etc.).
                    """.trimIndent(),
                )
            }
            return
        }

        defaultCreated.value = false
        reference.compareAndSet(value, factory)
    }

    /**
     * Set the singleton [Sketch] and overwrite any previously set value.
     */
    fun setUnsafe(sketch: Sketch) {
        val value = reference.value
        if (value === sketch) {
            return
        }
        if (value is Sketch) {
            value.shutdown()
        }
        defaultCreated.value = false
        reference.value = sketch
    }

    /**
     * Set the [Factory] that will be used to lazily create the singleton [Sketch] and
     * overwrite any previously set value.
     *
     * The factory is guaranteed to be invoked at most once.
     */
    @JvmStatic
    fun setUnsafe(factory: Factory) {
        val value = reference.value
        if (value is Sketch) {
            value.shutdown()
        }
        defaultCreated.value = false
        reference.value = factory
    }

    /**
     * Clear the [Sketch] and [Factory] held by this class.
     *
     * This method is useful for testing and its use is discouraged in production code.
     */
    @JvmStatic
    fun reset() {
        val value = reference.value
        if (value is Sketch) {
            value.shutdown()
        }
        defaultCreated.value = false
        reference.value = null
    }

    private fun initialSketch(context: PlatformContext): Sketch {
        // Local storage to ensure newImageLoader is invoked at most once.
        var imageLoader: Sketch? = null

        return reference.updateAndGet { value ->
            when {
                value is Sketch -> value
                imageLoader != null -> imageLoader
                else -> {
                    ((value as? Factory)?.createSketch(context)
                        ?: context.applicationSketchFactory()?.createSketch(context)
                        ?: (DefaultSketchFactory.createSketch(context)).apply {
                            defaultCreated.value = true
                        })
                        .also { imageLoader = it }
                }
            }
        } as Sketch
    }

    /**
     * A factory for creating [Sketch] instances. Usually used to configure [Sketch] singletons, you need to implement this interface in your Application
     */
    fun interface Factory {
        fun createSketch(context: PlatformContext): Sketch
    }
}

internal expect fun PlatformContext.applicationSketchFactory(): SingletonSketch.Factory?

private val DefaultSketchFactory = SingletonSketch.Factory { context ->
    Sketch(context)
}