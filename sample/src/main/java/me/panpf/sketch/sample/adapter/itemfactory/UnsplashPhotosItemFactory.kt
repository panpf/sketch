package me.panpf.sketch.sample.adapter.itemfactory

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.UnsplashImage
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.widget.SampleImageView
import me.xiaopan.assemblyadapter.AssemblyRecyclerItem
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory

class UnsplashPhotosItemFactory(private val unsplashPhotosItemEventListener: UnsplashPhotosItemEventListener?) : AssemblyRecyclerItemFactory<UnsplashPhotosItemFactory.UnsplashPhotosItem>() {

    override fun isTarget(o: Any): Boolean {
        return o is UnsplashImage
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): UnsplashPhotosItem {
        return UnsplashPhotosItem(R.layout.list_item_image_unsplash, viewGroup)
    }

    interface UnsplashPhotosItemEventListener {
        fun onClickImage(position: Int, image: UnsplashImage, optionsKey: String)

        fun onClickUser(position: Int, user: UnsplashImage.User)
    }

    inner class UnsplashPhotosItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyRecyclerItem<UnsplashImage>(itemLayoutId, parent) {
        val imageView: SampleImageView by bindView(R.id.image_unsplashImageItem)
        val userProfileImageView: SampleImageView by bindView(R.id.image_unsplashImageItem_userProfile)
        val userNameTextView: TextView by bindView(R.id.text_unsplashImageItem_userName)
        val dateTextView: TextView by bindView(R.id.text_unsplashImageItem_date)
        val rootViewGroup: ViewGroup by bindView(R.id.layout_unsplashImageItem_root)

        override fun onConfigViews(context: Context) {
            imageView.onClickListener = View.OnClickListener {
                unsplashPhotosItemEventListener?.onClickImage(adapterPosition, data, imageView.optionsKey)
            }
            imageView.setOptions(ImageOptions.LIST_FULL)

            imageView.page = SampleImageView.Page.UNSPLASH_LIST

            userProfileImageView.setOptions(ImageOptions.CIRCULAR_STROKE)

            userProfileImageView.onClickListener = View.OnClickListener {
                unsplashPhotosItemEventListener?.onClickUser(adapterPosition, data.user!!)
            }

            userNameTextView.setOnClickListener { userProfileImageView.performClick() }
        }

        override fun onSetData(i: Int, image: UnsplashImage) {
            val itemWidth = imageView.context.resources.displayMetrics.widthPixels

            imageView.layoutParams?.let {
                it.width = itemWidth
                it.height = (itemWidth / (image.width / image.height.toFloat())).toInt()
                imageView.layoutParams = it
            }

            rootViewGroup.layoutParams?.let {
                it.width = itemWidth
                it.height = (itemWidth / (image.width / image.height.toFloat())).toInt()
                rootViewGroup.layoutParams = it
            }

            imageView.displayImage(image.urls!!.regular!!)

            userProfileImageView.displayImage(image.user!!.profileImage!!.large!!)

            userNameTextView.text = image.user!!.name
            dateTextView.text = image.getFormattedUpdateDate()
        }
    }
}