package com.github.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.bean.Link
import com.github.panpf.sketch.sample.bean.ListSeparator
import com.github.panpf.sketch.sample.databinding.FragmentMainBinding
import com.github.panpf.sketch.sample.item.LinkItemFactory
import com.github.panpf.sketch.sample.item.ListSeparatorItemFactory

class MainFragment : ToolbarBindingFragment<FragmentMainBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        FragmentMainBinding.inflate(inflater, parent, false)

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
                        NavMainDirections.actionGlobalPexelsPhotosFragment()
                    ),
//                    Link("Giphy GIF - Giphy", NavMainDirections.actionGlobalOnlineGifFragment()),
                    Link("Local Photos", NavMainDirections.actionGlobalLocalPhotosFragment()),
//                    Link("Local Video", NavMainDirections.actionGlobalLocalVideoFragment()),
//                    Link("Huge Image", NavMainDirections.actionGlobalHugeImageFragment()),
//
                    ListSeparator("Test"),
                    Link("Image Format", NavMainDirections.actionGlobalImageFormatFragment()),
                    Link("Fetcher", NavMainDirections.actionGlobalFetcherFragment()),
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
//
//                    ListSeparator("App"),
//                    Link("Settings", NavMainDirections.actionGlobalSettingsFragment()),
//                    Link("About", NavMainDirections.actionGlobalAboutFragment()),
                )
            )
        }
    }
}