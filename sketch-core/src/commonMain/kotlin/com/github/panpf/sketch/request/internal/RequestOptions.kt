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

import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.target.TargetLifecycle

/**
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
data class RequestOptions(
    val listener: Listener?,
    val listeners: Set<Listener>?,
    val progressListener: ProgressListener?,
    val progressListeners: Set<ProgressListener>?,
    val lifecycleResolver: LifecycleResolver?,
) {

    fun newBuilder(): Builder {
        return Builder(this)
    }

    class Builder {
        private var listener: Listener? = null
        private var listeners: MutableSet<Listener>? = null
        private var progressListener: ProgressListener? = null
        private var progressListeners: MutableSet<ProgressListener>? = null
        private var lifecycleResolver: LifecycleResolver? = null

        constructor()

        constructor(requestOptions: RequestOptions) {
            listeners = requestOptions.listeners?.toMutableSet()
            listener = requestOptions.listener
            progressListeners = requestOptions.progressListeners?.toMutableSet()
            progressListener = requestOptions.progressListener
            lifecycleResolver = requestOptions.lifecycleResolver
        }

        /**
         * Set the [Listener]
         */
        fun listener(
            listener: Listener?
        ): Builder = apply {
            this.listener = listener
        }

        /**
         * Add the [Listener] to set
         */
        fun addListener(
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
        fun removeListener(
            listener: Listener
        ): Builder = apply {
            listeners?.remove(listener)
        }

        /**
         * Set the [ProgressListener]
         */
        fun progressListener(
            progressListener: ProgressListener?
        ): Builder = apply {
            this.progressListener = progressListener
        }

        /**
         * Add the [ProgressListener] to set
         */
        fun addProgressListener(
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
        fun removeProgressListener(
            progressListener: ProgressListener
        ): Builder = apply {
            progressListeners?.remove(progressListener)
        }

        /**
         * Set the [TargetLifecycle] for this request.
         *
         * Requests are queued while the lifecycle is not at least [TargetLifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [TargetLifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycle: TargetLifecycle?): Builder = apply {
            this.lifecycleResolver = if (lifecycle != null) LifecycleResolver(lifecycle) else null
        }

        /**
         * Set the [LifecycleResolver] for this request.
         *
         * Requests are queued while the lifecycle is not at least [TargetLifecycle.State.STARTED].
         * Requests are cancelled when the lifecycle reaches [TargetLifecycle.State.DESTROYED].
         *
         * If this is null or is not set the will attempt to find the lifecycle
         * for this request through its [context].
         */
        fun lifecycle(lifecycleResolver: LifecycleResolver?): Builder = apply {
            this.lifecycleResolver = lifecycleResolver
        }

        fun build(): RequestOptions {
            return RequestOptions(
                listener = listener,
                listeners = listeners,
                progressListener = progressListener,
                progressListeners = progressListeners,
                lifecycleResolver = lifecycleResolver
            )
        }
    }
}