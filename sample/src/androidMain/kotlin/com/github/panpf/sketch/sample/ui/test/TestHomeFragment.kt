package com.github.panpf.sketch.sample.ui.test

import android.Manifest.permission
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.github.panpf.assemblyadapter.recycler.AssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.AssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.model.Link
import com.github.panpf.sketch.sample.model.ListSeparator
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.ProjectInfoItemFactory
import com.github.panpf.sketch.sample.ui.common.list.TestGroupItemFactory
import com.github.panpf.sketch.sample.ui.common.list.TestItemItemFactory
import com.github.panpf.tools4a.dimen.ktx.dp2px

class TestHomeFragment : BaseBindingFragment<FragmentRecyclerBinding>() {

    private var pendingStartLink: Link? = null
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap ->
            val pendingStartLink = pendingStartLink ?: return@registerForActivityResult
            this@TestHomeFragment.pendingStartLink = null
            requestLinkPermissionsResult(grantedMap, pendingStartLink)
        }

    override fun onViewCreated(
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        binding.recycler.apply {
            setPadding(0, 0, 0, 80.dp2px)
            clipToPadding = false

            layoutManager = AssemblyGridLayoutManager.Builder(requireContext(), 2).apply {
                itemSpanByItemFactory(
                    ProjectInfoItemFactory::class to ItemSpan.fullSpan(),
                    TestGroupItemFactory::class to ItemSpan.fullSpan(),
                )
            }.build()
            adapter = AssemblyRecyclerAdapter(
                itemFactoryList = listOf(
                    TestItemItemFactory()
                        .setOnItemClickListener { _, _, _, _, data ->
                            startLink(data)
                        },
                    ProjectInfoItemFactory(),
                    TestGroupItemFactory(),
                ),
                initDataList = pageList()
            )
            addItemDecoration(AssemblyGridDividerItemDecoration.Builder(requireContext()).apply {
                divider(Divider.space(16.dp2px))
                sideDivider(Divider.space(16.dp2px))
                useDividerAsHeaderAndFooterDivider()
                useSideDividerAsSideHeaderAndFooterDivider()
            }.build())
        }
    }

    private fun pageList(): List<Any> = listOf(
        ListSeparator("Components"),
        Link(
            title = "Decoder",
            navDirections = NavMainDirections.actionDecoderTestPagerFragment()
        ),
        Link(
            title = "Fetcher",
            navDirections = NavMainDirections.actionFetcherTestFragment(),
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),

        ListSeparator("Functions"),
        Link(
            title = "AnimatedImage",
            navDirections = NavMainDirections.actionAnimatedImageTestFragment()
        ),
        Link(
            title = "ExifOrientation",
            navDirections = NavMainDirections.actionExifOrientationTestPagerFragment()
        ),
        Link(
            title = "Transformation",
            navDirections = NavMainDirections.actionTransformationTestPagerFragment()
        ),

        ListSeparator("UI"),
        Link(
            title = "CrossfadeDrawable",
            navDirections = NavMainDirections.actionCrossfadeDrawableTestFragment()
        ),
        Link(
            title = "ResizeDrawable",
            navDirections = NavMainDirections.actionResizeDrawableTestFragment()
        ),
        Link(
            title = "Drawable Mix",
            navDirections = NavMainDirections.actionMixDrawableTestFragment()
        ),
        Link(
            title = "IconDrawable",
            navDirections = NavMainDirections.actionIconDrawableTestFragment()
        ),
        Link(
            title = "AnimatablePlaceholder",
            navDirections = NavMainDirections.actionAnimatablePlaceholderTestViewFragment(),
        ),
        Link(
            title = "ProgressIndicator",
            navDirections = NavMainDirections.actionProgressIndicatorTestViewFragment()
        ),
        Link(
            title = "RemoteViews",
            navDirections = NavMainDirections.actionRemoteViewsFragment()
        ),
        Link(
            title = "Blurhash",
            navDirections = NavMainDirections.actionBlurhashFragment()
        ),

        ListSeparator("Other"),
        Link(
            title = "DisplayInsanity",
            navDirections = NavMainDirections.actionInsanityTestViewFragment()
        ),
        Link(
            title = "Local Videos",
            navDirections = NavMainDirections.actionLocalVideoListFragment(),
            permissions = listOf(permission.READ_EXTERNAL_STORAGE)
        ),
//        Link(
//            title = "ShareElement",
//            navDirections = NavMainDirections.actionShareElementTestFragment(),
//        ),
        Link(
            title = "Android Temp",
            navDirections = NavMainDirections.actionTempTestFragment()
        ),
        "ProjectInfo"
    )

    private fun startLink(data: Link) {
        if (data.minSdk == null || Build.VERSION.SDK_INT >= data.minSdk) {
            val permissions = data.permissions
            if (permissions != null) {
                pendingStartLink = data
                permissionLauncher.launch(permissions.toTypedArray())
            } else {
                findNavController().navigate(data.navDirections)
            }
        } else {
            Toast.makeText(
                context,
                "Must be API ${data.minSdk} or above",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun requestLinkPermissionsResult(grantedMap: Map<String, Boolean>, data: Link) {
        if (grantedMap.values.all { it }) {
            findNavController().navigate(data.navDirections)
        } else {
            Toast.makeText(context, "Please grant permission", Toast.LENGTH_LONG).show()
        }
    }
}