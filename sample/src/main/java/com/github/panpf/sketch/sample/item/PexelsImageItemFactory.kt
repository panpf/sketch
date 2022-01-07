package com.github.panpf.sketch.sample.item

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.bean.PexelsPhoto
import com.github.panpf.sketch.sample.databinding.ItemPexelsImageBinding
import com.github.panpf.sketch.sample.widget.MyImageView
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.tools4a.display.ktx.getScreenWidth

class PexelsImageItemFactory(
    activity: Activity,
    private val onClickPhoto: (view: MyImageView, position: Int, data: PexelsPhoto) -> Unit
) : BindingItemFactory<PexelsPhoto, ItemPexelsImageBinding>(PexelsPhoto::class) {

    private var itemSize: Int = 0

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemPexelsImageBinding {
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
        return ItemPexelsImageBinding.inflate(inflater, parent, false)
    }

    override fun initItem(
        context: Context,
        binding: ItemPexelsImageBinding,
        item: BindingItem<PexelsPhoto, ItemPexelsImageBinding>
    ) {
        binding.pexelsImageItemImageView.apply {
            if (itemSize > 0) {
                updateLayoutParams<ViewGroup.LayoutParams> {
                    width = itemSize
                    height = itemSize
                }
            }
//            setShowGifFlagEnabled(R.drawable.ic_gif)
//            setOptions(ImageOptions.LIST_FULL)
            setOnClickListener {
                onClickPhoto(
                    binding.pexelsImageItemImageView,
                    item.absoluteAdapterPosition,
                    item.dataOrThrow
                )
            }
//
//            appSettingsService.showPressedStatusInListEnabled.observeFromViewAndInit(this) {
//                isShowPressedStatusEnabled = it == true
//            }
//
//            appSettingsService.showImageDownloadProgressEnabled.observeFromViewAndInit(this) {
//                isShowDownloadProgressEnabled = it == true
//            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemPexelsImageBinding,
        item: BindingItem<PexelsPhoto, ItemPexelsImageBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: PexelsPhoto
    ) {
        binding.pexelsImageItemImageView.displayImage(data.src.large) {
            placeholderImage(StateImage.color(Color.parseColor(data.avgColor)))
        }
    }
}