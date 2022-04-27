package com.github.panpf.sketch.sample.ui.huge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.databinding.FragmentContainerBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.huge.Layout.COLUMN

class HugeImageListFragment : ToolbarBindingFragment<FragmentContainerBinding>() {

    private val viewModel by viewModels<HugeImageListViewModel>()

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ) = FragmentContainerBinding.inflate(inflater, parent, false)

    override fun onInitData(
        toolbar: Toolbar,
        binding: FragmentContainerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Huge Image"

        val menu = toolbar.menu.add("Layout").apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            setOnMenuItemClickListener {
                val newLayout = if (viewModel.layoutData.value == COLUMN) {
                    Layout.ROW
                } else {
                    COLUMN
                }
                viewModel.changeTab(newLayout)
                true
            }
        }

        viewModel.layoutData.observe(viewLifecycleOwner) {
            val meuIcon = if (it == COLUMN) {
                R.drawable.ic_layout_row
            } else {
                R.drawable.ic_layout_column
            }
            menu.setIcon(meuIcon)

            val fragment = if (it == COLUMN) {
                HugeImageHorListFragment()
            } else {
                HugeImageVerListFragment()
            }
            childFragmentManager.beginTransaction()
                .replace(binding.containerFragmentContainer.id, fragment)
                .commit()
        }
    }
}