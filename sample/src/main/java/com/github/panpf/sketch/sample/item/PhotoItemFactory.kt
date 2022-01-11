package com.github.panpf.sketch.sample.item

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
import com.github.panpf.sketch.request.saveCellularTraffic
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.bean.Photo
import com.github.panpf.sketch.sample.databinding.ItemImageBinding
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
            setClickRedisplayAndIgnoreSaveCellularTraffic(true)
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
                    height = if (itemSize.y != -1) itemSize.y else LayoutParams.WRAP_CONTENT
                }
            }

            displayImage(data.firstThumbnailUrl) {
                disabledAnimationDrawable(context.appSettingsService.disabledAnimatableDrawableInList.value == true)
                saveCellularTraffic(context.appSettingsService.saveCellularTrafficInList.value == true)
                placeholderImage(R.drawable.image_loading)
                errorImage(
                    defaultErrorDrawableResId = R.drawable.image_error,
                    saveCellularTrafficDrawableResId = R.drawable.image_pause_download
                )
            }
        }
    }
}