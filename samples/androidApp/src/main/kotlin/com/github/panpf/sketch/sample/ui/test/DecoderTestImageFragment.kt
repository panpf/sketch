package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.fragment.navArgs
import com.github.panpf.assemblyadapter.pager.FragmentItemFactory
import com.github.panpf.sketch.ability.showDataFromLogo
import com.github.panpf.sketch.ability.showProgressIndicator
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.request.LoadState.Error
import com.github.panpf.sketch.sample.databinding.FragmentImageBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.util.createThemeSectorProgressDrawable
import com.github.panpf.sketch.sample.ui.util.parentViewModel
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.util.SketchUtils

class DecoderTestImageFragment : BaseBindingFragment<FragmentImageBinding>() {

    private val args by navArgs<DecoderTestImageFragmentArgs>()
    private val decoderTestViewModel by parentViewModel<DecoderTestViewModel>()

    override fun onViewCreated(binding: FragmentImageBinding, savedInstanceState: Bundle?) {
        decoderTestViewModel.data
            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) { list ->
                val testItem = list[args.position]
                if ((testItem.currentApi ?: 0) >= (testItem.minAPI ?: 0)) {
                    binding.myImage.apply {
                        showDataFromLogo()
                        showProgressIndicator(createThemeSectorProgressDrawable(requireContext()))
                        loadImage(testItem.imageUri) {
                            memoryCachePolicy(DISABLED)
                            resultCachePolicy(DISABLED)
                            downloadCachePolicy(DISABLED)
                            val decoder = testItem.imageDecoder
                            if (decoder != null) {
                                components {
                                    add(decoder)
                                }
                            }
                        }
                    }

                    binding.smallState.apply {
                        binding.myImage.requestState.loadState
                            .repeatCollectWithLifecycle(viewLifecycleOwner, State.CREATED) {
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
                } else {
                    binding.smallState.error {
                        message("This format requires API ${testItem.minAPI} or higher")
                    }
                }
            }
    }

    class ItemFactory : FragmentItemFactory<Int>(Int::class) {

        override fun createFragment(
            bindingAdapterPosition: Int,
            absoluteAdapterPosition: Int,
            data: Int
        ): Fragment = DecoderTestImageFragment().apply {
            arguments = DecoderTestImageFragmentArgs(data).toBundle()
        }
    }
}