package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.AssetImage
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.bean.Image
import me.panpf.sketch.sample.databinding.FragmentPagerTabBinding
import me.panpf.sketch.sample.item.TitleTabFactory

/**
 * 大图页面，用来展示Sketch显示大图的能力
 */
class BlockDisplayTestFragment : BaseBindingFragment<FragmentPagerTabBinding>() {

    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPagerTabBinding.inflate(inflater, parent, false)

    override fun onInitData(binding: FragmentPagerTabBinding, savedInstanceState: Bundle?) {
        val activity = activity ?: return

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
                activity
            )
        )
        binding.tabPagerTabFragmentTabs.setViewPager(binding.pagerPagerTabFragmentContent)
    }
}
