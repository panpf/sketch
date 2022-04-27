package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.FragmentPager2TabBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.view.ImageZoomFragmentItemFactory
import com.google.android.material.tabs.TabLayoutMediator

class HugeImageHorListFragment : BindingFragment<FragmentPager2TabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPager2TabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentPager2TabBinding,
        savedInstanceState: Bundle?
    ) {
        val images = AssetImages.HUGES.plus(AssetImages.LONGS).map {
            ImageDetail(it, it, null)
        }
        val titles = arrayOf("WORLD", "CARD", "QMSHT", "CWB")

        binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
            this,
            listOf(ImageZoomFragmentItemFactory(true)),
            images
        )

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}