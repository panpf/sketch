package me.panpf.sketch.sample.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.tools4a.display.ktx.isOrientationPortrait
import com.github.panpf.tools4j.math.ktx.divide
import com.github.panpf.tools4k.lang.asOrThrow
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.bean.GiphyGif
import me.panpf.sketch.sample.databinding.ListItemImageStaggeredBinding
import me.panpf.sketch.sample.widget.SampleImageView

class GiphyGifFlexboxItemFactory(
    private val onClickPhoto: (view: SampleImageView, position: Int, data: GiphyGif) -> Unit
) : BindingItemFactory<GiphyGif, ListItemImageStaggeredBinding>(GiphyGif::class) {

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ListItemImageStaggeredBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ListItemImageStaggeredBinding,
        item: BindingItem<GiphyGif, ListItemImageStaggeredBinding>
    ) {
        binding.imageStaggeredImageItem.apply {
            setOptions(ImageOptions.RECT)
            page = SampleImageView.Page.SEARCH_LIST
            layoutParams!!.asOrThrow<FlexboxLayoutManager.LayoutParams>().flexGrow = 1.0f
            setOnClickListener {
                onClickPhoto(
                    binding.imageStaggeredImageItem,
                    item.absoluteAdapterPosition,
                    item.dataOrThrow
                )
            }
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemImageStaggeredBinding,
        item: BindingItem<GiphyGif, ListItemImageStaggeredBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: GiphyGif
    ) {
        val previewGif = data.images?.previewGif
        binding.imageStaggeredImageItem.apply {
            updateLayoutParams<ViewGroup.LayoutParams> {
                val screenHeight = context.resources.displayMetrics.heightPixels
                val previewAspectRatio =
                    previewGif?.height?.toFloat().divide(previewGif?.width?.toFloat())
                height = screenHeight / (if (context.isOrientationPortrait()) 5 else 2)
                width = (height / previewAspectRatio).toInt()
            }
        }

        binding.imageStaggeredImageItem.displayImage(
            previewGif?.getDownloadUrl().orEmpty()
        )
    }
}
