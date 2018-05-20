package me.panpf.sketch.sample.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.ktx.isPortraitOrientation
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.BaiduImage
import me.panpf.sketch.sample.widget.SampleImageView

class StaggeredImageItemFactory(private val onItemClickListener: OnItemClickListener?) : AssemblyItemFactory<BaiduImage>() {

    override fun match(o: Any?): Boolean {
        return o is BaiduImage
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): StaggeredImageItem {
        return StaggeredImageItem(R.layout.list_item_image_staggered, viewGroup)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, image: BaiduImage, loadingImageOptionsInfo: String)
    }

    inner class StaggeredImageItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyItem<BaiduImage>(itemLayoutId, parent) {
        val imageView: SampleImageView by bindView(R.id.image_staggeredImageItem)

        override fun onConfigViews(context: Context) {
            imageView.onClickListener = View.OnClickListener {
                data?.let { it1 -> onItemClickListener?.onItemClick(adapterPosition, it1, imageView.optionsKey) }
            }
            imageView.setOptions(ImageOptions.RECT)

            imageView.page = SampleImageView.Page.SEARCH_LIST

            imageView.layoutParams?.let {
                if (it is FlexboxLayoutManager.LayoutParams) {
                    it.flexGrow = 1.0f
                }
            }
        }

        override fun onSetData(i: Int, image: BaiduImage?) {
            image ?: return
            imageView.layoutParams?.let {
                it.height = imageView.context.resources.displayMetrics.heightPixels / (if (imageView.context.isPortraitOrientation()) 5 else 2)
                it.width = (it.height / (image.height / image.width.toFloat())).toInt()
                imageView.layoutParams = it
            }

            imageView.displayImage(image.url ?: "")
        }
    }
}
