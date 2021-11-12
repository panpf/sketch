package com.github.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.bean.VideoInfo
import com.github.panpf.sketch.sample.databinding.ListItemMyVideoBinding
import com.github.panpf.sketch.sample.util.VideoThumbnailUriModel

class LocalVideoItemFactory :
    BindingItemFactory<VideoInfo, ListItemMyVideoBinding>(VideoInfo::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ListItemMyVideoBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ListItemMyVideoBinding,
        item: BindingItem<VideoInfo, ListItemMyVideoBinding>
    ) {
        binding.imageMyVideoItemIcon.options.apply {
            loadingImage(R.drawable.image_loading)
            displayer = TransitionImageDisplayer()
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemMyVideoBinding,
        item: BindingItem<VideoInfo, ListItemMyVideoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: VideoInfo
    ) {
        binding.imageMyVideoItemIcon.displayImage(VideoThumbnailUriModel.makeUri(data.path ?: ""))
        binding.textMyVideoItemName.text = data.title
        binding.textMyVideoItemSize.text = data.getTempFormattedSize(context)
        binding.textMyVideoItemDate.text = data.tempFormattedDate
        binding.textMyVideoItemDuration.text = data.tempFormattedDuration
    }
}
