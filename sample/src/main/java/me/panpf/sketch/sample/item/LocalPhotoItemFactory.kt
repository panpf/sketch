package me.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.ImageInfo
import me.panpf.sketch.sample.databinding.ListItemMyPhotoBinding
import me.panpf.sketch.sample.widget.SampleImageView

class LocalPhotoItemFactory(
    private val onClickPhoto: (view: SampleImageView, position: Int, data: ImageInfo) -> Unit
) : BindingItemFactory<ImageInfo, ListItemMyPhotoBinding>(ImageInfo::class) {

    private var itemSize: Int = 0

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ListItemMyPhotoBinding {
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
        return ListItemMyPhotoBinding.inflate(inflater, parent, false)
    }

    override fun initItem(
        context: Context,
        binding: ListItemMyPhotoBinding,
        item: BindingItem<ImageInfo, ListItemMyPhotoBinding>
    ) {
        binding.imageMyPhotoItem.apply {
            if (itemSize > 0) {
                updateLayoutParams<ViewGroup.LayoutParams> {
                    width = itemSize
                    height = itemSize
                }
            }

            page = SampleImageView.Page.PHOTO_LIST

            setOnClickListener {
                onClickPhoto(
                    binding.imageMyPhotoItem,
                    item.absoluteAdapterPosition,
                    item.dataOrThrow
                )
            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemMyPhotoBinding,
        item: BindingItem<ImageInfo, ListItemMyPhotoBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: ImageInfo
    ) {
        binding.imageMyPhotoItem.apply {
            if (AppConfig.getBoolean(context, AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST)) {
                setOptions(ImageOptions.ROUND_RECT)
            } else {
                setOptions(ImageOptions.RECT)
            }

            displayImage(data.path)
        }
    }
}
