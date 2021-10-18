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

class ImageShaperTestFragment : ToolbarBindingFragment<FragmentPager2TabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPager2TabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPager2TabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "ImageShaper Test"

        val titles = arrayOf(
            "ROUND_RECT",
            "CIRCLE",
            "SHAPE_SIZE"
        )
        val fragments = arrayOf<Fragment>(
            RoundRectImageShaperTestFragment(),
            CircleImageShaperTestFragment(),
            ShapeSizeImageShaperTestFragment()
        )

        binding.tabPagerPager.adapter = ArrayFragmentStateAdapter(this, fragments)

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}