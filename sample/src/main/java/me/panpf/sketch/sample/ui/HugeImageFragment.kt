package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.ToolbarBindingFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentPager2TabBinding
import me.panpf.sketch.sample.item.ImageFragmentItemFactory

class HugeImageFragment : ToolbarBindingFragment<FragmentPager2TabBinding>() {

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

        val images = AssetImage.IMAGES_HUGE.map {
            Image(it, it)
        }
        val titles = arrayOf("WORLD", "QMSHT", "CWB", "CARD")

        binding.tabPagerPager.adapter = AssemblyFragmentStateAdapter(
            this,
            listOf(ImageFragmentItemFactory(showSmallMap = true)),
            images
        )

        TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}
