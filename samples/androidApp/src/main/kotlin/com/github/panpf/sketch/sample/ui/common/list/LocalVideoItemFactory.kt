package com.github.panpf.sketch.sample.ui.common.list

import android.content.Context
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.updateImageOptions
import com.github.panpf.sketch.request.videoFramePercent
import com.github.panpf.sketch.sample.databinding.ListItemVideoBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingItemFactory
import com.github.panpf.sketch.sample.ui.model.VideoInfo

class LocalVideoItemFactory :
    BaseBindingItemFactory<VideoInfo, ListItemVideoBinding>(VideoInfo::class) {

    override fun initItem(
        context: Context,
        binding: ListItemVideoBinding,
        item: BindingItem<VideoInfo, ListItemVideoBinding>
    ) {
        binding.thumbnailImage.updateImageOptions {
            videoFramePercent(0.5f)
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemVideoBinding,
        item: BindingItem<VideoInfo, ListItemVideoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: VideoInfo
    ) {
        binding.thumbnailImage.loadImage(data.uri)
        binding.nameText.text = data.title
        binding.sizeText.text = data.getTempFormattedSize(context)
        binding.dateText.text = data.tempFormattedDate
        binding.durationText.text = data.tempFormattedDuration
    }
}