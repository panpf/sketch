package com.github.panpf.sketch.sample.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentMainBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.common.link.LinkItemFactory
import com.github.panpf.sketch.sample.ui.common.list.ListSeparatorItemFactory

class MainFragment : ToolbarBindingFragment<FragmentMainBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, parent, false)

    override fun onInitViews(
        toolbar: Toolbar,
        binding: FragmentMainBinding,
        savedInstanceState: Bundle?
    ) {
        super.onInitViews(toolbar, binding, savedInstanceState)
        toolbar.menu.add(0, 0, 0, "Settings").apply {
            setIcon(R.drawable.ic_settings)
            setOnMenuItemClickListener {
                findNavController().navigate(NavMainDirections.actionGlobalSettingsFragment())
                true
            }
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
    }

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentMainBinding,
        savedInstanceState: Bundle?
    ) {
        binding.mainRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AssemblyRecyclerAdapter(
                listOf(LinkItemFactory(), ListSeparatorItemFactory()),
                listOf(
                    ListSeparator("Samples"),
                    Link(
                        "Pexels Photos",
                        NavMainDirections.actionGlobalPexelsPhotoListFragment()
                    ),
                    Link(
                        "Pexels Photos（Compose）",
                        NavMainDirections.actionGlobalPexelsPhotoListComposeFragment(),
                        Build.VERSION_CODES.LOLLIPOP
                    ),
                    Link("Giphy GIF", NavMainDirections.actionGlobalGiphyGifListFragment()),
                    Link(
                        "Giphy GIF（Compose）",
                        NavMainDirections.actionGlobalGiphyGifListComposeFragment(),
                        Build.VERSION_CODES.LOLLIPOP
                    ),
                    Link("Local Photos", NavMainDirections.actionGlobalLocalPhotoListFragment()),
                    Link("Local Video", NavMainDirections.actionGlobalLocalVideoListFragment()),
                    Link("Huge Image", NavMainDirections.actionGlobalHugeImageListFragment()),

                    ListSeparator("Test"),
                    Link("Image Format", NavMainDirections.actionGlobalImageFormatTestFragment()),
                    Link("Fetcher", NavMainDirections.actionGlobalFetcherTestFragment()),
                    // todo 增加更多的示例
//                    Link(
//                        "ImageProcessor Test",
//                        NavMainDirections.actionGlobalImageProcessorTestFragment()
//                    ),
//                    Link(
//                        "ImageShaper Test",
//                        NavMainDirections.actionGlobalImageShaperTestFragment()
//                    ),
//                    Link(
//                        "Repeat Load Or Download Test",
//                        NavMainDirections.actionGlobalRepeatLoadOrDownloadTestFragment()
//                    ),
//                    Link("inBitmap Test", NavMainDirections.actionGlobalInBitmapTestFragment()),
//                    Link(
//                        "Image Orientation Test",
//                        NavMainDirections.actionGlobalImageOrientationTestHomeFragment()
//                    ),
//                    Link("Other Test", NavMainDirections.actionGlobalOtherTestFragment()),
                    Link("Other Test", NavMainDirections.actionGlobalTestFragment()),
//
//                    ListSeparator("App"),
//                    Link("Settings", NavMainDirections.actionGlobalSettingsFragment()),
//                    Link("About", NavMainDirections.actionGlobalAboutFragment()),
                )
            )
        }
    }
}