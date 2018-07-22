package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import kotlinx.android.synthetic.main.fm_pager.*
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.pagerid.PagerIndicator
import me.panpf.sketch.sample.R
import me.panpf.sketch.sample.base.BaseFragment
import me.panpf.sketch.sample.base.BindContentView

/**
 * App列表页面，用来展示已安装APP和本地APK列表
 */
@BindContentView(R.layout.fm_pager)
class AppListFragment : BaseFragment() {

    private val fragmentAdapter: FragmentArrayPagerAdapter by lazy {
        FragmentArrayPagerAdapter(childFragmentManager, arrayOf<Fragment>(InstalledAppFragment(), AppPackageListFragment()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagerFm_pager.adapter = fragmentAdapter
        (parentFragment as GetPagerIndicatorCallback).onGetPagerIndicator().setViewPager(pagerFm_pager)
    }

    interface GetPagerIndicatorCallback {
        fun onGetPagerIndicator(): PagerIndicator
    }
}
