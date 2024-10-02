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

package com.github.panpf.sketch.request

import android.view.View
import android.widget.ImageView
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.target.ImageViewTarget

/**
 * Build and set the [ImageRequest], target is an ImageView
 *
 * @see com.github.panpf.sketch.view.core.test.request.ImageRequestViewTest.testImageRequest
 */
fun ImageRequest(
    imageView: ImageView,
    uri: String?,
    configBlock: (ImageRequest.Builder.() -> Unit)? = null
): ImageRequest = ImageRequest.Builder(imageView.context, uri).apply {
    target(imageView)
    configBlock?.invoke(this)
}.build()


/**
 * Set the target to the ImageView
 *
 * @see com.github.panpf.sketch.view.core.test.request.ImageRequestViewTest.testTarget
 */
fun ImageRequest.Builder.target(imageView: ImageView): ImageRequest.Builder = apply {
    target(ImageViewTarget(imageView))
}


/**
 * Set the resize size
 *
 * @see com.github.panpf.sketch.view.core.test.request.ImageRequestViewTest.testSizeWithView
 */
fun ImageRequest.Builder.sizeWithView(view: View, subtractPadding: Boolean = true): ImageRequest.Builder =
    apply {
        size(ViewSizeResolver(view, subtractPadding))
    }