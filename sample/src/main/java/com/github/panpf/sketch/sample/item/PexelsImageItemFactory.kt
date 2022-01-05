package com.github.panpf.sketch.sample.item

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.bean.PexelsPhoto
import com.github.panpf.sketch.sample.databinding.ItemPexelsImageBinding
import com.github.panpf.sketch.sample.widget.MyImageView
import com.github.panpf.sketch.stateimage.StateImage
import com.github.panpf.tools4a.display.Displayx
import com.github.panpf.tools4a.display.ktx.isOrientationPortrait

class PexelsImageItemFactory(
    activity: Activity,
    private val onClickPhoto: (view: MyImageView, position: Int, data: PexelsPhoto) -> Unit
) : BindingItemFactory<PexelsPhoto, ItemPexelsImageBinding>(PexelsPhoto::class) {

    // + DeviceUtils.getNavigationBarHeight(requireActivity()) for MIX 2
    private val windowHeightSupplement =
        if (activity.window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
            Displayx.getNavigationBarHeight(activity)
        } else {
            0
        }

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemPexelsImageBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemPexelsImageBinding,
        item: BindingItem<PexelsPhoto, ItemPexelsImageBinding>
    ) {
        binding.pexelsImageItemImageView.apply {
//            setShowGifFlagEnabled(R.drawable.ic_gif)
//            setOptions(ImageOptions.LIST_FULL)
//            setOnClickListener {
//                onClickPhoto(
//                    binding.imageUnsplashImageItem,
//                    item.absoluteAdapterPosition,
//                    item.dataOrThrow
//                )
//            }
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
        val itemWidth = context.resources.displayMetrics.widthPixels

        binding.root.updateLayoutParams<ViewGroup.LayoutParams> {
            width = itemWidth
            if (!context.isOrientationPortrait()) {
                width += windowHeightSupplement
            }
            height = (width / (data.width / data.height.toFloat())).toInt()
        }

        binding.pexelsImageItemImageView.apply {
            updateLayoutParams<ViewGroup.LayoutParams> {
                width = itemWidth
                if (!context.isOrientationPortrait()) {
                    width += windowHeightSupplement
                }
                height = (width / (data.width / data.height.toFloat())).toInt()
            }
            displayImage(data.src.large) {
                loadingImage(StateImage.color(Color.parseColor(data.avgColor)))
            }
        }
    }
}