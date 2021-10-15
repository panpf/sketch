package me.panpf.sketch.sample.item

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.github.panpf.assemblyadapter.BindingItemFactory
import com.github.panpf.tools4a.activity.ktx.safeStartActivity
import com.github.panpf.tools4a.display.ktx.isOrientationPortrait
import me.panpf.sketch.sample.image.ImageOptions
import me.panpf.sketch.sample.bean.UnsplashImage
import me.panpf.sketch.sample.databinding.ListItemImageUnsplashBinding
import me.panpf.sketch.sample.util.DeviceUtils
import me.panpf.sketch.sample.widget.SampleImageView

class UnsplashImageItemFactory(
    activity: Activity,
    private val onClickPhoto: (view: SampleImageView, position: Int, data: UnsplashImage) -> Unit
) : BindingItemFactory<UnsplashImage, ListItemImageUnsplashBinding>(UnsplashImage::class) {

    private val windowHeightSupplement = DeviceUtils.getWindowHeightSupplement(activity)

    override fun createItemViewBinding(
        context: Context,
        inflater: LayoutInflater,
        parent: ViewGroup
    ) = ListItemImageUnsplashBinding.inflate(inflater, parent, false)

    override fun initItem(
        context: Context,
        binding: ListItemImageUnsplashBinding,
        item: BindingItem<UnsplashImage, ListItemImageUnsplashBinding>
    ) {
        binding.imageUnsplashImageItem.apply {
            setOptions(ImageOptions.LIST_FULL)
            page = SampleImageView.Page.UNSPLASH_LIST
            onClickListener = View.OnClickListener {
                onClickPhoto(
                    binding.imageUnsplashImageItem,
                    item.absoluteAdapterPosition,
                    item.dataOrThrow
                )
            }
        }

        binding.imageUnsplashImageItemUserProfile.apply {
            setOptions(ImageOptions.CIRCULAR_STROKE)
            onClickListener = View.OnClickListener {
                val data = item.dataOrThrow
                val uri = Uri.parse(data.user!!.links!!.html)
                    .buildUpon()
                    .appendQueryParameter("utm_source", "SketchSample")
                    .appendQueryParameter("utm_medium", "referral")
                    .appendQueryParameter("utm_campaign", "api-credit")
                    .build()
                context.safeStartActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }

        binding.textUnsplashImageItemUserName.setOnClickListener {
            binding.imageUnsplashImageItemUserProfile.performClick()
        }
    }

    override fun bindItemData(
        context: Context,
        binding: ListItemImageUnsplashBinding,
        item: BindingItem<UnsplashImage, ListItemImageUnsplashBinding>,
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: UnsplashImage
    ) {
        val itemWidth = context.resources.displayMetrics.widthPixels

        binding.root.updateLayoutParams<ViewGroup.LayoutParams> {
            width = itemWidth
            if (!context.isOrientationPortrait()) {
                width += windowHeightSupplement
            }
            height = (width / (data.width / data.height.toFloat())).toInt()
        }

        binding.imageUnsplashImageItem.apply {
            updateLayoutParams<ViewGroup.LayoutParams> {
                width = itemWidth
                if (!context.isOrientationPortrait()) {
                    width += windowHeightSupplement
                }
                height = (width / (data.width / data.height.toFloat())).toInt()
            }
            displayImage(data.urls?.regular)
        }

        binding.imageUnsplashImageItemUserProfile.displayImage(data.user?.profileImage?.large)

        binding.textUnsplashImageItemUserName.text = data.user?.name

        binding.textUnsplashImageItemDate.text = data.formattedUpdateDate
    }
}