package com.github.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.bean.ImageInfo
import com.github.panpf.sketch.sample.databinding.ItemMyPhotoBinding
import com.github.panpf.sketch.sample.widget.MyImageView
import com.github.panpf.tools4a.display.ktx.getScreenWidth

class LocalPhotoItemFactory(
    private val onClickPhoto: (view: MyImageView, position: Int, data: ImageInfo) -> Unit
) : BindingItemFactory<ImageInfo, ItemMyPhotoBinding>(ImageInfo::class) {

    private var itemSize: Int = 0

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemMyPhotoBinding {
        if (itemSize == 0) {
            itemSize = -1
            if (parent is RecyclerView) {
                val spanCount = when (val layoutManager = parent.layoutManager) {
                    is GridLayoutManager -> layoutManager.spanCount
                    is StaggeredGridLayoutManager -> layoutManager.spanCount
                    else -> 1
                }
                if (spanCount > 1) {
                    val screenWidth = context.getScreenWidth()
                    val gridDivider = context.resources.getDimensionPixelSize(R.dimen.grid_divider)
                    itemSize = (screenWidth - (gridDivider * (spanCount + 1))) / spanCount
                }
            }
        }
        return ItemMyPhotoBinding.inflate(inflater, parent, false)
    }

    override fun initItem(
        context: Context,
        binding: ItemMyPhotoBinding,
        item: BindingItem<ImageInfo, ItemMyPhotoBinding>
    ) {
        binding.myPhotoItemImageView.apply {
            if (itemSize > 0) {
                updateLayoutParams<ViewGroup.LayoutParams> {
                    width = itemSize
                    height = itemSize
                }
            }

//            setShowGifFlagEnabled(R.drawable.ic_gif)

            setOnClickListener {
                onClickPhoto(
                    binding.myPhotoItemImageView,
                    item.absoluteAdapterPosition,
                    item.dataOrThrow
                )
            }

//            appSettingsService.showRoundedInPhotoListEnabled.observeFromViewAndInit(this) {
//                if (it == true) {
//                    setOptions(ImageOptions.ROUND_RECT)
//                } else {
//                    setOptions(ImageOptions.RECT)
//                }
//                val data = item.dataOrNull
//                if (data != null) {
//                    bindItemData(
//                        context, binding, item,
//                        item.bindingAdapterPosition, item.absoluteAdapterPosition, data
//                    )
//                }
//            }
//
//            appSettingsService.showPressedStatusInListEnabled.observeFromViewAndInit(this) {
//                isShowPressedStatusEnabled = it == true
//            }
//
//            appSettingsService.playGifInListEnabled.observeFromViewAndInit(this) {
//                options.isDecodeGifImage = it == true
//                val data = item.dataOrNull
//                if (data != null) {
//                    bindItemData(
//                        context, binding, item,
//                        item.bindingAdapterPosition, item.absoluteAdapterPosition, data
//                    )
//                }
//            }
//
//            appSettingsService.clickPlayGifEnabled.observeFromViewAndInit(this) {
//                setClickPlayGifEnabled(if (it == true) R.drawable.ic_play else 0)
//            }
//
//            appSettingsService.thumbnailModeEnabled.observeFromViewAndInit(this) {
//                val thumbnailMode = it == true
//                options.isThumbnailMode = thumbnailMode
//                if (thumbnailMode) {
//                    options.resize = Resize.byViewFixedSize()
//                } else {
//                    options.resize = null
//                }
//                if (isAttachedToWindow) {
//                    redisplay { _, cacheOptions ->
//                        cacheOptions.isThumbnailMode = thumbnailMode
//                        if (thumbnailMode) {
//                            cacheOptions.resize = Resize.byViewFixedSize()
//                        } else {
//                            cacheOptions.resize = null
//                        }
//                    }
//                }
//                val data = item.dataOrNull
//                if (data != null) {
//                    bindItemData(
//                        context, binding, item,
//                        item.bindingAdapterPosition, item.absoluteAdapterPosition, data
//                    )
//                }
//            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemMyPhotoBinding,
        item: BindingItem<ImageInfo, ItemMyPhotoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: ImageInfo
    ) {
        binding.myPhotoItemImageView.displayImage(data.path)
    }
}
