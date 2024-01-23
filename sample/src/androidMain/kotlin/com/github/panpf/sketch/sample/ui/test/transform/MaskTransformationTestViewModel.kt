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
package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MaskTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    private val _maskColorData = MutableStateFlow(MaskColor.RED)
    val maskColorData: StateFlow<MaskColor> = _maskColorData

    fun changeMaskColor(maskColor: MaskColor) {
        _maskColorData.value = maskColor
    }

    enum class MaskColor(val colorInt: Int) {
        RED(ColorUtils.setAlphaComponent(Color.RED, 128)),
        GREEN(ColorUtils.setAlphaComponent(Color.GREEN, 128)),
        BLUE(ColorUtils.setAlphaComponent(Color.BLUE, 128))
    }
}