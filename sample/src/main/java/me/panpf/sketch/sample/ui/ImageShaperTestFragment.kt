package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.databinding.FragmentPagerTabBinding
import me.panpf.sketch.sample.item.TitleTabFactory

class ImageShaperTestFragment : BaseToolbarFragment<FragmentPagerTabBinding>() {

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPagerTabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPagerTabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Image Shaper Test"

        binding.pagerPagerTabFragmentContent.adapter = FragmentArrayPagerAdapter(
            childFragmentManager,
            arrayOf(
                RoundRectImageShaperTestFragment(),
                CircleImageShaperTestFragment(),
                ShapeSizeImageShaperTestFragment()
            )
        )

        binding.tabPagerTabFragmentTabs.setTabViewFactory(
            TitleTabFactory(
                arrayOf(
                    "ROUND_RECT",
                    "CIRCLE",
                    "SHAPE_SIZE"
                ),
                requireActivity()
            )
        )
        binding.tabPagerTabFragmentTabs.setViewPager(binding.pagerPagerTabFragmentContent)
    }
}