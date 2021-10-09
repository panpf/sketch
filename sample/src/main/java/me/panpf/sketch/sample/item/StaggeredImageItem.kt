package me.panpf.sketch.sample.item

import android.content.Context
import android.view.ViewGroup
import com.github.panpf.tools4a.display.ktx.isOrientationPortrait
import com.github.panpf.tools4j.math.ktx.divide
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.GiphyData
import me.panpf.sketch.sample.widget.SampleImageView

class StaggeredImageItem(parent: ViewGroup) : AssemblyItem<GiphyData>(R.layout.list_item_image_staggered, parent) {
    private val imageView: SampleImageView by bindView(R.id.image_staggeredImageItem)

    override fun onConfigViews(context: Context) {
        imageView.setOptions(ImageOptions.RECT)

        imageView.page = SampleImageView.Page.SEARCH_LIST

        imageView.layoutParams?.let {
            if (it is FlexboxLayoutManager.LayoutParams) {
                it.flexGrow = 1.0f
            }
        }
    }

    override fun onSetData(i: Int, image: GiphyData?) {
        image ?: return
        imageView.layoutParams?.let {
            it.height = imageView.context.resources.displayMetrics.heightPixels / (if (imageView.context.isOrientationPortrait()) 5 else 2)
            it.width = (it.height / (image.media?.previewGif?.height?.toFloat().divide(image.media?.previewGif?.width?.toFloat()))).toInt()
            imageView.layoutParams = it
        }

        imageView.displayImage(image.media?.previewGif?.getDownloadUrl().orEmpty())
    }

    class Factory : AssemblyItemFactory<GiphyData>() {

        override fun match(o: Any?): Boolean = o is GiphyData

        override fun createAssemblyItem(viewGroup: ViewGroup) = StaggeredImageItem(viewGroup)
    }
}
