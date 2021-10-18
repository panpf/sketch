package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.databinding.FragmentPager2TabBinding

class ImageProcessorTestFragment : ToolbarBindingFragment<FragmentPager2TabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPager2TabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPager2TabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ImageProcessor Test"

        val titles = arrayOf(
            "REFLECTION",
            "GAUSSIAN_BLUR",
            "ROTATE",
            "ROUND_RECT",
            "CIRCLE",
            "RESIZE",
            "MASK",
            "WRAPPED"
        )
        val fragments = arrayOf<Fragment>(
            ReflectionImageProcessorTestFragment(),
            GaussianBlurImageProcessorTestFragment(),
            RotateImageProcessorTestFragment(),
            RoundRectImageProcessorTestFragment(),
            CircleImageProcessorTestFragment(),
            ResizeImageProcessorTestFragment(),
            MaskImageProcessorTestFragment(),
            WrappedImageProcessorTestFragment()
        )

        binding.tabPagerPager.adapter = ArrayFragmentStateAdapter(this, fragments)

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
