package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.xiaopan.sketch.util.SketchUtils
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.BaiduImage
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.widget.SampleImageView

class StaggeredImageItemFactory(private val onItemClickListener: OnItemClickListener?) : AssemblyRecyclerItemFactory<StaggeredImageItemFactory.StaggeredImageItem>() {
    private var itemWidth: Int = 0

    override fun isTarget(o: Any): Boolean {
        return o is BaiduImage
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): StaggeredImageItem {
        return StaggeredImageItem(R.layout.list_item_image_staggered, viewGroup)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, image: BaiduImage, loadingImageOptionsInfo: String)
    }

    inner class StaggeredImageItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<BaiduImage>(itemLayoutId, parent) {
        val imageView: SampleImageView by bindView(R.id.image_staggeredImageItem)

        override fun onConfigViews(context: Context) {
            imageView.onClickListener = View.OnClickListener {
                onItemClickListener?.onItemClick(adapterPosition, data, imageView.optionsKey)
            }
            imageView.setOptions(ImageOptions.RECT)

            imageView.page = SampleImageView.Page.SEARCH_LIST
        }

        override fun onSetData(i: Int, image: BaiduImage) {
            if (itemWidth == 0) {
                val screenWidth = imageView.context.resources.displayMetrics.widthPixels
                itemWidth = (screenWidth - SketchUtils.dp2px(imageView.context, 4) * 3) / 2
            }

            imageView.layoutParams?.let {
                it.width = itemWidth
                it.height = (itemWidth / (image.width / image.height.toFloat())).toInt()
                imageView.layoutParams = it
            }

            imageView.displayImage(image.url ?: "")
        }
    }
}
