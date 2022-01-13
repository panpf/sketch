package com.github.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.decode.video.videoFramePercentDuration
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.extensions.pauseLoadWhenScrolling
import com.github.panpf.sketch.extensions.pauseLoadWhenScrollingErrorImage
import com.github.panpf.sketch.extensions.saveCellularTraffic
import com.github.panpf.sketch.extensions.saveCellularTrafficErrorImage
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.bean.VideoInfo
import com.github.panpf.sketch.sample.databinding.ItemVideoBinding

class LocalVideoItemFactory :
    BindingItemFactory<VideoInfo, ItemVideoBinding>(VideoInfo::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ItemVideoBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ItemVideoBinding,
        item: BindingItem<VideoInfo, ItemVideoBinding>
    ) {
    }

    override fun bindItemData(
        context: Context,
        binding: ItemVideoBinding,
        item: BindingItem<VideoInfo, ItemVideoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: VideoInfo
    ) {
        binding.videoItemIconImage.displayImage(data.path) {
            disabledAnimationDrawable(context.appSettingsService.disabledAnimatableDrawableInList.value == true)
            pauseLoadWhenScrolling(context.appSettingsService.pauseLoadWhenScrollInList.value == true)
            saveCellularTraffic(context.appSettingsService.saveCellularTrafficInList.value == true)
            placeholderImage(R.drawable.im_placeholder)
            videoFramePercentDuration(0.5f)
            errorImage(R.drawable.im_error) {
                saveCellularTrafficErrorImage(R.drawable.im_save_cellular_traffic)
                pauseLoadWhenScrollingErrorImage()
            }
        }
        binding.videoItemNameText.text = data.title
        binding.videoItemSizeText.text = data.getTempFormattedSize(context)
        binding.videoItemDateText.text = data.tempFormattedDate
        binding.videoItemDurationText.text = data.tempFormattedDuration
    }
}
