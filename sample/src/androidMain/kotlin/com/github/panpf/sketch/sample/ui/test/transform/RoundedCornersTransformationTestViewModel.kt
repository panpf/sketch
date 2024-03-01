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
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RoundedCornersTransformationTestViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    private val _topLeftRadiusData = MutableStateFlow(10)
    val topLeftRadiusData: StateFlow<Int> = _topLeftRadiusData
    private val _topRightRadiusData = MutableStateFlow(20)
    val topRightRadiusData: StateFlow<Int> = _topRightRadiusData
    private val _bottomLeftRadiusData = MutableStateFlow(40)
    val bottomLeftRadiusData: StateFlow<Int> = _bottomLeftRadiusData
    private val _bottomRightRadiusData = MutableStateFlow(80)
    val bottomRightRadiusData: StateFlow<Int> = _bottomRightRadiusData

    fun changeTopLeftRadius(radius: Int) {
        _topLeftRadiusData.value = radius
    }

    fun changeTopRightRadius(radius: Int) {
        _topRightRadiusData.value = radius
    }

    fun changeBottomLeftRadius(radius: Int) {
        _bottomLeftRadiusData.value = radius
    }

    fun changeBottomRightRadius(radius: Int) {
        _bottomRightRadiusData.value = radius
    }
}