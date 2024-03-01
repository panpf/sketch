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

class MultiTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    private val _blurRadiusData = MutableStateFlow(30)
    val blurRadiusData: StateFlow<Int> = _blurRadiusData

    private val _maskColorData = MutableStateFlow<Int?>(ColorUtils.setAlphaComponent(Color.RED, 128))
    val maskColorData: StateFlow<Int?> = _maskColorData

    private val _roundedCornersRadiusData = MutableStateFlow(30)
    val roundedCornersRadiusData: StateFlow<Int> = _roundedCornersRadiusData

    private val _rotateData = MutableStateFlow(45)
    val rotateData: StateFlow<Int> = _rotateData

    fun changeRotate(rotate: Int) {
        _rotateData.value = rotate
    }

    fun changeRoundedCornersRadius(radius: Int) {
        _roundedCornersRadiusData.value = radius
    }

    fun changeBlurRadius(radius: Int) {
        _blurRadiusData.value = radius
    }

    fun changeMaskColor(color: Int?) {
        _maskColorData.value = color
    }
}