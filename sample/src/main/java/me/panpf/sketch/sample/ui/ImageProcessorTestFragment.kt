package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.databinding.FragmentPagerTabBinding
import me.panpf.sketch.sample.item.TitleTabFactory

class ImageProcessorTestFragment : BaseToolbarFragment<FragmentPagerTabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPagerTabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPagerTabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Image Processor Test"

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
                ),
                requireActivity()
            )
        )
        binding.tabPagerTabFragmentTabs.setViewPager(binding.pagerPagerTabFragmentContent)
    }
}
