package com.github.panpf.sketch.sample.ui.test.format

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.viewer.ImageFragment
import com.google.android.material.tabs.TabLayoutMediator

class ImageFormatTestFragment : ToolbarBindingFragment<TabPagerFragmentBinding>() {

    private val viewModel by viewModels<ImageFormatTestViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: TabPagerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Image Format"

        viewModel.data.observe(viewLifecycleOwner) {
            it ?: return@observe
            binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
                fragment = this,
                itemFactoryList = listOf(ImageFragment.ItemFactory()),
                initDataList = it.second
            )

            TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
                tab.text = it.first[position]
            }.attach()
        }
    }
}
