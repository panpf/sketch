package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentPagerTabBinding
import me.panpf.sketch.sample.item.TitleTabFactory

class ImageProcessorTestFragment : BaseBindingFragment<FragmentPagerTabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPagerTabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentPagerTabBinding,
        savedInstanceState: Bundle?
    ) {
        val activity = activity ?: return

        binding.pagerPagerTabFragmentContent.adapter = FragmentArrayPagerAdapter(
            childFragmentManager, arrayOf(
                ReflectionImageProcessorTestFragment(),
                GaussianBlurImageProcessorTestFragment(),
                RotateImageProcessorTestFragment(),
                RoundRectImageProcessorTestFragment(),
                CircleImageProcessorTestFragment(),
                ResizeImageProcessorTestFragment(),
                MaskImageProcessorTestFragment(),
                WrappedImageProcessorTestFragment()
            )
        )

        binding.tabPagerTabFragmentTabs.setTabViewFactory(
            TitleTabFactory(
                arrayOf(
                    "REFLECTION",
                    "GAUSSIAN_BLUR",
                    "ROTATE",
                    "ROUND_RECT",
                    "CIRCLE",
                    "RESIZE",
                    "MASK",
                    "WRAPPED"
                ), activity
            )
        )
        binding.tabPagerTabFragmentTabs.setViewPager(binding.pagerPagerTabFragmentContent)
    }
}
