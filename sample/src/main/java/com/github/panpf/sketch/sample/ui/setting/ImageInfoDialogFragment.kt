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
package com.github.panpf.sketch.sample.ui.setting

import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toRect
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.displayResult
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.ImageInfoDialogBinding
import com.github.panpf.sketch.sample.ui.base.BindingDialogFragment
import com.github.panpf.sketch.sample.util.format
import com.github.panpf.sketch.util.findLastSketchDrawable
import com.github.panpf.sketch.zoom.SketchZoomImageView
import com.github.panpf.tools4j.io.ktx.formatFileSize
import com.github.panpf.tools4k.lang.asOrThrow

class ImageInfoDialogFragment : BindingDialogFragment<ImageInfoDialogBinding>() {

    private val args by navArgs<ImageInfoDialogFragmentArgs>()

    override fun onViewCreated(binding: ImageInfoDialogBinding, savedInstanceState: Bundle?) {
        binding.imageInfoUriContent.text = args.uri
        binding.imageInfoOptionsContent.text = args.optionsInfo

        binding.imageInfoThrowableContent.text = args.throwableString
        binding.imageInfoImageContent.text = args.imageInfo
        binding.imageInfoDrawableContent.text = args.drawableInfo
        binding.imageInfoDataFromContent.text = args.dataFromInfo
        binding.imageInfoTransformedContent.text = args.transformedInfo
        binding.imageInfoZoomContent.text = args.zoomInfo
        binding.imageInfoTilesContent.text = args.tilesInfo

        binding.imageInfoThrowableContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString != null
        binding.imageInfoImageContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.imageInfoDrawableContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.imageInfoDataFromContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.imageInfoTransformedContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null
        binding.imageInfoZoomContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null && args.zoomInfo != null
        binding.imageInfoTilesContent.parent.asOrThrow<ViewGroup>().isVisible =
            args.throwableString == null && args.tilesInfo != null

        dialogHeightRatio = when {
            args.throwableString != null -> 0.45f
            arrayOf(args.zoomInfo, args.tilesInfo).all { it?.isNotEmpty() == true } -> 0.8f
            args.zoomInfo?.isNotEmpty() == true -> 0.7f
            else -> 0.45f
        }
    }

    companion object {
        fun createDirectionsFromImageView(
            imageView: ImageView,
            uri: String?,
        ): NavDirections {
            var uri1: String? = uri
            var imageInfo: String? = null
            var drawableInfo: String? = null
            var optionsInfo: String? = null
            var dataFromInfo: String? = null
            var transformedInfo: String? = null
            var zoomInfo: String? = null
            var tilesInfo: String? = null
            var throwableString: String? = null
            val displayResult = imageView.displayResult
            if (displayResult is DisplayResult.Success) {
                val sketchDrawable = displayResult.drawable.findLastSketchDrawable()!!
                uri1 = sketchDrawable.imageUri
                imageInfo = sketchDrawable.imageInfo.run {
                    "${width}x${height}, ${mimeType}, ${exifOrientationName(exifOrientation)}"
                }

                val keyUri = sketchDrawable.requestKey.toUri()
                optionsInfo = keyUri.queryParameterNames.mapNotNull {
                    if (it.startsWith("_")) {
                        val value = keyUri.getQueryParameter(it)
                        "$it=$value"
                    } else {
                        null
                    }
                }.joinToString(separator = "\n")

                drawableInfo = sketchDrawable.bitmapInfo.run {
                    "${width}x${height}, ${config}, ${byteCount.toLong().formatFileSize()}"
                }

                dataFromInfo = sketchDrawable.dataFrom.name

                transformedInfo = sketchDrawable.transformedList
                    ?.joinToString(separator = "\n") { transformed ->
                        transformed.replace("Transformed", "")
                    }
            } else if (displayResult is DisplayResult.Error) {
                uri1 = displayResult.request.uriString

                val keyUri = displayResult.request.key.toUri()
                optionsInfo = keyUri.queryParameterNames.mapNotNull {
                    if (it.startsWith("_")) {
                        val value = keyUri.getQueryParameter(it)
                        "$it=$value"
                    } else {
                        null
                    }
                }.joinToString(separator = "\n")

                throwableString = displayResult.exception.toString()
            }

            if (imageView is SketchZoomImageView) {
                zoomInfo = buildList {
                    add("view=${imageView.width}x${imageView.height}")
                    add("draw=${RectF().apply { imageView.getDrawRect(this) }.toRect()}")
                    add("visible=${Rect().apply { imageView.getVisibleRect(this) }}")
                    add(
                        "nowScale=${imageView.scale.format(2)}(${imageView.baseScale.format(2)},${
                            imageView.supportScale.format(2)
                        })"
                    )
                    add("minScale=${imageView.minScale.format(2)}")
                    add("maxScale=${imageView.maxScale.format(2)}")
                    val stepScales = imageView.stepScales
                        ?.joinToString(prefix = "[", postfix = "]") { it.format(2).toString() }
                    add("stepScales=${stepScales}")
                    add("rotateDegrees=${imageView.rotateDegrees}")
                    add(
                        "horScroll(left/right)=${imageView.canScrollHorizontally(-1)},${
                            imageView.canScrollHorizontally(1)
                        }"
                    )
                    add(
                        "verScroll(up/down)=${imageView.canScrollVertically(-1)},${
                            imageView.canScrollVertically(1)
                        }"
                    )
                    add("ScrollEdge(hor/ver)=${imageView.horScrollEdge},${imageView.verScrollEdge}")
                }.joinToString(separator = "\n")

                tilesInfo = imageView.tileList?.takeIf { it.isNotEmpty() }?.let {
                    buildList {
                        add("tileCount=${it.size}")
                        add("validTileCount=${it.count { it.bitmap != null }}")
                        val tilesByteCount = it.sumOf { it.bitmap?.byteCount ?: 0 }
                            .toLong().formatFileSize()
                        add("tilesByteCount=${tilesByteCount}")
                    }.joinToString(separator = "\n")
                }
            }

            return NavMainDirections.actionGlobalImageInfoDialogFragment(
                uri = uri1,
                imageInfo = imageInfo,
                drawableInfo = drawableInfo,
                optionsInfo = optionsInfo,
                dataFromInfo = dataFromInfo,
                transformedInfo = transformedInfo,
                zoomInfo = zoomInfo,
                tilesInfo = tilesInfo,
                throwableString = throwableString
            )
        }
    }
}