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
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.appSettingsService
import me.panpf.sketch.sample.bean.GiphyGif
import me.panpf.sketch.sample.databinding.ListItemImageStaggeredBinding
import me.panpf.sketch.sample.image.ImageOptions
import me.panpf.sketch.sample.util.observeFromViewAndInit
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
            setShowGifFlagEnabled(R.drawable.ic_gif)
            setOptions(ImageOptions.RECT)
            layoutParams!!.asOrThrow<FlexboxLayoutManager.LayoutParams>().flexGrow = 1.0f
            setOnClickListener {
                onClickPhoto(
                    binding.imageStaggeredImageItem,
                    item.absoluteAdapterPosition,
                    item.dataOrThrow
                )
            }

            appSettingsService.showPressedStatusInListEnabled.observeFromViewAndInit(this) {
                isShowPressedStatusEnabled = it == true
            }

            appSettingsService.showImageDownloadProgressEnabled.observeFromViewAndInit(this) {
                isShowDownloadProgressEnabled = it == true
            }

            appSettingsService.playGifInListEnabled.observeFromViewAndInit(this) {
                options.isDecodeGifImage = it == true
                val data = item.dataOrNull
                if (data != null) {
                    bindItemData(
                        context, binding, item,
                        item.bindingAdapterPosition, item.absoluteAdapterPosition, data
                    )
                }
            }

            appSettingsService.clickPlayGifEnabled.observeFromViewAndInit(this) {
                setClickPlayGifEnabled(if (it == true) R.drawable.ic_play else 0)
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
        val previewGif = data.images.previewGif
        binding.imageStaggeredImageItem.apply {
            updateLayoutParams<ViewGroup.LayoutParams> {
                val screenHeight = context.resources.displayMetrics.heightPixels
                val previewAspectRatio =
                    previewGif.height.toFloat().divide(previewGif.width.toFloat())
                height = screenHeight / (if (context.isOrientationPortrait()) 5 else 2)
                width = (height / previewAspectRatio).toInt()
            }
            displayImage(previewGif.downloadUrl)
        }
    }
}
