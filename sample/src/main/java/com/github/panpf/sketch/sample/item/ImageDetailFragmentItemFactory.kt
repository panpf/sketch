package com.github.panpf.sketch.sample.item

import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.ui.ImageDetailFragment
import com.github.panpf.sketch.sample.ui.ImageDetailFragmentArgs
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ImageDetailFragmentItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

    override fun createFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: ImageDetail
    ): Fragment {
        return ImageDetailFragment().apply {
            arguments = ImageDetailFragmentArgs(
                Json.encodeToString(data),
            ).toBundle()
        }
    }
}
