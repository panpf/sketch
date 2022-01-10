package com.github.panpf.sketch.sample.item

import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.ui.ImageFragment
import com.github.panpf.sketch.sample.ui.ImageFragmentArgs

class ImageFragmentItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

    override fun createFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: ImageDetail
    ): Fragment {
        return ImageFragment().apply {
            arguments = ImageFragmentArgs(
                data.middenUrl,
            ).toBundle()
        }
    }
}
