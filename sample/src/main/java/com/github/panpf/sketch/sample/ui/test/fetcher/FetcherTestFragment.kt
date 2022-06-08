package com.github.panpf.sketch.sample.ui.test.fetcher

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.databinding.TabPagerFragmentBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.viewer.ImageFragment
import com.google.android.material.tabs.TabLayoutMediator

class FetcherTestFragment : ToolbarBindingFragment<TabPagerFragmentBinding>() {

    private val viewModel by viewModels<FetcherTestViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: TabPagerFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Fetcher"

        viewModel.data.observe(viewLifecycleOwner) { data ->
            val imageFromData = data ?: return@observe
            val images = imageFromData.uris.mapIndexed { index, s ->
                ImageDetail(index, s, s, null)
            }
            val titles = imageFromData.titles

            binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
                fragment = this,
                itemFactoryList = listOf(ImageFragment.ItemFactory()),
                initDataList = images
            )

            TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }
}
