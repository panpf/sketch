package com.github.panpf.sketch.sample.ui.viewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.sample.databinding.ImageFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.viewability.showSectorProgressIndicator
import com.github.panpf.sketch.viewability.showDataFromLogo

class ImageFragment : BindingFragment<ImageFragmentBinding>() {

    private val args by navArgs<ImageFragmentArgs>()

    override fun onViewCreated(binding: ImageFragmentBinding, savedInstanceState: Bundle?) {
        binding.imageImage.apply {
            showDataFromLogo()
            showSectorProgressIndicator()
            displayImage(args.url) {
                lifecycle(viewLifecycleOwner.lifecycle)
            }
        }
    }

    class ItemFactory : FragmentItemFactory<ImageDetail>(ImageDetail::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: ImageDetail
        ): Fragment = ImageFragment().apply {
            arguments = ImageFragmentArgs(
                data.middenUrl,
            ).toBundle()
        }
    }
}