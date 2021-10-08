package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentPagerTabBinding
import me.panpf.sketch.sample.item.TitleTabFactory

class HugeImageFragment : BaseToolbarFragment<FragmentPagerTabBinding>() {

    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPagerTabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPagerTabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Huge Image"

        if (fragmentAdapter == null) {
            val hugeAssetImageNames = AssetImage.HUGE_IMAGES
            val fragments = arrayOfNulls<androidx.fragment.app.Fragment>(hugeAssetImageNames.size)
            for (w in hugeAssetImageNames.indices) {
                val url = hugeAssetImageNames[w]
                fragments[w] = ImageFragment.build(Image(url, url), null, true)
            }
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        binding.pagerPagerTabFragmentContent.adapter = fragmentAdapter

        binding.tabPagerTabFragmentTabs.setTabViewFactory(
            TitleTabFactory(
                arrayOf("WORLD", "QMSHT", "CWB", "CARD"),
                requireActivity()
            )
        )
        binding.tabPagerTabFragmentTabs.setViewPager(binding.pagerPagerTabFragmentContent)
    }
}
