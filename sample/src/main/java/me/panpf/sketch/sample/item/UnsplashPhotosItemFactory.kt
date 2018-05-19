package me.panpf.sketch.sample.item

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.panpf.adapter.AssemblyItem
import me.panpf.adapter.AssemblyItemFactory
import me.panpf.adapter.ktx.bindView
import me.panpf.sketch.sample.ImageOptions
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.bean.UnsplashImage
import me.panpf.sketch.sample.ktx.isPortraitOrientation
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.widget.SampleImageView

class UnsplashPhotosItemFactory(private val activity: Activity,
                                private val unsplashPhotosItemEventListener: UnsplashPhotosItemEventListener?)
    : AssemblyItemFactory<UnsplashImage>() {

    override fun match(o: Any?): Boolean {
        return o is UnsplashImage
    }

    override fun createAssemblyItem(viewGroup: ViewGroup): UnsplashPhotosItem {
        return UnsplashPhotosItem(R.layout.list_item_image_unsplash, viewGroup)
    }

    interface UnsplashPhotosItemEventListener {
        fun onClickImage(position: Int, image: UnsplashImage, optionsKey: String)

        fun onClickUser(position: Int, user: UnsplashImage.User)
    }

    inner class UnsplashPhotosItem(itemLayoutId: Int, parent: ViewGroup) : AssemblyItem<UnsplashImage>(itemLayoutId, parent) {
        val imageView: SampleImageView by bindView(R.id.image_unsplashImageItem)
        private val userProfileImageView: SampleImageView by bindView(R.id.image_unsplashImageItem_userProfile)
        private val userNameTextView: TextView by bindView(R.id.text_unsplashImageItem_userName)
        private val dateTextView: TextView by bindView(R.id.text_unsplashImageItem_date)
        private val rootViewGroup: ViewGroup by bindView(R.id.layout_unsplashImageItem_root)

        override fun onConfigViews(context: Context) {
            imageView.onClickListener = View.OnClickListener {
                data?.let { it1 -> unsplashPhotosItemEventListener?.onClickImage(adapterPosition, it1, imageView.optionsKey) }
            }
            imageView.setOptions(ImageOptions.LIST_FULL)

            imageView.page = SampleImageView.Page.UNSPLASH_LIST

            userProfileImageView.setOptions(ImageOptions.CIRCULAR_STROKE)

            userProfileImageView.onClickListener = View.OnClickListener {
                data?.user?.let { it1 -> unsplashPhotosItemEventListener?.onClickUser(adapterPosition, it1) }
            }

            userNameTextView.setOnClickListener { userProfileImageView.performClick() }
        }

        override fun onSetData(i: Int, image: UnsplashImage?) {
            image ?: return

            val itemWidth = imageView.context.resources.displayMetrics.widthPixels

            imageView.layoutParams?.let {
                it.width = itemWidth
                if (!imageView.context.isPortraitOrientation()) {
                    it.width += DeviceUtils.getWindowHeightSupplement(activity)
                }
                it.height = (it.width / (image.width / image.height.toFloat())).toInt()
                imageView.layoutParams = it
            }

            rootViewGroup.layoutParams?.let {
                it.width = itemWidth
                if (!imageView.context.isPortraitOrientation()) {
                    it.width += DeviceUtils.getWindowHeightSupplement(activity)
                }
                it.height = (it.width / (image.width / image.height.toFloat())).toInt()
                rootViewGroup.layoutParams = it
            }

            imageView.displayImage(image.urls!!.regular!!)

            userProfileImageView.displayImage(image.user!!.profileImage!!.large!!)

            userNameTextView.text = image.user!!.name
            dateTextView.text = image.getFormattedUpdateDate()
        }
    }
}