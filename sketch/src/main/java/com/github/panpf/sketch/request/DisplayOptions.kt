/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.display.ImageDisplayer
import com.github.panpf.sketch.shaper.ImageShaper
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.StateImage

class DisplayOptions : LoadOptions {
    /**
     * Disabled memory caching
     */
    var isCacheInMemoryDisabled = false

    /**
     * Placeholder image displayed while loading
     */
    var loadingImage: StateImage? = null

    /**
     * Show this image when loading fails
     */
    var errorImage: StateImage? = null

    /**
     * Show this image when pausing a download
     */
    var pauseDownloadImage: StateImage? = null

    /**
     * Modify the shape of the image when drawing
     */
    var shaper: ImageShaper? = null

    /**
     * Modify the size of the image when drawing
     */
    var shapeSize: ShapeSize? = null

    /**
     * Display image after image loading is completeThe, default value is [com.github.panpf.sketch.display.DefaultImageDisplayer]
     */
    var displayer: ImageDisplayer? = null

    constructor()

    constructor(from: DisplayOptions) {
        copy(from)
    }

    fun loadingImage(@DrawableRes drawableResId: Int): DisplayOptions {
        loadingImage = DrawableStateImage(drawableResId)
        return this
    }

    fun errorImage(@DrawableRes drawableResId: Int): DisplayOptions {
        errorImage = DrawableStateImage(drawableResId)
        return this
    }

    fun pauseDownloadImage(@DrawableRes drawableResId: Int): DisplayOptions {
        pauseDownloadImage = DrawableStateImage(drawableResId)
        return this
    }

    fun shapeSize(shapeWidth: Int, shapeHeight: Int): DisplayOptions {
        shapeSize = ShapeSize(shapeWidth, shapeHeight, null)
        return this
    }

    fun shapeSize(shapeWidth: Int, shapeHeight: Int, scaleType: ScaleType?): DisplayOptions {
        shapeSize = ShapeSize(shapeWidth, shapeHeight, scaleType)
        return this
    }

    override fun reset() {
        super.reset()
        isCacheInMemoryDisabled = false
        displayer = null
        loadingImage = null
        errorImage = null
        pauseDownloadImage = null
        shaper = null
        shapeSize = null
    }

    fun copy(options: DisplayOptions) {
        super.copy(options as LoadOptions)
        isCacheInMemoryDisabled = options.isCacheInMemoryDisabled
        displayer = options.displayer
        loadingImage = options.loadingImage
        errorImage = options.errorImage
        pauseDownloadImage = options.pauseDownloadImage
        shaper = options.shaper
        shapeSize = options.shapeSize
    }
}