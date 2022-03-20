package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.bean.ImageDetail
import com.github.panpf.sketch.sample.databinding.FragmentPager2TabBinding
import com.github.panpf.sketch.sample.item.ImageDetailFragmentItemFactory
import com.google.android.material.tabs.TabLayoutMediator

class HugeImagePagerFragment : ToolbarBindingFragment<FragmentPager2TabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPager2TabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPager2TabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Huge Image"

        val images = AssetImages.HUGES.plus(AssetImages.LONGS).map {
            ImageDetail(it, it, null)
        }
        val titles = arrayOf("WORLD", "CARD", "QMSHT", "CWB")

        binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
            this,
            listOf(ImageDetailFragmentItemFactory(true)),
            images
        )

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}