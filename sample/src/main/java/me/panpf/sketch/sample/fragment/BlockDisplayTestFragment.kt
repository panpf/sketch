package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.*
import me.panpf.sketch.sample.activity.MainActivity
import me.panpf.sketch.sample.bean.Image

/**
 * 大图页面，用来展示Sketch显示大图的能力
 */
@BindContentView(R.layout.fragment_pager_tab)
class BlockDisplayTestFragment : BaseFragment() {
    private val tabStrip: PagerIndicator by bindView(R.id.tab_pagerTabFragment_tabs)
    private val viewPager: ViewPager by bindView(R.id.pager_pagerTabFragment_content)

    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return

        if (fragmentAdapter == null) {
            val hugeAssetImageNames = AssetImage.HUGE_IMAGES
            val fragments = arrayOfNulls<Fragment>(hugeAssetImageNames.size)
            for (w in hugeAssetImageNames.indices) {
                val url = hugeAssetImageNames[w]
                fragments[w] = ImageFragment.build(Image(url, url), null, true)
            }
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        viewPager.adapter = fragmentAdapter

        tabStrip.setTabViewFactory(MainActivity.TitleTabFactory(arrayOf("WORLD", "QMSHT", "CWB", "CARD"), activity))
        tabStrip.setViewPager(viewPager)
    }
}
