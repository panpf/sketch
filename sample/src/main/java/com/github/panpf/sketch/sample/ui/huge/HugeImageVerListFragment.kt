package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.databinding.FragmentPager2TabVerticalBinding
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.BindingFragment
import com.github.panpf.sketch.sample.ui.view.ImageZoomFragmentItemFactory
import com.github.panpf.sketch.sample.widget.VerTabLayoutMediator

class HugeImageVerListFragment : BindingFragment<FragmentPager2TabVerticalBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPager2TabVerticalBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentPager2TabVerticalBinding,
        savedInstanceState: Bundle?
    ) {
        val images = AssetImages.HUGES.plus(AssetImages.LONGS).map {
            ImageDetail(it, it, null)
        }
        val titles = arrayOf("WORLD", "CARD", "QMSHT", "CWB")

        binding.tabPagerVerticalPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.tabPagerVerticalPager.adapter = AssemblyFragmentStateAdapter(
            this,
            listOf(ImageZoomFragmentItemFactory(true)),
            images
        )

        VerTabLayoutMediator(
            binding.tabPagerVerticalTabLayout,
            binding.tabPagerVerticalPager
        ) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}