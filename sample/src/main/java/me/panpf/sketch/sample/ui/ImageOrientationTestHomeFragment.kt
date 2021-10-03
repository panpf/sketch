package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import me.panpf.adapter.pager.FragmentArrayPagerAdapter
import me.panpf.sketch.sample.base.BaseBindingFragment
import me.panpf.sketch.sample.databinding.FragmentPagerTabBinding
import me.panpf.sketch.sample.item.TitleTabFactory
import me.panpf.sketch.sample.util.ImageOrientationCorrectTestFileGenerator

class ImageOrientationTestHomeFragment : BaseBindingFragment<FragmentPagerTabBinding>() {
    private var fragmentAdapter: FragmentArrayPagerAdapter? = null

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPagerTabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        binding: FragmentPagerTabBinding,
        savedInstanceState: Bundle?
    ) {
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
        binding.pagerPagerTabFragmentContent.adapter = fragmentAdapter

        binding.tabPagerTabFragmentTabs.setTabViewFactory(
            TitleTabFactory(
                arrayOf(
                    "ROTATE_90",
                    "ROTATE_180",
                    "ROTATE_270",
                    "FLIP_HORIZONTAL",
                    "TRANSPOSE",
                    "FLIP_VERTICAL",
                    "TRANSVERSE"
                ), activity
            )
        )
        binding.tabPagerTabFragmentTabs.setViewPager(binding.pagerPagerTabFragmentContent)
    }
}
