package com.github.panpf.sketch.sample.ui.photo

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.updateDisplayImageOptions
import com.github.panpf.sketch.resize.Precision.EXACTLY
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.longImageClipPrecision
import com.github.panpf.sketch.resize.longImageScale
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.databinding.ItemImageBinding
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.pauseLoadWhenScrollingError
import com.github.panpf.sketch.stateimage.saveCellularTrafficError
import com.github.panpf.sketch.viewability.setClickIgnoreSaveCellularTrafficEnabled
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlin.math.roundToInt

class PhotoItemFactory : BindingItemFactory<Photo, ItemImageBinding>(Photo::class) {

    private var itemSize: Point? = null

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemImageBinding {
        if (itemSize == null && parent is RecyclerView) {
            val screenWidth = context.getScreenWidth()
            val gridDivider = context.resources.getDimensionPixelSize(R.dimen.grid_divider)
            itemSize = when (val layoutManager = parent.layoutManager) {
                is GridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val itemSize1 = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                    Point(itemSize1, itemSize1)
                }
                is StaggeredGridLayoutManager -> {
                    val spanCount = layoutManager.spanCount
                    val itemSize1 = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                    Point(itemSize1, -1)
                }
                else -> {
                    Point(screenWidth, -1)
                }
            }
        }
        return ItemImageBinding.inflate(inflater, parent, false)
    }

    override fun initItem(
        context: Context,
        binding: ItemImageBinding,
        item: BindingItem<Photo, ItemImageBinding>
    ) {

        binding.imageItemImageView.apply {
            setClickIgnoreSaveCellularTrafficEnabled(true)
            updateDisplayImageOptions {
                placeholder(
                    IconStateImage(R.drawable.ic_image_outline, ResColor(R.color.placeholder_bg))
                )
                error(
                    IconStateImage(R.drawable.ic_error, ResColor(R.color.placeholder_bg))
                ) {
                    saveCellularTrafficError(
                        IconStateImage(
                            R.drawable.ic_signal_cellular,
                            ResColor(R.color.placeholder_bg)
                        )
                    )
                    pauseLoadWhenScrollingError()
                }
                crossfade()
                resizeApplyToDrawable()
            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemImageBinding,
        item: BindingItem<Photo, ItemImageBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Photo
    ) {
        binding.imageItemImageView.apply {
            updateLayoutParams<LayoutParams> {
                val photoWidth = data.width
                val photoHeight = data.height
                val itemSize = itemSize!!
                if (photoWidth != null && photoHeight != null) {
                    width = itemSize.x
                    height = if (itemSize.y == -1) {
                        val previewAspectRatio = photoWidth.toFloat() / photoHeight.toFloat()
                        (itemSize.x / previewAspectRatio).roundToInt()
                    } else {
                        itemSize.y
                    }
                } else {
                    width = itemSize.x
                    height = itemSize.x
                }
            }

            displayImage(data.firstThumbnailUrl) {
                resizeScale(
                    longImageScale(
                        Scale.valueOf(appSettingsService.longImageResizeScale.value),
                        Scale.valueOf(appSettingsService.otherResizeScale.value)
                    )
                )
                when (appSettingsService.resizePrecision.value) {
                    "LESS_PIXELS" -> {
                        resizePrecision(precision = LESS_PIXELS)
                    }
                    "SAME_ASPECT_RATIO" -> {
                        resizePrecision(precision = SAME_ASPECT_RATIO)
                    }
                    "EXACTLY" -> {
                        resizePrecision(precision = EXACTLY)
                    }
                    "LONG_IMAGE_CROP" -> {
                        resizePrecision(longImageClipPrecision(precision = SAME_ASPECT_RATIO))
                    }
                    "ORIGINAL" -> {
                        resizeSize(-1, -1)
                    }
                }
            }
        }
    }
}