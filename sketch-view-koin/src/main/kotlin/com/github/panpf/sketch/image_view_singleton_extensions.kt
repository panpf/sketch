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

package com.github.panpf.sketch

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.request.ImageRequest
import org.koin.mp.KoinPlatform
import java.io.File

/**
 * Load the image from [uri] and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadImage(
    uri: String?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = KoinPlatform.getKoin().get<Sketch>().enqueue(ImageRequest(this, uri, block))

/**
 * Load the image from [uri] and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadImage(
    uri: Uri?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(uri?.toString(), block)

/**
 * Load the image from drawable res and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadImage(
    @DrawableRes resId: Int?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(resId?.let { newResourceUri(it) }, block)

/**
 * Load the image from local file and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadImage(
    file: File?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(file?.let { newFileUri(it.path) }, block)

/**
 * Load the image from app assets and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadAssetImage(
    fileName: String?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(fileName?.let { newAssetUri(fileName) }, block)

/**
 * Load the image from drawable res and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadResourceImage(
    @DrawableRes resId: Int?,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(resId?.let { newResourceUri(it) }, block)

/**
 * Load the image from drawable res and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [block]
 *
 * @see com.github.panpf.sketch.view.koin.test.ImageViewSingletonExtensionsTest.testLoadImage
 */
fun ImageView.loadResourceImage(
    packageName: String,
    @DrawableRes resId: Int,
    block: (ImageRequest.Builder.() -> Unit)? = null
): Disposable = loadImage(newResourceUri(packageName, resId), block)