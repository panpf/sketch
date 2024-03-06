package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.ability.showDataFromLogo
import com.github.panpf.sketch.ability.showProgressIndicator
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.displayImage
import com.github.panpf.sketch.request.LoadState.Error
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.util.SketchUtils

class FetcherTestImageFragment : BaseBindingFragment<FragmentImageBinding>() {

    private val args by navArgs<FetcherTestImageFragmentArgs>()

    override var isPage = false

    override fun onViewCreated(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        binding.myImage.apply {
            scaleType = CENTER_INSIDE
            showDataFromLogo()
            showProgressIndicator(createThemeSectorProgressDrawable(requireContext()))
            displayImage(args.imageUri) {
                memoryCachePolicy(DISABLED)
                resultCachePolicy(DISABLED)
                downloadCachePolicy(DISABLED)
            }
        }

        binding.smallState.apply {
            binding.myImage.requestState.loadState
                .repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) {
                    if (it is Error) {
                        error {
                            retryAction {
                                SketchUtils.restart(binding.myImage)
                            }
                        }
                    } else {
                        gone()
                    }
                }
        }
    }

    class ItemFactory : FragmentItemFactory<String>(String::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: String
        ): Fragment = FetcherTestImageFragment().apply {
            arguments = FetcherTestImageFragmentArgs(data).toBundle()
        }
    }
}