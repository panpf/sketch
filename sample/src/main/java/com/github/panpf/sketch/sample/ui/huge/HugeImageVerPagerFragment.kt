package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.TabPagerVerFragmentBinding
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.widget.VerTabLayoutMediator

class HugeImageVerPagerFragment : BindingFragment<TabPagerVerFragmentBinding>() {

    override fun onViewCreated(binding: TabPagerVerFragmentBinding, savedInstanceState: Bundle?) {
        val images = AssetImages.HUGES.plus(AssetImages.LONGS).toList()
        val titles = arrayOf("WORLD", "CARD", "QMSHT", "CWB")

        binding.tabPagerVerPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = AssemblyFragmentStateAdapter(
                this@HugeImageVerPagerFragment,
                listOf(HugeImageViewerFragment.ItemFactory()),
                images
            )
        }

        VerTabLayoutMediator(
            binding.tabPagerVerTabLayout,
            binding.tabPagerVerPager
        ) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}