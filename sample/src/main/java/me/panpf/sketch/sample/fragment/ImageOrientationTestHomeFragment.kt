package me.panpf.sketch.sample.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import me.panpf.adapter.FragmentArrayPagerAdapter
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.BaseFragment
import me.panpf.sketch.sample.BindContentView
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.activity.MainActivity
import me.panpf.sketch.sample.bindView
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator

@BindContentView(R.layout.fragment_pager_tab)
class ImageOrientationTestHomeFragment : BaseFragment() {
    val tabStrip: PagerIndicator by bindView(R.id.tab_pagerTabFragment_tabs)
    val viewPager: ViewPager by bindView(R.id.pager_pagerTabFragment_content)

    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return
        val activity = activity ?: return

        if (fragmentAdapter == null) {
            val filePaths = ImageOrientationCorrectTestFileGenerator.getInstance(context).filePaths
            val fragments = arrayOfNulls<Fragment>(filePaths.size)
            for (w in filePaths.indices) {
                fragments[w] = ImageOrientationTestFragment.build(filePaths[w])
            }
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        viewPager.adapter = fragmentAdapter

        tabStrip.setTabViewFactory(MainActivity.TitleTabFactory(
                arrayOf("ROTATE_90", "ROTATE_180", "ROTATE_270", "FLIP_HORIZONTAL", "TRANSPOSE", "FLIP_VERTICAL", "TRANSVERSE"), activity))
        tabStrip.setViewPager(viewPager)
    }
}
