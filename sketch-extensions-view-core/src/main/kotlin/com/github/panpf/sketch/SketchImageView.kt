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

import android.content.Context
import android.util.AttributeSet
import com.github.panpf.sketch.ability.AbsAbilityImageView
import com.github.panpf.sketch.internal.parseImageXmlAttributes
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.RequestState
import com.github.panpf.sketch.request.internal.Listeners
import com.github.panpf.sketch.request.internal.PairListener
import com.github.panpf.sketch.request.internal.PairProgressListener
import com.github.panpf.sketch.request.internal.ProgressListeners

open class SketchImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AbsAbilityImageView(context, attrs, defStyle), ImageOptionsProvider {

    override var imageOptions: ImageOptions? = null
    private var listeners: Listeners? = null
    private var progressListeners: ProgressListeners? = null

    override val requestState = RequestState()

    init {
        @Suppress("LeakingThis")
        imageOptions = parseImageXmlAttributes(context, attrs)
        @Suppress("LeakingThis")
        registerListener(requestState)
    }

    override fun getListener(): Listener? {
        val myListener = listeners
        val superListener = super.getListener()
        return if (myListener != null && superListener != null) {
            PairListener(first = myListener, second = superListener)
        } else {
            myListener ?: superListener
        }
    }

    override fun getProgressListener(): ProgressListener? {
        val myProgressListener = progressListeners
        val superProgressListener = super.getProgressListener()
        return if (myProgressListener != null && superProgressListener != null) {
            PairProgressListener(first = myProgressListener, second = superProgressListener)
        } else {
            myProgressListener ?: superProgressListener
        }
    }

    fun registerListener(listener: Listener) {
        listeners = (listeners?.list?.toMutableList() ?: mutableListOf())
            .apply { add(listener) }
            .takeIf { it.isNotEmpty() }
            ?.let { Listeners(it.toList()) }
    }

    fun unregisterListener(listener: Listener) {
        listeners = listeners?.list?.toMutableList()
            ?.apply { remove(listener) }
            ?.takeIf { it.isNotEmpty() }
            ?.let { Listeners(it.toList()) }
    }

    fun registerProgressListener(listener: ProgressListener) {
        progressListeners = (progressListeners?.list?.toMutableList() ?: mutableListOf())
            .apply { add(listener) }
            .takeIf { it.isNotEmpty() }
            ?.let { ProgressListeners(it.toList()) }
    }

    fun unregisterProgressListener(listener: ProgressListener) {
        progressListeners = progressListeners?.list?.toMutableList()
            ?.apply { remove(listener) }
            ?.takeIf { it.isNotEmpty() }
            ?.let { ProgressListeners(it.toList()) }
    }
}