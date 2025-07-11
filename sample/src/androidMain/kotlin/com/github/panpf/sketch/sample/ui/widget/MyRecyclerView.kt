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

package com.github.panpf.sketch.sample.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.util.lifecycleOwner
import com.github.panpf.sketch.sample.util.collectWithLifecycle
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener
import org.koin.mp.KoinPlatform

class MyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private val appSettings: AppSettings = KoinPlatform.getKoin().get()
    private val scrollListener = PauseLoadWhenScrollingMixedScrollListener()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        appSettings.pauseLoadWhenScrollInList.collectWithLifecycle(lifecycleOwner) {
            setEnabledPauseLoadWhenScrolling(it)
        }
    }

    private fun setEnabledPauseLoadWhenScrolling(enabled: Boolean) {
        removeOnScrollListener(scrollListener)
        if (enabled) {
            addOnScrollListener(scrollListener)
        }
    }
}