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
package com.github.panpf.sketch.request.internal

import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
data class RequestOptions(
    val listener: Listener?,
    val progressListener: ProgressListener?,
    val lifecycleResolver: LifecycleResolver?,
) {

    fun newBuilder(): Builder {
        return Builder(this)
    }

    class Builder {
        private var listeners: MutableSet<Listener>? = null
        private var progressListeners: MutableSet<ProgressListener>? = null
        private var lifecycleResolver: LifecycleResolver? = null

        constructor()

        constructor(requestOptions: RequestOptions) {
            listeners = requestOptions.listener?.let {
                if (it is Listeners) {
                    it.list.toMutableSet()
                } else {
                    mutableSetOf(it)
                }
            }
            progressListeners = requestOptions.progressListener?.let {
                if (it is ProgressListeners) {
                    it.list.toMutableSet()
                } else {
                    mutableSetOf(it)
                }
            }
            lifecycleResolver = requestOptions.lifecycleResolver
        }

        /**
         * Add the [Listener] to set
         */
        fun registerListener(
            listener: Listener
        ): Builder = apply {
            val listeners = listeners
                ?: mutableSetOf<Listener>().apply {
                    this@Builder.listeners = this
                }
            listeners.add(listener)
        }

        /**
         * Remove the [Listener] from set
         */
        fun unregisterListener(
            listener: Listener
        ): Builder = apply {
            listeners?.remove(listener)
        }

        /**
         * Add the [ProgressListener] to set
         */
        fun registerProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            val progressListeners =
                progressListeners ?: mutableSetOf<ProgressListener>().apply {
                    this@Builder.progressListeners = this
                }
            progressListeners.add(progressListener)
        }

        /**
         * Remove the [ProgressListener] from set
         */
        fun unregisterProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            progressListeners?.remove(progressListener)
        }

        /**
         * Set the [Lifecycle] for this request.
         *
         * Requests are queued while the lifecycle is not at least [Lifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [Lifecycle.State.DESTROYED].
         */
        fun lifecycle(lifecycle: Lifecycle?): Builder = apply {
            this.lifecycleResolver = if (lifecycle != null) LifecycleResolver(lifecycle) else null
        }

        /**
         * Set the [LifecycleResolver] for this request.
         *
         * Requests are queued while the lifecycle is not at least [Lifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [Lifecycle.State.DESTROYED].
         */
        fun lifecycle(lifecycleResolver: LifecycleResolver?): Builder = apply {
            this.lifecycleResolver = lifecycleResolver
        }

        fun build(): RequestOptions {
            return RequestOptions(
                listener = listeners
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        if (it.size > 1) {
                            Listeners(it.toList())
                        } else {
                            it.first()
                        }
                    },
                progressListener = progressListeners
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        if (it.size > 1) {
                            ProgressListeners(it.toList())
                        } else {
                            it.first()
                        }
                    },
                lifecycleResolver = lifecycleResolver
            )
        }
    }
}