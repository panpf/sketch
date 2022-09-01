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
package com.github.panpf.sketch.viewability

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.request.DisplayListenerProvider
import com.github.panpf.sketch.request.DisplayRequest

/**
 * Provides access services for ViewAbility registration, uninstallation, and event callbacks and properties
 */
interface ViewAbilityContainer : DisplayListenerProvider {

    /**
     * ViewAbility List
     */
    val viewAbilityList: List<ViewAbility>

    /**
     * Add a ViewAbility and run it
     */
    fun addViewAbility(viewAbility: ViewAbility)

    /**
     * Delete a ViewAbility
     */
    fun removeViewAbility(viewAbility: ViewAbility)

    /**
     * Call the parent class's setOnClickListener() method
     */
    fun superSetOnClickListener(listener: OnClickListener?)

    /**
     * Call the parent class's setOnLongClickListener() method
     */
    fun superSetOnLongClickListener(listener: OnLongClickListener?)

    /**
     * Call the parent class's setScaleType() method
     */
    fun superSetScaleType(scaleType: ScaleType)

    /**
     * Call the parent class's getScaleType() method
     */
    fun superGetScaleType(): ScaleType

    /**
     * Call the parent class's setImageMatrix() method
     */
    fun superSetImageMatrix(matrix: Matrix?)

    /**
     * Call the parent class's getImageMatrix() method
     */
    fun superGetImageMatrix(): Matrix?

    /**
     * Get Drawable
     */
    fun getDrawable(): Drawable?

    /**
     * Submit an display image request
     */
    fun submitRequest(request: DisplayRequest)
}