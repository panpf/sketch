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

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import java.io.File

/**
 * Load the image from [uri] and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayImage(
    uri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    context.sketch.enqueue(DisplayRequest(this, uri, configBlock))

/**
 * Load the image from [uri] and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayImage(
    uri: Uri?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    displayImage(uri?.toString(), configBlock)

/**
 * Load the image from drawable res and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayImage(
    @DrawableRes drawableResId: Int?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    displayImage(drawableResId?.let { newResourceUri(it) }, configBlock)

/**
 * Load the image from local file and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayImage(
    file: File?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    displayImage(file?.let { newFileUri(it.path) }, configBlock)

/**
 * Load the image from app assets and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayAssetImage(
    assetFileName: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    displayImage(assetFileName?.let { newAssetUri(assetFileName) }, configBlock)

/**
 * Load the image from drawable res and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayResourceImage(
    @DrawableRes drawableResId: Int?,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    displayImage(drawableResId?.let { newResourceUri(it) }, configBlock)

/**
 * Load the image from drawable res and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayResourceImage(
    packageName: String,
    @DrawableRes drawableResId: Int,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    displayImage(newResourceUri(packageName, drawableResId), configBlock)