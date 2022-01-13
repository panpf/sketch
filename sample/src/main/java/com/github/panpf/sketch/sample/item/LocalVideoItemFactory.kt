package com.github.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.sketch.displayImage
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
//        binding.imageMyVideoItemIcon.options.apply {
//            pl(R.drawable.image_loading)
//        }
    }

    override fun bindItemData(
        context: Context,
        binding: ItemVideoBinding,
        item: BindingItem<VideoInfo, ItemVideoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: VideoInfo
    ) {
        binding.imageMyVideoItemIcon.displayImage(data.path)
        binding.textMyVideoItemName.text = data.title
        binding.textMyVideoItemSize.text = data.getTempFormattedSize(context)
        binding.textMyVideoItemDate.text = data.tempFormattedDate
        binding.textMyVideoItemDuration.text = data.tempFormattedDuration
    }
}
