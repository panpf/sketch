package com.github.panpf.sketch.sample.ui.test.transform

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.test.fetcher.FetcherTestViewModel
import com.google.android.material.tabs.TabLayoutMediator

class TransformationTestPagerFragment : ToolbarBindingFragment<TabPagerFragmentBinding>() {

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: TabPagerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Transformation"

        binding.tabPagerPager.adapter = ArrayFragmentStateAdapter(
            fragment = this,
            arrayOf(
                RoundedCornersTransformationTestFragment(),
                CircleCropTransformationTestFragment(),
                RotateTransformationTestFragment(),
                BlurTransformationTestFragment()
            ),
        )

        val titles = arrayOf("ROUNDED_CORNERS", "CIRCLE", "ROTATE", "BLUR")
        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
