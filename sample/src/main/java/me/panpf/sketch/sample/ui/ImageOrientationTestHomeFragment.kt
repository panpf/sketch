package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.assemblyadapter.pager2.ArrayFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import me.panpf.sketch.sample.base.ToolbarBindingFragment
import me.panpf.sketch.sample.databinding.FragmentPager2TabBinding
import me.panpf.sketch.sample.vm.ImageOrientationTestHomeViewModel

class ImageOrientationTestHomeFragment : ToolbarBindingFragment<FragmentPager2TabBinding>() {

    private val viewModel by viewModels<ImageOrientationTestHomeViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentPager2TabBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentPager2TabBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Image Orientation Test"

        viewModel.listData.observe(viewLifecycleOwner) { list ->
            list ?: return@observe
            val titles = list.map { it.title }
            val fragments = list.map {
                ImageOrientationTestFragment().apply {
                    arguments = ImageOrientationTestFragmentArgs(it.imageUri).toBundle()
                }
            }
            binding.tabPagerPager.adapter = ArrayFragmentStateAdapter(this, fragments)

            TabLayoutMediator(binding.tabPagerTabLayout, binding.tabPagerPager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }
}
