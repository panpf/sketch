package com.github.panpf.sketch.sample.ui.setting

import android.graphics.Rect
import android.os.Bundle
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.ImageInfoDialogBinding
import com.github.panpf.sketch.sample.ui.base.BindingDialogFragment
import com.github.panpf.sketch.sample.util.format
import com.github.panpf.sketch.util.findLastSketchDrawable
import com.github.panpf.sketch.zoom.SketchZoomImageView
import com.github.panpf.tools4j.io.ktx.formatFileSize

class ImageInfoDialogFragment : BindingDialogFragment<ImageInfoDialogBinding>() {

    private val args by navArgs<ImageInfoDialogFragmentArgs>()

    override fun onViewCreated(binding: ImageInfoDialogBinding, savedInstanceState: Bundle?) {
        binding.imageInfoUriContent.text = args.uri
        binding.imageInfoImageContent.text = args.imageInfo
        binding.imageInfoDrawableContent.text = args.drawableInfo
        binding.imageInfoOptionsContent.text = args.optionsInfo
        binding.imageInfoDataFromContent.text = args.dataFromInfo
        binding.imageInfoTransformedContent.text = args.transformedInfo
        binding.imageInfoZoomContent.text = args.zoomInfo
        binding.imageInfoZoomItem.isVisible = args.zoomInfo != null
        binding.imageInfoTilesContent.text = args.tilesInfo
        binding.imageInfoTilesItem.isVisible = args.tilesInfo != null
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
            val sketchDrawable = imageView.drawable.findLastSketchDrawable()
            if (sketchDrawable != null) {
                uri1 = sketchDrawable.imageUri
                imageInfo = sketchDrawable.imageInfo.run {
                    "${width}x${height}, ${mimeType}, ${exifOrientationName(sketchDrawable.imageExifOrientation)}"
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
                        transformed.toString().replace("Transformed", "")
                    }
            }

            if (imageView is SketchZoomImageView) {
                val zoomAbility = imageView.zoomAbility
                zoomInfo = buildList {
                    add("visible=${Rect().apply { zoomAbility.getVisibleRect(this) }}")
                    add("nowScale=${zoomAbility.scale.format(1)}(${zoomAbility.baseScale.format(1)},${zoomAbility.supportScale.format(1)})")
                    add("minScale=${zoomAbility.minScale.format(1)}")
                    add("maxScale=${zoomAbility.maxScale.format(1)}")
                    val stepScales = zoomAbility.stepScales
                        ?.joinToString(prefix = "[", postfix = "]") { it.format(1).toString() }
                    add("stepScales=${stepScales}")
                    add("rotateDegrees=${zoomAbility.rotateDegrees}")
                    add("horScroll(left/right)=${zoomAbility.canScrollHorizontally(-1)},${zoomAbility.canScrollHorizontally(1)}")
                    add("verScroll(up/down)=${zoomAbility.canScrollVertically(-1)},${zoomAbility.canScrollVertically(1)}")
                    add("ScrollEdge(hor/ver)=${zoomAbility.horScrollEdge},${zoomAbility.verScrollEdge}")
                }.joinToString(separator = "\n")

                tilesInfo = zoomAbility.tileList?.takeIf { it.isNotEmpty() }?.let {
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
                tilesInfo = tilesInfo
            )
        }
    }
}