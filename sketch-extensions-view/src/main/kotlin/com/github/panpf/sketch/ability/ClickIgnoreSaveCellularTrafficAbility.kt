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

package com.github.panpf.sketch.ability

import android.view.View
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.SaveCellularTrafficRequestInterceptor
import com.github.panpf.sketch.request.ignoreSaveCellularTraffic
import com.github.panpf.sketch.request.isCausedBySaveCellularTraffic
import com.github.panpf.sketch.util.SketchUtils

/**
 * Set to enable click View to force ignore the data saving function
 *
 * @see com.github.panpf.sketch.extensions.view.test.ability.ClickIgnoreSaveCellularTrafficAbilityTest.testClickIgnoreSaveCellularTrafficEnabled
 */
fun ViewAbilityContainer.setClickIgnoreSaveCellularTrafficEnabled(enable: Boolean = true) {
    val enabled = isClickIgnoreSaveCellularTrafficEnabled
    if (enable && !enabled) {
        addViewAbility(ClickIgnoreSaveCellularTrafficAbility())
    } else if (!enable && enabled) {
        viewAbilityList
            .find { it is ClickIgnoreSaveCellularTrafficAbility }
            ?.let { removeViewAbility(it) }
    }
}

/**
 * Returns true if click View force ignore data saving feature is enabled
 *
 * @see com.github.panpf.sketch.extensions.view.test.ability.ClickIgnoreSaveCellularTrafficAbilityTest.testClickIgnoreSaveCellularTrafficEnabled
 */
val ViewAbilityContainer.isClickIgnoreSaveCellularTrafficEnabled: Boolean
    get() = viewAbilityList.find { it is ClickIgnoreSaveCellularTrafficAbility } != null

/**
 * Click View to force ignoring the data saving function, generally used with [SaveCellularTrafficRequestInterceptor]
 *
 * @see com.github.panpf.sketch.extensions.view.test.ability.ClickIgnoreSaveCellularTrafficAbilityTest
 */
class ClickIgnoreSaveCellularTrafficAbility : ViewAbility, ClickObserver, RequestListenerObserver {

    private var errorFromSaveCellularTraffic = false
    private var request: ImageRequest? = null

    override var host: Host? = null

    override val canIntercept: Boolean
        get() = host != null && errorFromSaveCellularTraffic && request != null

    override fun onClick(v: View): Boolean {
        if (!canIntercept) return false
        host ?: return false
        val request = request ?: return false
        val newRequest = request.newRequest {
            ignoreSaveCellularTraffic(true)
        }
        SketchUtils.getSketch(v)?.enqueue(newRequest)
        return true
    }

    override fun onRequestStart(request: ImageRequest) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }

    override fun onRequestError(request: ImageRequest, error: ImageResult.Error) {
        errorFromSaveCellularTraffic =
            isCausedBySaveCellularTraffic(error.request, error.throwable)
        if (errorFromSaveCellularTraffic) {
            this.request = request
        } else {
            this.request = null
        }
    }

    override fun onRequestSuccess(request: ImageRequest, result: ImageResult.Success) {
        errorFromSaveCellularTraffic = false
        this.request = null
    }
}