package com.github.panpf.sketch.sample.ui.test

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.github.panpf.assemblyadapter.recycler.AssemblyGridLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.ItemSpan
import com.github.panpf.assemblyadapter.recycler.divider.AssemblyGridDividerItemDecoration
import com.github.panpf.assemblyadapter.recycler.divider.Divider
import com.github.panpf.sketch.sample.NavMainDirections
import com.github.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import com.github.panpf.sketch.sample.ui.base.BaseBindingFragment
import com.github.panpf.sketch.sample.ui.common.list.ProjectInfoItemFactory
import com.github.panpf.sketch.sample.ui.common.list.TestGroupItemFactory
import com.github.panpf.sketch.sample.ui.common.list.TestItemItemFactory
import com.github.panpf.sketch.sample.ui.common.list.ViewTestGroup
import com.github.panpf.sketch.sample.ui.model.ViewTestItem
import com.github.panpf.tools4a.dimen.ktx.dp2px

class TestHomeFragment : BaseBindingFragment<FragmentRecyclerBinding>() {

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
                            this@TestHomeFragment.findNavController().navigate(data.navDirections)
                        },
                    TestGroupItemFactory(),
                    ProjectInfoItemFactory(),
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
        ViewTestGroup("Components"),
        ViewTestItem(
            title = "Decoder",
            navDirections = NavMainDirections.actionDecoderTestPagerFragment()
        ),
        ViewTestItem(
            title = "Fetcher",
            navDirections = NavMainDirections.actionFetcherTestFragment(),
        ),

        ViewTestGroup("Functions"),
        ViewTestItem(
            title = "AnimatedImage",
            navDirections = NavMainDirections.actionAnimatedImageTestFragment()
        ),
        ViewTestItem(
            title = "ExifOrientation",
            navDirections = NavMainDirections.actionExifOrientationTestPagerFragment()
        ),
        ViewTestItem(
            title = "Transformation",
            navDirections = NavMainDirections.actionTransformationTestPagerFragment()
        ),
        ViewTestItem(
            title = "Progress",
            navDirections = NavMainDirections.actionProgressTestFragment()
        ),

        ViewTestGroup("UI"),
        ViewTestItem(
            title = "CrossfadeDrawable",
            navDirections = NavMainDirections.actionCrossfadeDrawableTestFragment()
        ),
        ViewTestItem(
            title = "ResizeDrawable",
            navDirections = NavMainDirections.actionResizeDrawableTestFragment()
        ),
        ViewTestItem(
            title = "Drawable Mix",
            navDirections = NavMainDirections.actionMixDrawableTestFragment()
        ),
        ViewTestItem(
            title = "IconDrawable",
            navDirections = NavMainDirections.actionIconDrawableTestFragment()
        ),
        ViewTestItem(
            title = "AnimatablePlaceholder",
            navDirections = NavMainDirections.actionAnimatablePlaceholderTestViewFragment(),
        ),
        ViewTestItem(
            title = "ProgressIndicator",
            navDirections = NavMainDirections.actionProgressIndicatorTestViewFragment()
        ),
        ViewTestItem(
            title = "RemoteViews",
            navDirections = NavMainDirections.actionRemoteViewsFragment()
        ),
        ViewTestItem(
            title = "BlurHash",
            navDirections = NavMainDirections.actionBlurHashFragment()
        ),

        ViewTestGroup("Other"),
        ViewTestItem(
            title = "DisplayInsanity",
            navDirections = NavMainDirections.actionInsanityTestViewFragment()
        ),
        ViewTestItem(
            title = "Local Videos",
            navDirections = NavMainDirections.actionLocalVideoListFragment(),
        ),
//        Link(
//            title = "ShareElement",
//            navDirections = NavMainDirections.actionShareElementTestFragment(),
//        ),
        ViewTestItem(
            title = "Android Temp",
            navDirections = NavMainDirections.actionTempTestFragment()
        ),
        "ProjectInfo"
    )
}