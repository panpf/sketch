package com.github.panpf.sketch.sample.ui.view

import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.sample.model.ImageDetail

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
