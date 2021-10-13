package me.panpf.sketch.sample.item

import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.ui.ImageFragment
import me.panpf.sketch.sample.ui.ImageFragmentArgs

class ImageFragmentItemFactory(
    private val loadingOptionsId: String? = null,
    private val showSmallMap: Boolean = false
) : FragmentItemFactory<Image>(Image::class) {

    override fun createFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: Image
    ): Fragment {
        return ImageFragment().apply {
            arguments = ImageFragmentArgs(
                data.normalQualityUrl,
                data.rawQualityUrl,
                loadingOptionsId,
                showSmallMap
            ).toBundle()
        }
    }
}
