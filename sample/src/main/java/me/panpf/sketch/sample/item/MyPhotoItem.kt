package me.panpf.sketch.sample.item

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.widget.SampleImageView
import me.panpf.sketch.util.SketchUtils

class MyPhotoItem(parent: ViewGroup, val factory: Factory) : AssemblyItem<String>(R.layout.list_item_my_photo, parent) {
    private val imageView: SampleImageView by bindView(R.id.image_myPhotoItem)

    override fun onConfigViews(context: Context) {
        if (factory.itemSize > 0) {
            imageView.layoutParams?.let {
                it.width = factory.itemSize
                it.height = factory.itemSize
                imageView.layoutParams = it
            }
        }

        imageView.page = SampleImageView.Page.PHOTO_LIST
    }

    override fun onSetData(i: Int, imageUri: String?) {
        if (AppConfig.getBoolean(imageView.context, AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST)) {
            imageView.setOptions(ImageOptions.ROUND_RECT)
        } else {
            imageView.setOptions(ImageOptions.RECT)
        }

        imageView.displayImage(imageUri ?: "")
    }

    class Factory : AssemblyItemFactory<String>() {
        var itemSize: Int = 0

        override fun match(o: Any?): Boolean {
            return o is String
        }

        override fun createAssemblyItem(viewGroup: ViewGroup): MyPhotoItem {
            if (itemSize == 0) {
                itemSize = -1
                if (viewGroup is RecyclerView) {
                    val spanCount = when (viewGroup.layoutManager) {
                        is GridLayoutManager -> (viewGroup.layoutManager as GridLayoutManager).spanCount
                        is StaggeredGridLayoutManager -> (viewGroup.layoutManager as StaggeredGridLayoutManager).spanCount
                        else -> 1
                    }
                    if (spanCount > 1) {
                        val screenWidth = viewGroup.getResources().displayMetrics.widthPixels
                        itemSize = (screenWidth - SketchUtils.dp2px(viewGroup.getContext(), 4) * (spanCount + 1)) / spanCount
                    }
                }
            }

            return MyPhotoItem(viewGroup, this)
        }
    }
}
