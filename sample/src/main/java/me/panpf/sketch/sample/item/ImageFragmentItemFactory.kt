package me.panpf.sketch.sample.item

import android.content.Context
import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import me.panpf.sketch.sample.AppConfig
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.ui.ImageFragment

class ImageFragmentItemFactory(
    private val context: Context,
    private val loadingImageOptionsId: String?,
    private val showTools: Boolean? = null
) : FragmentItemFactory<Image>(Image::class) {

    override fun createFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Image
    ): Fragment {
        val showTools =
            showTools ?: AppConfig.getBoolean(context, AppConfig.Key.SHOW_TOOLS_IN_IMAGE_DETAIL)
        return ImageFragment.build(data, loadingImageOptionsId, showTools)
    }
}
