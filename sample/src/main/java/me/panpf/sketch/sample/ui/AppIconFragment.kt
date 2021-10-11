package me.panpf.sketch.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.panpf.assemblyadapter.recycler.AssemblyRecyclerAdapter
import com.github.panpf.assemblyadapter.recycler.AssemblySingleDataRecyclerAdapter
import me.panpf.sketch.sample.base.BaseToolbarFragment
import me.panpf.sketch.sample.databinding.FragmentRecyclerBinding
import me.panpf.sketch.sample.item.AppItemFactory
import me.panpf.sketch.sample.item.AppsOverviewItemFactory
import me.panpf.sketch.sample.item.ListSeparatorItemFactory
import me.panpf.sketch.sample.util.ScrollingPauseLoadManager
import me.panpf.sketch.sample.vm.PinyinFlatAppsViewModel

class AppIconFragment : BaseToolbarFragment<FragmentRecyclerBinding>() {

    private val viewModel by viewModels<PinyinFlatAppsViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentRecyclerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentRecyclerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "App Icon"

        val appsOverviewAdapter =
            AssemblySingleDataRecyclerAdapter(AppsOverviewItemFactory())
        val listAdapter = AssemblyRecyclerAdapter<Any>(
            listOf(AppItemFactory(), ListSeparatorItemFactory())
        )

        binding.recyclerRecyclerFragmentContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ConcatAdapter(appsOverviewAdapter, listAdapter)
            addOnScrollListener(ScrollingPauseLoadManager(requireContext()))
        }

        binding.refreshRecyclerFragment.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.loadingData.observe(viewLifecycleOwner) {
            binding.hintRecyclerFragment.hidden()
            binding.refreshRecyclerFragment.isRefreshing = it == true
        }

        viewModel.appsOverviewData.observe(viewLifecycleOwner) {
            appsOverviewAdapter.data = it
        }

        viewModel.pinyinFlatAppListData.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
    }
}
