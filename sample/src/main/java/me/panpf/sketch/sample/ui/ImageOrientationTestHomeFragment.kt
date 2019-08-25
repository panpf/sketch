package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_pager_tab.*
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView
import me.panpf.sketch.sample.item.TitleTabFactory
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator

@BindContentView(R.layout.fragment_pager_tab)
class ImageOrientationTestHomeFragment : BaseFragment() {
    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return
        val activity = activity ?: return

        if (fragmentAdapter == null) {
            val filePaths = ImageOrientationCorrectTestFileGenerator.getInstance(context).filePaths
            val fragments = arrayOfNulls<androidx.fragment.app.Fragment>(filePaths.size)
            for (w in filePaths.indices) {
                fragments[w] = ImageOrientationTestFragment.build(filePaths[w])
            }
            fragmentAdapter = FragmentArrayPagerAdapter(childFragmentManager, fragments)
        }
        pager_pagerTabFragment_content.adapter = fragmentAdapter

        tab_pagerTabFragment_tabs.setTabViewFactory(TitleTabFactory(
                arrayOf("ROTATE_90", "ROTATE_180", "ROTATE_270", "FLIP_HORIZONTAL", "TRANSPOSE", "FLIP_VERTICAL", "TRANSVERSE"), activity))
        tab_pagerTabFragment_tabs.setViewPager(pager_pagerTabFragment_content)
    }
}
