package me.xiaopan.sketchsample.adapter.itemfactory

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewGroup
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory
import me.xiaopan.sketch.util.SketchUtils
import me.xiaopan.sketchsample.ImageOptions
import me.xiaopan.sketchsample.R
import me.xiaopan.sketchsample.util.AppConfig
import me.xiaopan.sketchsample.widget.SampleImageView
import me.xiaopan.ssvt.bindView

class MyPhotoItemFactory(private val onImageClickListener: OnImageClickListener?) : AssemblyRecyclerItemFactory<MyPhotoItemFactory.PhotoAlbumItem>() {
    private var itemSize: Int = 0

    override fun isTarget(o: Any): Boolean {
        return o is String
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): PhotoAlbumItem {
        if (itemSize == 0) {
            itemSize = -1
            if (viewGroup is RecyclerView) {
                var spanCount = 1
                val recyclerView = viewGroup
                val layoutManager = recyclerView.layoutManager
                if (layoutManager is GridLayoutManager) {
                    spanCount = (recyclerView.layoutManager as GridLayoutManager).spanCount
                } else if (layoutManager is StaggeredGridLayoutManager) {
                    spanCount = (recyclerView.layoutManager as StaggeredGridLayoutManager).spanCount
                }
                if (spanCount > 1) {
                    val screenWidth = viewGroup.getResources().displayMetrics.widthPixels
                    itemSize = (screenWidth - SketchUtils.dp2px(viewGroup.getContext(), 4) * (spanCount + 1)) / spanCount
                }
            }
        }

        return PhotoAlbumItem(R.layout.list_item_my_photo, viewGroup)
    }

    interface OnImageClickListener {
        fun onClickImage(position: Int, optionsKey: String)
    }

    inner class PhotoAlbumItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<String>(itemLayoutId, parent) {
        val imageView: SampleImageView by bindView(R.id.image_myPhotoItem)

        override fun onConfigViews(context: Context) {
            imageView.onClickListener = View.OnClickListener {
                onImageClickListener?.onClickImage(adapterPosition, imageView.optionsKey)
            }

            if (itemSize > 0) {
                imageView.layoutParams?.let {
                    it.width = itemSize
                    it.height = itemSize
                    imageView.layoutParams = it
                }
            }

            imageView.page = SampleImageView.Page.PHOTO_LIST
        }

        override fun onSetData(i: Int, imageUri: String) {
            if (AppConfig.getBoolean(imageView.context, AppConfig.Key.SHOW_ROUND_RECT_IN_PHOTO_LIST)) {
                imageView.setOptions(ImageOptions.ROUND_RECT)
            } else {
                imageView.setOptions(ImageOptions.RECT)
            }

            imageView.displayImage(imageUri)
        }
    }
}
