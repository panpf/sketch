package com.github.panpf.sketch.sample.ui.view

import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory

class HugeImageFragmentItemFactory: FragmentItemFactory<String>(String::class) {

    override fun createFragment(
        bindingAdapterPosition: Int,
        absoluteAdapterPosition: Int,
        data: String
    ): Fragment {
        return HugeImageFragment().apply {
            arguments = HugeImageFragmentArgs(data).toBundle()
        }
    }
}
