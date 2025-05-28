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

package com.github.panpf.sketch.sample.ui.components.zoomimage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.github.panpf.zoomimage.compose.util.rtlFlipped
import com.github.panpf.zoomimage.compose.util.toCompat
import com.github.panpf.zoomimage.compose.zoom.ZoomableState
import com.github.panpf.zoomimage.compose.zoom.zoomable
import com.github.panpf.zoomimage.zoom.internal.calculateBaseTransform

/**
 * A Modifier that can recognize gestures such as click, long press, double-click, one-finger zoom, two-finger zoom, drag, fling, etc.,
 * and then apply the gesture changes to the component. It can be used on any composable component.
 *
 * Since it consumes all gestures, [Modifier.clickable] and [Modifier.combinedClickable] will not work.
 * You can pass [onTap] and [onLongPress] parameters instead.
 *
 * If the zoomed content does not fill the container, you can set the size, scaling and alignment of the content through
 * the contentSize, contentScale, and alignment properties of [ZoomableState]. This will only translate within the content area after scaling.
 */
fun Modifier.zoom2(
    zoomable: ZoomableState,
    userSetupContentSize: Boolean = false,
    firstRestoreContentBaseTransform: Boolean = false,
    onLongPress: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
): Modifier = this
    .zoomable(
        zoomable = zoomable,
        userSetupContentSize = userSetupContentSize,
        onLongPress = onLongPress,
        onTap = onTap
    )
    .zooming2(zoomable, firstRestoreContentBaseTransform)

/**
 * A Modifier that applies changes in [ZoomableState].transform to the component. It can be used on any composable component.
 */
fun Modifier.zooming2(
    zoomable: ZoomableState,
    firstRestoreContentBaseTransform: Boolean = false,
): Modifier = this
    .clipToBounds()
    .graphicsLayer {
        val transform = zoomable.transform
        zoomable.logger.v { "ZoomableState. graphicsLayer. transform=$transform" }
        scaleX = transform.scaleX
        scaleY = transform.scaleY
        translationX = transform.offsetX
        translationY = transform.offsetY
        transformOrigin = transform.scaleOrigin
    }
    // Because rotationOrigin and rotationOrigin are different, they must be set separately.
    .graphicsLayer {
        val transform = zoomable.transform
        rotationZ = transform.rotation
        transformOrigin = transform.rotationOrigin
    }
    .let {
        // ZoomImage's zoom is located in the upper left corner based on the image in its original size, so you must first restore the zoom and offset of the image.
        if (firstRestoreContentBaseTransform) {
            it.graphicsLayer {
                val baseTransform = calculateBaseTransform(
                    containerSize = zoomable.containerSize.toCompat(),
                    contentSize = zoomable.contentSize.toCompat(),
                    contentScale = zoomable.contentScale.toCompat(),
                    alignment = zoomable.alignment.rtlFlipped(zoomable.layoutDirection).toCompat(),
                    rotation = 0
                )
                scaleX = 1f / baseTransform.scaleX
                scaleY = 1f / baseTransform.scaleY
                translationX = 0f - (baseTransform.offsetX * (1f / baseTransform.scaleX))
                translationY = 0f - (baseTransform.offsetY * (1f / baseTransform.scaleY))
                transformOrigin = TransformOrigin(0f, 0f)
            }
        } else {
            it
        }
    }