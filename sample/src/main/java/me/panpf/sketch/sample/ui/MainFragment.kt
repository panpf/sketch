package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.recycler.sticky.sample.bean.ListSeparator
import me.panpf.sketch.sample.NavMainDirections
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.bean.Link
import me.panpf.sketch.sample.databinding.FragmentMainBinding
import me.panpf.sketch.sample.item.LinkItemFactory
import me.panpf.sketch.sample.item.ListSeparatorItemFactory

class MainFragment : BaseToolbarFragment<FragmentMainBinding>() {

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
                    Link("Local Photos", NavMainDirections.actionGlobalLocalPhotosFragment()),
                    Link("Online Photos", NavMainDirections.actionGlobalOnlinePhotosFragment()),
                    Link("Online GIF", NavMainDirections.actionGlobalOnlineGifFragment()),
                    Link(
                        "Huge Image",
                        NavMainDirections.actionGlobalHugeImageFragment()
                    ),
                    Link("Video Thumbnail", NavMainDirections.actionGlobalVideoThumbnailFragment()),
                    Link("App Icon", NavMainDirections.actionGlobalAppIconFragment()),
                    Link("Apk Icon", NavMainDirections.actionGlobalApkIconFragment()),
                    Link(
                        "Base64 Image",
                        NavMainDirections.actionGlobalBase64ImageFragment()
                    ),

                    ListSeparator("Test"),
                    Link(
                        "Image Processor Test",
                        NavMainDirections.actionGlobalImageProcessorTestFragment()
                    ),
                    Link(
                        "Image Shaper Test",
                        NavMainDirections.actionGlobalImageShaperTestFragment()
                    ),
                    Link(
                        "Repeat Load Or Download Test",
                        NavMainDirections.actionGlobalRepeatLoadOrDownloadTestFragment()
                    ),
                    Link("inBitmap Test", NavMainDirections.actionGlobalInBitmapTestFragment()),
                    Link(
                        "Image Orientation Test",
                        NavMainDirections.actionGlobalImageOrientationTestHomeFragment()
                    ),
                    Link("Other Test", NavMainDirections.actionGlobalOtherTestFragment()),

                    ListSeparator("App"),
                    Link("Settings", NavMainDirections.actionGlobalSettingsFragment()),
                    Link("About", NavMainDirections.actionGlobalAboutFragment()),
                )
            )
        }
    }
}