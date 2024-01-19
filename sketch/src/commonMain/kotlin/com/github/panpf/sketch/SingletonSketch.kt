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
package com.github.panpf.sketch

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet

object SingletonSketch {

    private val reference = atomic<Any?>(null)

    /**
     * Get the singleton [Sketch].
     */
    @JvmStatic
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
            if (value.isDefault) {
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

        reference.compareAndSet(value, factory)
    }

    /**
     * Set the singleton [Sketch] and overwrite any previously set value.
     */
    @JvmStatic
    fun setUnsafe(sketch: Sketch) {
        val value = reference.value
        if (value === sketch) {
            return
        }
        if (value is Sketch) {
            value.shutdown()
        }
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
        reference.value = factory
    }

//    /**
//     * Set the singleton [Sketch].
//     * Prefer using `setSketch(SketchFactory)` to create the [Sketch] lazily.
//     */
//    @JvmStatic
//    @Synchronized
//    fun setSketch(sketch: Sketch) {
//        this.sketch?.shutdown()
//        this.sketch = sketch
//        this.factory = null
//    }
//
//    /**
//     * Set the [Factory] that will be used to create the singleton [Sketch].
//     * The [factory] is guaranteed to be called at most once.
//     *
//     * NOTE: [factory] will take precedence over an [Application] that implements [Factory].
//     */
//    @JvmStatic
//    @Synchronized
//    fun setSketch(factory: Factory) {
//        this.sketch?.shutdown()
//        this.sketch = null
//        this.factory = factory
//    }

    /**
     * Clear the [Sketch] and [Factory] held by this class.
     *
     * This method is useful for testing and its use is discouraged in production code.
     */
    @JvmStatic
    @Synchronized
    fun reset() {
        val value = reference.value
        if (value is Sketch) {
            value.shutdown()
        }
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
                        ?: DefaultSketchFactory.createSketch(context))
                        .also { imageLoader = it }
                }
            }
        } as Sketch
    }

    /**
     * A factory for creating [Sketch] instances. Usually used to configure [Sketch] singletons, you need to implement this interface in your [Application]
     */
    fun interface Factory {
        fun createSketch(context: PlatformContext): Sketch
    }
}

internal expect fun PlatformContext.applicationSketchFactory(): SingletonSketch.Factory?

private val DefaultSketchFactory = SingletonSketch.Factory { context ->
    Sketch.Builder(context)
        // Add a marker value so we know this was created by the default singleton image loader.
//        .apply { extras[DefaultSingletonImageLoaderKey] = Unit } // TODO
        .build()
}

private val Sketch.isDefault: Boolean
    //    get() = defaults.extras[DefaultSingletonImageLoaderKey] != null // TODO
    get() = false