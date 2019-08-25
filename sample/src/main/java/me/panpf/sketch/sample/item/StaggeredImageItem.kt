package me.panpf.sketch.sample.item

import android.content.Context
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.androidxkt.view.isOrientationPortrait
import me.panpf.javaxkt.lang.divide
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.TenorData
import me.panpf.sketch.sample.widget.SampleImageView

class StaggeredImageItem(parent: ViewGroup) : AssemblyItem<TenorData>(R.layout.list_item_image_staggered, parent) {
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

    override fun onSetData(i: Int, image: TenorData?) {
        image ?: return
        imageView.layoutParams?.let {
            it.height = imageView.context.resources.displayMetrics.heightPixels / (if (imageView.context.isOrientationPortrait()) 5 else 2)
            it.width = (it.height / (image.gifMedia.height.toFloat().divide(image.gifMedia.width.toFloat()))).toInt()
            imageView.layoutParams = it
        }

        imageView.displayImage(image.gifMedia.url)
    }

    class Factory : AssemblyItemFactory<TenorData>() {

        override fun match(o: Any?): Boolean = o is TenorData

        override fun createAssemblyItem(viewGroup: ViewGroup) = StaggeredImageItem(viewGroup)
    }
}
