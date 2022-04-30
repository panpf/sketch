package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class HugeImageHorPagerFragment : BindingFragment<TabPagerFragmentBinding>() {

    override fun onViewCreated(binding: TabPagerFragmentBinding, savedInstanceState: Bundle?) {
        val images = AssetImages.HUGES.plus(AssetImages.LONGS).toList()
        val titles = arrayOf("WORLD", "CARD", "QMSHT", "CWB")

        binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
            this,
            listOf(HugeImageViewerFragment.ItemFactory()),
            images
        )

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}